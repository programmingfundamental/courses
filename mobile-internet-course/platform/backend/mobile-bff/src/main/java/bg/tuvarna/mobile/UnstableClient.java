package bg.tuvarna.mobile;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
@Path("/unstable") @RegisterRestClient(configKey="notifications") public interface UnstableClient {
 @GET Response get(@QueryParam("mode") String mode);
}
