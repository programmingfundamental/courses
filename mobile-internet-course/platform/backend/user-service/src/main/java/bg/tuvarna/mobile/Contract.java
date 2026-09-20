package bg.tuvarna.mobile;
import jakarta.ws.rs.core.Response;
public final class Contract {
 private Contract() {}
 public record Problem(String code,String message,String requestId) {}
 public static Response todo(String task) { return Response.status(501).entity(new Problem("LAB_TODO",task,"see X-Request-ID")).build(); }
}
