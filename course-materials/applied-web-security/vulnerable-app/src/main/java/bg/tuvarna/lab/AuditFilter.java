package bg.tuvarna.lab;

import java.io.IOException;
import java.util.UUID;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.*;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

@Component @Order(-200)
public class AuditFilter extends OncePerRequestFilter {
    private static final Logger log=LoggerFactory.getLogger(AuditFilter.class);
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException,IOException {
        String id=UUID.randomUUID().toString(); response.setHeader("X-Correlation-ID",id);
        try { chain.doFilter(request,response); }
        finally { log.info("security_event correlation={} method={} status={}",id,request.getMethod(),response.getStatus()); }
    }
}
