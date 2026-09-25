package bg.tuvarna.mobile
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
sealed interface ApiResult<out T>{
 data class Success<T>(val value:T):ApiResult<T>
 data class Failure(val kind:String,val status:Int?=null):ApiResult<Nothing>
}
class ApiClient {
 val transport=OkHttpClient.Builder().connectTimeout(2,TimeUnit.SECONDS).readTimeout(3,TimeUnit.SECONDS).callTimeout(5,TimeUnit.SECONDS).retryOnConnectionFailure(false).build()
 suspend fun get(url:String):ApiResult<String> = ApiResult.Failure("TODO L02-G: cancellable request + classification")
 // TODO L02-I: bounded retry policy за GET; cancellation никога не се преобразува във Failure.
}

