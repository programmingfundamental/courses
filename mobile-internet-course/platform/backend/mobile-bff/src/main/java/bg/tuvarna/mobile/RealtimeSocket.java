package bg.tuvarna.mobile;
import io.quarkus.websockets.next.*;
@WebSocket(path="/realtime") public class RealtimeSocket {
 @OnOpen public java.util.Map<String,String> open(){return java.util.Map.of("type","ready");}
 @OnTextMessage public java.util.Map<String,String> message(String text){return java.util.Map.of("type","pong");}
 // TODO L10-G: authenticated handshake и per-user routing; demo feed е само synthetic.
}
