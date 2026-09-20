package bg.tuvarna.mobile;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.util.UUID;
import org.jboss.logging.Logger;

@Provider
public class CorrelationFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOG = Logger.getLogger(CorrelationFilter.class);

    @Override
    public void filter(ContainerRequestContext request) {
        String id = request.getHeaderString("X-Request-ID");
        if (id == null || !id.matches("[a-zA-Z0-9-]{1,64}")) {
            id = UUID.randomUUID().toString();
        }
        request.setProperty("requestId", id);
        request.setProperty("startNanos", System.nanoTime());
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        // Authentication may abort before the request filter runs.
        Object id = request.getProperty("requestId");
        if (id == null) id = UUID.randomUUID().toString();
        response.getHeaders().putSingle("X-Request-ID", id);
        Object started = request.getProperty("startNanos");
        double durationMs = started instanceof Long nanos
                ? (System.nanoTime() - nanos) / 1_000_000.0 : -1.0;
        LOG.infof("requestId=%s method=%s status=%d durationMs=%.3f",
                id, request.getMethod(), response.getStatus(), durationMs);
    }
}
