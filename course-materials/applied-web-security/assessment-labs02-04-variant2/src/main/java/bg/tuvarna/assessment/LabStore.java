package bg.tuvarna.assessment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Statement;
import java.time.Instant;
import java.util.*;

@Repository
public class LabStore {
    public record Session(String id, String workspace, int caseNo, String username) {}
    public record IssuedSession(String token, Session session) {}
    private final JdbcTemplate jdbc;
    private final TransactionTemplate tx;
    private final ObjectMapper mapper;
    private final BCryptPasswordEncoder passwords = new BCryptPasswordEncoder(10);
    private final String dummyHash = passwords.encode(secret());
    private final List<List<String>> seedUsers;
    private final Object[] locks = java.util.stream.IntStream.range(0, 64).mapToObj(i -> new Object()).toArray();
    public Object lock(String workspace) { return locks[Math.floorMod(workspace.hashCode(), locks.length)]; }
    public LabStore(JdbcTemplate jdbc, TransactionTemplate tx, ObjectMapper mapper) {
        this.jdbc = jdbc; this.tx = tx; this.mapper = mapper;
        seedUsers = List.of(
            List.of("alice", passwords.encode("Alice-lab-2026!"), "USER"),
            List.of("bob", passwords.encode("Bob-lab-2026!"), "USER"),
            List.of("admin", passwords.encode("Admin-lab-2026!"), "ADMIN"));
    }
    public static String secret() {
        byte[] bytes = new byte[32]; new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    public static String digest(String text) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception e) { throw new IllegalStateException(e); }
    }
    public static String label(Session session) { return session == null ? "—" : "S-" + session.id().substring(0, 12); }
    public Map<String, Object> one(String sql, Object... args) {
        List<Map<String, Object>> rows = all(sql, args); return rows.isEmpty() ? null : rows.get(0);
    }
    public List<Map<String, Object>> all(String sql, Object... args) {
        return jdbc.query(sql, (rs, row) -> {
            Map<String, Object> result = new LinkedHashMap<>();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                String name = rs.getMetaData().getColumnLabel(i).toLowerCase(Locale.ROOT);
                Object value = rs.getObject(i);
                result.put(name, value instanceof java.sql.Clob ? rs.getString(i) : value);
            }
            return result;
        }, args);
    }
    public int update(String sql, Object... args) { return jdbc.update(sql, args); }
    public String encode(String password) { return passwords.encode(password); }
    public boolean matches(String password, Map<String, Object> user) {
        return passwords.matches(password, user == null ? dummyHash : (String) user.get("password_hash")) && user != null;
    }
    public Map<String, Object> user(String workspace, int n, String username) {
        return one("SELECT * FROM lab_users WHERE workspace=? AND case_no=? AND username=?", workspace, n, username);
    }
    public String createWorkspace() {
        String token = secret(), id = digest(token);
        tx.executeWithoutResult(status -> {
            update("INSERT INTO workspaces(id,created) VALUES(?,?)", id, Instant.now().toString());
            for (int n = 1; n <= 5; n++) {
                update("INSERT INTO lab_runs(workspace,case_no) VALUES(?,?)", id, n);
                seedCase(id, n);
            }
        });
        return token;
    }
    private void seedCase(String workspace, int n) {
        for (List<String> user : seedUsers) update("INSERT INTO lab_users VALUES(?,?,?,?,?)", workspace, n, user.get(0), user.get(1), user.get(2));
        update("INSERT INTO tasks VALUES(?,?,?,?,?,?)", workspace, n, 101, "Начална страница", "Преглед на началната страница на проекта.", "alice");
        update("INSERT INTO tasks VALUES(?,?,?,?,?,?)", workspace, n, 102, "Форма за контакт", "Проверка на формата и съобщенията за грешка.", "alice");
        update("INSERT INTO tasks VALUES(?,?,?,?,?,?)", workspace, n, 201, "Навигация", "Проверка на менюто на мобилен екран.", "bob");
        update("INSERT INTO reports VALUES(?,?,?,?,?)", workspace, n, 501, 101, "Учебен отчет: началната страница е прегледана.");
    }
    public void reset(String workspace, int n) {
        tx.executeWithoutResult(status -> {
            for (String table : List.of("lab_users", "tasks", "reports", "lab_sessions", "lab_tokens"))
                update("DELETE FROM " + table + " WHERE workspace=? AND case_no=?", workspace, n);
            seedCase(workspace, n);
            update("UPDATE lab_runs SET generation=generation+1 WHERE workspace=? AND case_no=?", workspace, n);
        });
    }
    public Session session(String token, String workspace, int n) {
        if (token == null) return null;
        Map<String, Object> row = one("SELECT * FROM lab_sessions WHERE id=? AND workspace=? AND case_no=? AND expires>?", digest(token), workspace, n, System.currentTimeMillis());
        return row == null ? null : new Session((String)row.get("id"), workspace, n, (String)row.get("username"));
    }
    public IssuedSession rotate(Session old, String workspace, int n, String username) {
        String token = secret();
        tx.executeWithoutResult(status -> {
            if (old != null) dropSession(old.id());
            update("INSERT INTO lab_sessions VALUES(?,?,?,?,?)", digest(token), workspace, n, username, System.currentTimeMillis() + 8 * 3600_000L);
        });
        return new IssuedSession(token, session(token, workspace, n));
    }
    public void dropSession(String id) {
        update("DELETE FROM lab_tokens WHERE session_id=?", id);
        update("DELETE FROM lab_sessions WHERE id=?", id);
    }
    public Map<String, Object> issueTokens(Session session) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String kind : List.of("accessToken", "refreshToken")) {
            String token = secret();
            long ttl = kind.equals("accessToken") ? 15 * 60_000L : 8 * 3600_000L;
            update("INSERT INTO lab_tokens VALUES(?,?,?,?,?,?,?)", digest(token), session.workspace(), session.caseNo(), session.username(), session.id(), kind, System.currentTimeMillis() + ttl);
            result.put(kind, token);
        }
        return result;
    }
    public String bearerUser(String token, String workspace, int n) {
        Map<String, Object> row = one("SELECT username FROM lab_tokens WHERE id=? AND workspace=? AND case_no=? AND kind='accessToken' AND expires>?", digest(token), workspace, n, System.currentTimeMillis());
        return row == null ? null : (String)row.get("username");
    }
    public long record(String workspace, int n, Map<String, Object> entry) {
        Number generation = (Number)one("SELECT generation FROM lab_runs WHERE workspace=? AND case_no=?", workspace, n).get("generation");
        var keys = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement("INSERT INTO evidence(workspace,case_no,generation,record_json) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, workspace); statement.setInt(2, n); statement.setInt(3, generation.intValue()); statement.setString(4, json(redact(entry)));
            return statement;
        }, keys);
        return Objects.requireNonNull(keys.getKey()).longValue();
    }
    public List<Map<String, Object>> evidence(String workspace) {
        return all("SELECT * FROM evidence WHERE workspace=? ORDER BY id", workspace).stream().map(row -> {
            Map<String, Object> entry = parse((String)row.get("record_json"));
            entry.put("id", row.get("id")); entry.put("case", row.get("case_no")); entry.put("generation", row.get("generation")); return entry;
        }).toList();
    }
    public String json(Object value) {
        try { return mapper.writeValueAsString(value); } catch (Exception e) { throw new IllegalStateException(e); }
    }
    public Map<String, Object> parse(String value) {
        try { return mapper.readValue(value, new TypeReference<LinkedHashMap<String, Object>>() {}); }
        catch (Exception e) { throw new IllegalArgumentException("Невалиден JSON обект."); }
    }
    public static Object redact(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> result = new LinkedHashMap<>();
            map.forEach((key, item) -> result.put(key.toString(), key.toString().toLowerCase(Locale.ROOT).matches(".*(password|token|cookie|authorization|secret|sessionid).*") ? "[скрито]" : redact(item)));
            return result;
        }
        if (value instanceof List<?> list) return list.stream().map(LabStore::redact).toList();
        return value;
    }
}
