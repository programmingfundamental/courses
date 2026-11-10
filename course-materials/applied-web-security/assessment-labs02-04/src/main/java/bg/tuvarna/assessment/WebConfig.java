package bg.tuvarna.assessment;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
class StaticController {
    private static final Map<String, String> TYPES = Map.of(
        "/", "text/html", "/app.js", "text/javascript", "/style.css", "text/css", "/assignment.json", "application/json", "/favicon.svg", "image/svg+xml");
    @GetMapping({"/", "/app.js", "/style.css", "/assignment.json", "/favicon.svg"})
    ResponseEntity<ClassPathResource> asset(HttpServletRequest req) {
        String path = req.getRequestURI();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, TYPES.get(path) + ";charset=UTF-8")
            .body(new ClassPathResource("static/" + (path.equals("/") ? "index.html" : path.substring(1))));
    }
}

@Component
class WebHeaders extends OncePerRequestFilter {
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        res.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self'; connect-src 'self'; object-src 'none'; base-uri 'none'; frame-ancestors 'none'; form-action 'self'");
        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("Referrer-Policy", "no-referrer");
        res.setHeader("Cache-Control", "no-store");
        if (req.getRequestURI().startsWith("/api/") && !req.getMethod().equals("GET")) {
            String origin = req.getHeader("Origin"), expected = req.getScheme() + "://" + req.getHeader("Host");
            if ((origin != null && !origin.equals(expected)) || "cross-site".equals(req.getHeader("Sec-Fetch-Site"))) {
                fail(res, 403, "Заявката трябва да е от същия сайт."); return;
            }
            if (req.getContentType() == null || !req.getContentType().toLowerCase(java.util.Locale.ROOT).startsWith("application/json")) {
                fail(res, 415, "Използвайте application/json."); return;
            }
            byte[] bytes = req.getInputStream().readNBytes(65537);
            if (bytes.length > 65536) { fail(res, 413, "Заявката е твърде голяма."); return; }
            var wrapped = new HttpServletRequestWrapper(req) {
                @Override public ServletInputStream getInputStream() {
                    ByteArrayInputStream input = new ByteArrayInputStream(bytes);
                    return new ServletInputStream() {
                        public int read() { return input.read(); }
                        public boolean isFinished() { return input.available() == 0; }
                        public boolean isReady() { return true; }
                        public void setReadListener(ReadListener listener) { throw new UnsupportedOperationException(); }
                    };
                }
                @Override public BufferedReader getReader() { return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8)); }
            };
            chain.doFilter(wrapped, res); return;
        }
        chain.doFilter(req, res);
    }
    private void fail(HttpServletResponse res, int status, String message) throws IOException {
        res.setStatus(status); res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
