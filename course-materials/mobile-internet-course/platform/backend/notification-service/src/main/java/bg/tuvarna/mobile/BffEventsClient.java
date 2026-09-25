package bg.tuvarna.mobile;
import jakarta.ws.rs.*; import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
@Path("/internal/events") @RegisterRestClient(configKey="bff") public interface BffEventsClient { @POST void relay(String event); }

