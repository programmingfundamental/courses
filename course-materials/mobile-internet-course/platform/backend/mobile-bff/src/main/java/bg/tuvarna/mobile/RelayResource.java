package bg.tuvarna.mobile;
import jakarta.ws.rs.*; import jakarta.inject.Inject; import jakarta.ws.rs.core.Response;
import io.quarkus.websockets.next.OpenConnections;
@Path("/internal/events") public class RelayResource {
 @Inject OpenConnections connections;
 @POST public Response relay(String json){
  connections.listAll().forEach(c->c.sendTextAndAwait(json)); return Response.accepted().build();
 }
 // Infrastructure-only endpoint: gateway не го route-ва. L10: service identity boundary.
}
