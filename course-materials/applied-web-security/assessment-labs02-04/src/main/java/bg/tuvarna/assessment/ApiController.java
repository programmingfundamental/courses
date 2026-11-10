package bg.tuvarna.assessment;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@RestController
public class ApiController {
    private final LabStore store;
    public ApiController(LabStore store) { this.store = store; }
    private static class Refusal extends RuntimeException {
        final int status;
        Refusal(int status, String message) { super(message); this.status = status; }
    }
    private static void require(boolean condition, int status, String message) { if (!condition) throw new Refusal(status, message); }
    private static String cookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) for (Cookie c : request.getCookies()) if (c.getName().equals(name)) return c.getValue();
        return null;
    }
    private static void setCookie(HttpServletRequest req, HttpServletResponse res, String name, String token, String path, long seconds) {
        res.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(name, token).path(path).httpOnly(true).sameSite("Strict").secure(req.isSecure()).maxAge(Duration.ofSeconds(seconds)).build().toString());
    }
    private String workspace(HttpServletRequest req) {
        String token = cookie(req, "assessment_workspace");
        String id = token == null ? "" : LabStore.digest(token);
        require(store.one("SELECT id FROM workspaces WHERE id=?", id) != null, 401, "Отворете началния екран, за да създадете работно място.");
        return id;
    }
    @GetMapping("/health") public Map<String, String> health() {
        store.one("SELECT 1 AS ok"); return Map.of("status", "UP");
    }
    @GetMapping("/api/workspace")
    public Map<String, Object> bootstrap(HttpServletRequest req, HttpServletResponse res) {
        String token = cookie(req, "assessment_workspace");
        String id = token == null ? "" : LabStore.digest(token);
        if (store.one("SELECT id FROM workspaces WHERE id=?", id) == null) {
            token = store.createWorkspace(); id = LabStore.digest(token);
            setCookie(req, res, "assessment_workspace", token, "/", 30 * 24 * 3600L);
        }
        synchronized (store.lock(id)) { return snapshot(id); }
    }
    private Map<String, Object> snapshot(String id) {
        Map<String, Object> w = store.one("SELECT created,participant,answers FROM workspaces WHERE id=?", id);
        return Map.of("code", id.substring(0, 10).toUpperCase(Locale.ROOT), "created", w.get("created"),
            "participant", w.get("participant"), "answers", store.parse((String)w.get("answers")),
            "runs", store.all("SELECT case_no AS caseNumber,generation FROM lab_runs WHERE workspace=? ORDER BY case_no", id),
            "evidence", store.evidence(id));
    }
    @PutMapping("/api/answers")
    public Map<String, Object> saveAnswers(@RequestBody Map<String, Object> body, HttpServletRequest req) {
        String workspace = workspace(req);
        String participant = string(body, "participant", 0, 120);
        require(body.get("answers") instanceof Map<?, ?>, 400, "Липсват отговорите.");
        Map<String, Object> answers = new LinkedHashMap<>();
        for (var item : ((Map<?, ?>)body.get("answers")).entrySet()) {
            require(item.getKey().toString().matches("[1-5]"), 400, "Непозната проверка.");
            require(item.getValue() instanceof String text && text.length() <= 5000, 400, "Всеки отговор може да е до 5000 символа.");
            answers.put(item.getKey().toString(), item.getValue());
        }
        synchronized (store.lock(workspace)) {
            store.update("UPDATE workspaces SET participant=?,answers=? WHERE id=?", participant, store.json(answers), workspace);
        }
        return Map.of("saved", true, "at", Instant.now().toString());
    }
    @GetMapping("/api/export")
    public ResponseEntity<Map<String, Object>> export(HttpServletRequest req) {
        String workspace = workspace(req);
        synchronized (store.lock(workspace)) {
            Map<String, Object> data = new LinkedHashMap<>(snapshot(workspace));
            data.put("format", "web-security-assessment-v1"); data.put("exportedAt", Instant.now().toString());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=assessment-" + workspace.substring(0, 10) + ".json").body(data);
        }
    }
    private class Context {
        final String workspace;
        final int n;
        final HttpServletRequest req;
        final HttpServletResponse res;
        LabStore.Session session;
        Context(String workspace, int n, HttpServletRequest req, HttpServletResponse res) {
            this.workspace = workspace; this.n = n; this.req = req; this.res = res;
            session = store.session(cookie(req, "assessment_s" + n), workspace, n);
        }
        String username() {
            String auth = req.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) return store.bearerUser(auth.substring(7), workspace, n);
            return session == null ? null : session.username();
        }
        Map<String, Object> principal() {
            String name = username();
            require(name != null, 401, "Необходимо е удостоверяване.");
            Map<String, Object> user = store.user(workspace, n, name);
            require(user != null, 401, "Необходимо е удостоверяване.");
            return user;
        }
        void rotate(String username) {
            var issued = store.rotate(session, workspace, n, username); session = issued.session();
            setCookie(req, res, "assessment_s" + n, issued.token(), "/api/cases/" + n, 8 * 3600);
        }
        void clearCookie() { setCookie(req, res, "assessment_s" + n, "", "/api/cases/" + n, 0); session = null; }
    }
    @RequestMapping("/api/cases/{n}/**")
    public ResponseEntity<Object> execute(@PathVariable int n, @RequestBody(required=false) String raw, HttpServletRequest req, HttpServletResponse res) {
        require(n >= 1 && n <= 5, 404, "Няма такава проверка.");
        String workspace = workspace(req);
        synchronized (store.lock(workspace)) {
            Context c = new Context(workspace, n, req, res);
            String path = req.getRequestURI().substring(("/api/cases/" + n).length());
            String before = LabStore.label(c.session), identityBefore = Objects.toString(c.username(), "anonymous");
            int status = 200; Object data; Map<String, Object> body = Map.of();
            try {
                require(req.getQueryString() == null, 400, "Използвайте път без query параметри.");
                if (raw != null && !raw.isBlank()) body = store.parse(raw);
                data = route(c, req.getMethod(), path, body);
            } catch (Refusal e) { status = e.status; data = Map.of("message", e.getMessage()); }
              catch (IllegalArgumentException e) { status = 400; data = Map.of("message", "Невалиден JSON обект."); }
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("at", Instant.now().toString()); entry.put("method", req.getMethod()); entry.put("path", path);
            entry.put("status", status); entry.put("request", body); entry.put("response", data);
            entry.put("sessionBefore", before); entry.put("sessionAfter", LabStore.label(c.session));
            entry.put("identityBefore", identityBefore); entry.put("identityAfter", Objects.toString(c.username(), "anonymous"));
            long evidence = store.record(workspace, n, entry);
            return ResponseEntity.status(status).header("X-Evidence-Id", Long.toString(evidence))
                .header("X-Lab-Session-Before", before.equals("—") ? "none" : before)
                .header("X-Lab-Session-After", c.session == null ? "none" : LabStore.label(c.session))
                .header("X-Lab-Identity", Objects.toString(c.username(), "anonymous")).body(data);
        }
    }
    private Object route(Context c, String method, String path, Map<String, Object> body) {
        String workspace = c.workspace; int n = c.n;
        if (method.equals("POST") && path.equals("/reset")) {
            store.reset(workspace, n); c.clearCookie(); return Map.of("message", "Началните данни са възстановени. Предишните доказателства и отговори са запазени.");
        }
        if (method.equals("GET") && path.equals("/auth/session")) {
            if (c.session == null) c.rotate(null);
            return Map.of("session", LabStore.label(c.session), "username", Objects.toString(c.session.username(), "anonymous"));
        }
        if (method.equals("POST") && path.equals("/auth/register")) {
            String username = string(body, "username", 3, 32), password = string(body, "password", 12, 64);
            require(username.matches("[a-z][a-z0-9_-]{2,31}"), 400, "Името трябва да започва с малка латинска буква и да съдържа само a–z, 0–9, _ или -.");
            require(password.getBytes(StandardCharsets.UTF_8).length <= 72, 400, "Паролата е над 72 UTF-8 байта.");
            require(store.user(workspace, n, username) == null, 409, "Името вече е заето.");
            Number count = (Number)store.one("SELECT COUNT(*) AS total FROM lab_users WHERE workspace=? AND case_no=?", workspace, n).get("total");
            require(count.intValue() < 50, 409, "Достигнат е лимитът за тази проверка. Възстановете началните данни.");
            store.update("INSERT INTO lab_users VALUES(?,?,?,?,?)", workspace, n, username, store.encode(password), "USER");
            return Map.of("username", username, "message", "Регистрацията е приета.");
        }
        if (method.equals("POST") && path.equals("/auth/login")) {
            String username = string(body, "username", 1, 32), password = string(body, "password", 1, 128);
            if (n == 2 && c.session != null && c.session.username() != null) {
                c.rotate(c.session.username());
            } else {
                require(password.getBytes(StandardCharsets.UTF_8).length <= 72, 401, "Невалидни данни за вход.");
                require(store.matches(password, store.user(workspace, n, username)), 401, "Невалидни данни за вход.");
                c.rotate(username);
            }
            Map<String, Object> response = new LinkedHashMap<>(store.issueTokens(c.session));
            response.put("username", c.session.username()); return response;
        }
        if (method.equals("POST") && path.equals("/auth/logout")) {
            if (c.session != null) store.dropSession(c.session.id()); c.clearCookie();
            return Map.of("message", "Излязохте от профила.");
        }
        if (method.equals("GET") && path.equals("/auth/me")) {
            Map<String, Object> user = c.principal(); return Map.of("username", user.get("username"), "role", user.get("role"));
        }
        if (method.equals("GET") && path.startsWith("/diagnostics/users/")) {
            Map<String, Object> user = c.principal();
            require(user.get("username").equals(path.substring("/diagnostics/users/".length())), 404, "Няма достъпен запис.");
            Map<String, Object> row = store.one("SELECT username,role FROM lab_users WHERE workspace=? AND case_no=? AND username=?", workspace, n, user.get("username"));
            return Map.of("source", "H2", "storedUser", row);
        }
        if (method.equals("GET") && path.equals("/tasks")) {
            Map<String, Object> user = c.principal();
            return isAdmin(user) ? store.all("SELECT id,summary,description,owner FROM tasks WHERE workspace=? AND case_no=? ORDER BY id", workspace, n)
                : store.all("SELECT id,summary,description,owner FROM tasks WHERE workspace=? AND case_no=? AND owner=? ORDER BY id", workspace, n, user.get("username"));
        }
        if (path.matches("/reports/task/[1-9][0-9]{0,8}") && method.equals("GET")) {
            Map<String, Object> user = c.principal();
            require(isAdmin(user), 403, "Достъпът е отказан.");
            int id = Integer.parseInt(path.substring("/reports/task/".length()));
            var row = store.one("SELECT id,task_id AS taskId,report_text AS text FROM reports WHERE workspace=? AND case_no=? AND task_id=?", workspace, n, id);
            require(row != null, 404, "Няма достъпен отчет."); return row;
        }
        if (path.matches("/tasks/[1-9][0-9]{0,8}(/update|/delete)?")) {
            Map<String, Object> user = c.principal(); int id = Integer.parseInt(path.split("/")[2]);
            Map<String, Object> task = store.one("SELECT id,summary,description,owner FROM tasks WHERE workspace=? AND case_no=? AND id=?", workspace, n, id);
            require(task != null, 404, "Няма достъпна задача.");
            if (method.equals("PATCH") && path.endsWith("/update")) {
                String summary = string(body, "summary", 1, 160);
                String description = body.containsKey("description") ? string(body, "description", 0, 2000) : (String)task.get("description");
                if (n == 4) store.update("UPDATE tasks SET summary=?,description=? WHERE workspace=? AND case_no=? AND id=?", summary, description, workspace, n, id);
                require(canRead(user, task), 404, "Няма достъпна задача.");
                if (n != 4) store.update("UPDATE tasks SET summary=?,description=? WHERE workspace=? AND case_no=? AND id=?", summary, description, workspace, n, id);
                return store.one("SELECT id,summary,description,owner FROM tasks WHERE workspace=? AND case_no=? AND id=?", workspace, n, id);
            }
            require(canRead(user, task), 404, "Няма достъпна задача.");
            if (method.equals("GET") && path.equals("/tasks/" + id)) return task;
            if (method.equals("DELETE") && path.endsWith("/delete")) {
                require(store.one("SELECT id FROM reports WHERE workspace=? AND case_no=? AND task_id=?", workspace, n, id) == null, 409, "Задачата има свързан отчет.");
                store.update("DELETE FROM tasks WHERE workspace=? AND case_no=? AND id=?", workspace, n, id); return Map.of("deleted", id);
            }
        }
        throw new Refusal(404, "Няма такъв маршрут в тази проверка.");
    }
    private static boolean isAdmin(Map<String, Object> user) { return "ADMIN".equals(user.get("role")); }
    private static boolean canRead(Map<String, Object> user, Map<String, Object> task) { return isAdmin(user) || user.get("username").equals(task.get("owner")); }
    private static String string(Map<String, Object> body, String key, int min, int max) {
        Object value = body.get(key);
        require(value instanceof String text && text.length() >= min && text.length() <= max && (min == 0 || !text.isBlank()), 400, "Полето " + key + " трябва да е текст с дължина " + min + "–" + max + ".");
        return (String)value;
    }
    @ExceptionHandler(Refusal.class)
    public ResponseEntity<Map<String, String>> refusal(Refusal e) { return ResponseEntity.status(e.status).body(Map.of("message", e.getMessage())); }
}
