package bg.tuvarna.mobile
import kotlinx.serialization.Serializable
@Serializable data class ActivityEvent(val eventId:String,val activityId:String,val userId:String,val version:Long,val status:String,val occurredAt:String)
interface EventPolicy { fun accept(currentVersion:Long,event:ActivityEvent):Boolean }
class StudentEventPolicy:EventPolicy {
 override fun accept(currentVersion:Long,event:ActivityEvent):Boolean = false
 // TODO L09-I: duplicate/stale policy с durable version; няма готово решение.
}

