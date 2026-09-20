package bg.tuvarna.mobile;
import jakarta.ws.rs.*; import java.util.Map; import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
@Path("/users") @RegisterRestClient(configKey="users") public interface UsersClient {
 @GET @Path("/{id}") Map<String,Object> get(@PathParam("id") String id,@HeaderParam("X-Request-ID") String requestId,@HeaderParam("Authorization") String authorization);
}
