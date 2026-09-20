package bg.tuvarna.mobile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
@ApplicationScoped public class DownstreamPolicy {
 @Inject @RestClient UnstableClient client;
 public Response load(String mode) { return client.get(mode); }
 // TODO L07-G: classify transient failures, timeout, circuit breaker и explicit fallback.
 // TODO L07-I: policy за отделния failure scenario; няма автоматичен retry по подразбиране.
}
