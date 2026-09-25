package bg.tuvarna.mobile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*

sealed interface SocketEvent {
 data object Open : SocketEvent
 data class Text(val text:String) : SocketEvent
 data class Closed(val code:Int) : SocketEvent
 data class Failed(val reason:String) : SocketEvent
}
class RealtimeSource(private val client:OkHttpClient) {
 fun events(url:String) = callbackFlow {
  val listener=object:WebSocketListener(){
   override fun onOpen(webSocket:WebSocket,response:Response){trySend(SocketEvent.Open)}
   override fun onMessage(webSocket:WebSocket,text:String){
    if(text.length>65536 || trySend(SocketEvent.Text(text)).isFailure){webSocket.cancel();close(IllegalStateException("Realtime input overflow"))}
   }
   override fun onClosing(webSocket:WebSocket,code:Int,reason:String){webSocket.close(code,null)}
   override fun onClosed(webSocket:WebSocket,code:Int,reason:String){trySend(SocketEvent.Closed(code));close()}
   override fun onFailure(webSocket:WebSocket,t:Throwable,response:Response?){trySend(SocketEvent.Failed(t.javaClass.simpleName));close(t)}
  }
  val socket=client.newWebSocket(Request.Builder().url(url).build(),listener)
  awaitClose{socket.cancel()}
 }
 // TODO L09-G: lifecycle-owned collection и DTO decoding; control frames != ActivityEvent.
 // TODO L09-I: reconnect/backoff + snapshot reconciliation след gap.
 // TODO L10-G: Bearer header от in-memory session, никога token в query string.
}
