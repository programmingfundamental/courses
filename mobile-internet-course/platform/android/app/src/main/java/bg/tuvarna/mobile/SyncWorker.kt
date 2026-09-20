package bg.tuvarna.mobile
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
class SyncWorker(context:Context,parameters:WorkerParameters):CoroutineWorker(context,parameters){
 override suspend fun doWork():Result = Result.failure()
 // TODO L08-G: bounded drain, status classification, unique work и network constraint.
 // TODO L08-I: stable operation key при всяко повторение; reconcile Sending след process death.
}

