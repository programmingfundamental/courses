package bg.tuvarna.mobile;
import jakarta.enterprise.context.ApplicationScoped; import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming; import org.eclipse.microprofile.rest.client.inject.RestClient;
import io.smallrye.common.annotation.Blocking;
@ApplicationScoped public class EventConsumer {
 @Inject @RestClient BffEventsClient bff;
 @Incoming("activity-events-in") @Blocking public void accept(String event){bff.relay(event);}
 // Transport scaffold: processing failure propagates to connector. No exactly-once claim.
}
