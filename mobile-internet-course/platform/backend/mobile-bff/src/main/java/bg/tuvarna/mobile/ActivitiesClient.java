package bg.tuvarna.mobile;
import jakarta.ws.rs.*; import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
@Path("/activities") @RegisterRestClient(configKey="activities") public interface ActivitiesClient {
 @GET Response recent(@HeaderParam("X-Request-ID") String requestId,@HeaderParam("Authorization") String authorization);
 @POST Response create(String body,@HeaderParam("Idempotency-Key") String key,@HeaderParam("X-Request-ID") String requestId,@HeaderParam("Authorization") String authorization);
}
