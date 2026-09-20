package bg.tuvarna.mobile
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Entity(tableName="local_activities") data class LocalActivity(@PrimaryKey val localId:String,val title:String,val serverId:String?=null,val syncState:String="Pending",val version:Long=0)
@Entity(tableName="pending_ops") data class PendingOp(@PrimaryKey val operationId:String,val localId:String,val payload:String,val attempt:Int=0,val state:String="Pending")
@Dao interface LocalDao {
 @Query("SELECT * FROM local_activities ORDER BY localId LIMIT 100") fun observe():Flow<List<LocalActivity>>
 @Insert(onConflict=OnConflictStrategy.ABORT) suspend fun insert(item:LocalActivity)
 @Insert(onConflict=OnConflictStrategy.ABORT) suspend fun enqueue(op:PendingOp)
 @Query("SELECT * FROM pending_ops WHERE state='Pending' ORDER BY operationId LIMIT 20") suspend fun pending():List<PendingOp>
 @Query("UPDATE pending_ops SET state=:state,attempt=:attempt WHERE operationId=:id") suspend fun updateOperation(id:String,state:String,attempt:Int)
 @Query("UPDATE local_activities SET serverId=:serverId,syncState=:state WHERE localId=:id") suspend fun updateActivity(id:String,serverId:String?,state:String)
}
@Database(entities=[LocalActivity::class,PendingOp::class],version=1,exportSchema=false) abstract class LocalDatabase:RoomDatabase(){abstract fun dao():LocalDao}
object DatabaseProvider {
 @Volatile private var instance:LocalDatabase?=null
 fun get(context:android.content.Context):LocalDatabase = instance ?: synchronized(this) {
  instance ?: Room.databaseBuilder(context.applicationContext,LocalDatabase::class.java,"mobile-activity.db").build().also{instance=it}
 }
}
// TODO L08-G: app-scoped Room instance и transaction create+enqueue; explicit sync transitions.
