package bg.tuvarna.assessment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.sql.DriverManager;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties={
    "spring.datasource.url=jdbc:h2:mem:assessment-tests;DB_CLOSE_DELAY=-1", "spring.datasource.password=", "server.port=0"
})
class AssessmentIntegrationTest {
    @LocalServerPort int port;
    @Autowired ObjectMapper mapper;
    @Autowired LabStore store;
    record Reply(int status, JsonNode body, HttpHeaders headers, String raw) {}
    class Browser {
        final HttpClient client = HttpClient.newHttpClient();
        final Map<String,String> cookies = new LinkedHashMap<>();
        Reply request(String method, String path, Object body) throws Exception { return send(method, path, body == null ? "" : mapper.writeValueAsString(body), Map.of()); }
        Reply send(String method, String path, String raw, Map<String,String> headers) throws Exception {
            var builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path));
            builder.header("Content-Type", "application/json");
            String cookie = cookies.entrySet().stream().filter(e -> e.getKey().equals("assessment_workspace") || path.startsWith("/api/cases/" + e.getKey().replace("assessment_s", "") + "/"))
                .map(e -> e.getKey() + "=" + e.getValue()).reduce((a,b) -> a + "; " + b).orElse("");
            if (!cookie.isEmpty()) builder.header("Cookie", cookie);
            headers.forEach(builder::header);
            var response = client.send(builder.method(method, raw.isEmpty() ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(raw)).build(), HttpResponse.BodyHandlers.ofString());
            for (String value : response.headers().allValues("Set-Cookie")) {
                String[] pair = value.split(";",2)[0].split("=",2);
                if (pair.length == 2 && !pair[1].isEmpty()) cookies.put(pair[0],pair[1]); else cookies.remove(pair[0]);
            }
            JsonNode json;
            try { json = mapper.readTree(response.body()); } catch(Exception e) { json = mapper.createObjectNode(); }
            return new Reply(response.statusCode(), json, response.headers(), response.body());
        }
        Reply call(int n, String method, String path, Object body) throws Exception { return request(method, "/api/cases/" + n + path, body); }
        Reply get(int n, String path) throws Exception { return call(n,"GET",path,null); }
        Reply login(int n, String name) throws Exception {
            return call(n,"POST","/auth/login",Map.of("username",name,"password",name.substring(0,1).toUpperCase() + name.substring(1) + "-lab-2026!"));
        }
    }
    Browser browser() throws Exception {
        Browser b = new Browser(); assertThat(b.request("GET","/api/workspace",null).status()).isEqualTo(200); return b;
    }
    @Test void registrationUsesStoredUserRoleAndDeniesReports() throws Exception {
        Browser b = browser();
        assertThat(b.call(1,"POST","/auth/register",Map.of("username","candidate","password","Candidate-lab-2026!","role","ADMIN")).status()).isEqualTo(200);
        assertThat(b.login(1,"candidate").status()).isEqualTo(200);
        assertThat(b.get(1,"/auth/me").body().get("role").asText()).isEqualTo("USER");
        assertThat(b.get(1,"/diagnostics/users/candidate").body().at("/storedUser/role").asText()).isEqualTo("USER");
        assertThat(b.get(1,"/diagnostics/users/admin").status()).isEqualTo(404);
        assertThat(b.get(1,"/reports/task/101").status()).isEqualTo(403);
        String workspace = LabStore.digest(b.cookies.get("assessment_workspace"));
        String hash = (String)store.user(workspace,1,"candidate").get("password_hash");
        assertThat(hash).startsWith("$2").doesNotContain("Candidate-lab");
    }
    @Test void secondCaseReproducesTheAuthenticatedLoginShortcut() throws Exception {
        Browser b = browser(); b.login(2,"alice"); String old = b.cookies.get("assessment_s2");
        Reply attempt = b.call(2,"POST","/auth/login",Map.of("username","bob","password","Wrong-password-2026!"));
        assertThat(attempt.status()).isEqualTo(200);
        assertThat(attempt.body().get("username").asText()).isEqualTo("alice");
        assertThat(attempt.body().has("accessToken")).isTrue(); assertThat(attempt.body().has("refreshToken")).isTrue();
        assertThat(b.cookies.get("assessment_s2")).isNotEqualTo(old);
        assertThat(b.get(2,"/auth/me").body().get("username").asText()).isEqualTo("alice");
        b.call(2,"POST","/auth/logout",Map.of());
        Reply anonymousAttempt = b.call(2,"POST","/auth/login",Map.of("username","bob","password","Wrong-password-2026!"));
        assertThat(anonymousAttempt.status()).isEqualTo(401);
        assertThat(anonymousAttempt.body().has("accessToken")).isFalse();
    }
    @Test void thirdCaseChangesSessionAndIdentityAndChecksEveryPassword() throws Exception {
        Browser b = browser(); b.login(3,"alice"); String old = b.cookies.get("assessment_s3");
        assertThat(b.login(3,"bob").body().get("username").asText()).isEqualTo("bob");
        assertThat(b.cookies.get("assessment_s3")).isNotEqualTo(old);
        assertThat(b.get(3,"/auth/me").body().get("username").asText()).isEqualTo("bob");
        Reply wrong = b.call(3,"POST","/auth/login",Map.of("username","alice","password","Wrong-password-2026!"));
        assertThat(wrong.status()).isEqualTo(401); assertThat(wrong.body().has("accessToken")).isFalse();
        assertThat(b.get(3,"/auth/me").body().get("username").asText()).isEqualTo("bob");
        b.cookies.put("assessment_s3",old);
        assertThat(b.get(3,"/auth/me").status()).isEqualTo(401);
    }
    @Test void fourthCaseCommitsTheChangeDespiteThe404() throws Exception {
        Browser b = browser(); b.login(4,"alice");
        assertThat(b.get(4,"/tasks/101").body().get("summary").asText()).isEqualTo("Начална страница");
        b.login(4,"bob");
        assertThat(b.call(4,"PATCH","/tasks/101/update",Map.of("summary","Проверено от Bob")).status()).isEqualTo(404);
        b.login(4,"alice"); JsonNode task = b.get(4,"/tasks/101").body();
        assertThat(task.get("summary").asText()).isEqualTo("Проверено от Bob");
        assertThat(task.get("owner").asText()).isEqualTo("alice");
    }
    @Test void fifthCaseSeparatesOwnershipAndReportRoles() throws Exception {
        Browser b = browser(); assertThat(b.get(5,"/tasks/101").status()).isEqualTo(401);
        b.login(5,"alice"); assertThat(b.get(5,"/tasks/101").status()).isEqualTo(200); assertThat(b.get(5,"/reports/task/101").status()).isEqualTo(403);
        b.login(5,"bob"); Reply foreign = b.get(5,"/tasks/101"), absent = b.get(5,"/tasks/999999");
        assertThat(foreign.status()).isEqualTo(404); assertThat(absent.status()).isEqualTo(404); assertThat(foreign.body()).isEqualTo(absent.body());
        assertThat(b.get(5,"/tasks").body().size()).isEqualTo(1);
        assertThat(b.get(5,"/reports/task/101").status()).isEqualTo(403);
        assertThat(b.call(5,"PATCH","/tasks/101/update",Map.of("summary","attempt")).status()).isEqualTo(404);
        b.login(5,"admin"); assertThat(b.get(5,"/reports/task/101").status()).isEqualTo(200);
        assertThat(b.get(5,"/tasks/101").body().get("summary").asText()).isEqualTo("Начална страница");
        assertThat(b.get(5,"/tasks").body().size()).isEqualTo(3);
    }
    @Test void participantsCasesAndTokensAreIsolated() throws Exception {
        Browser a = browser(), b = browser(); a.login(4,"bob");
        a.call(4,"PATCH","/tasks/101/update",Map.of("summary","Private change"));
        b.login(4,"alice"); assertThat(b.get(4,"/tasks/101").body().get("summary").asText()).isEqualTo("Начална страница");
        a.login(5,"alice"); assertThat(a.get(5,"/tasks/101").body().get("summary").asText()).isEqualTo("Начална страница");
        Reply login = a.login(3,"alice"); String token = login.body().get("accessToken").asText();
        a.cookies.remove("assessment_s3");
        assertThat(a.send("GET","/api/cases/3/auth/me","",Map.of("Authorization","Bearer " + token)).status()).isEqualTo(200);
        assertThat(b.send("GET","/api/cases/3/auth/me","",Map.of("Authorization","Bearer " + token)).status()).isEqualTo(401);
        assertThat(a.send("GET","/api/cases/1/auth/me","",Map.of("Authorization","Bearer " + token)).status()).isEqualTo(401);
        b.cookies.put("assessment_s5",a.cookies.get("assessment_s5"));
        assertThat(b.get(5,"/auth/me").status()).isEqualTo(401);
    }
    @Test void resetPreservesNotesAndEvidenceButRevokesOnlyThisCase() throws Exception {
        Browser b = browser(); b.login(4,"bob"); b.login(5,"alice"); String old = b.cookies.get("assessment_s4");
        b.call(4,"PATCH","/tasks/101/update",Map.of("summary","before reset"));
        b.request("PUT","/api/answers",Map.of("participant","12345","answers",Map.of("4","My observation #1")));
        assertThat(b.call(4,"POST","/reset",Map.of()).status()).isEqualTo(200);
        b.cookies.put("assessment_s4",old); assertThat(b.get(4,"/auth/me").status()).isEqualTo(401);
        assertThat(b.get(5,"/auth/me").body().get("username").asText()).isEqualTo("alice");
        b.login(4,"alice"); assertThat(b.get(4,"/tasks/101").body().get("summary").asText()).isEqualTo("Начална страница");
        JsonNode export = b.request("GET","/api/export",null).body();
        assertThat(export.at("/answers/4").asText()).isEqualTo("My observation #1");
        assertThat(export.get("evidence").toString()).contains("before reset", "\"generation\":1", "\"generation\":2");
    }
    @Test void journalAndExportRedactCredentialsAndAreScoped() throws Exception {
        Browser a = browser(), b = browser(); Reply login = a.login(3,"alice");
        String access = login.body().get("accessToken").asText(), refresh = login.body().get("refreshToken").asText();
        a.call(3,"GET","/tasks",null);
        Reply export = a.request("GET","/api/export",null);
        assertThat(export.raw()).contains("[скрито]", "sessionBefore", "identityAfter").doesNotContain("Alice-lab-2026!",access,refresh,a.cookies.get("assessment_workspace"),a.cookies.get("assessment_s3"));
        assertThat(b.request("GET","/api/export",null).body().get("evidence").size()).isZero();
        assertThat(export.headers().firstValue("Content-Disposition").orElse("")).contains("attachment", ".json");
    }
    @Test void logoutInvalidatesSessionAndTokens() throws Exception {
        Browser b = browser(); Reply login = b.login(3,"alice"); String old = b.cookies.get("assessment_s3"), token = login.body().get("accessToken").asText();
        b.call(3,"POST","/auth/logout",Map.of()); b.cookies.put("assessment_s3",old);
        assertThat(b.get(3,"/auth/me").status()).isEqualTo(401);
        assertThat(b.send("GET","/api/cases/3/auth/me","",Map.of("Authorization","Bearer " + token)).status()).isEqualTo(401);
    }
    @Test void validationRejectsBadInputWithoutChangingUsers() throws Exception {
        Browser b = browser();
        assertThat(b.call(1,"POST","/auth/register",Map.of("username","candidate","password","Ж".repeat(40))).status()).isEqualTo(400);
        assertThat(b.login(1,"candidate").status()).isEqualTo(401);
        assertThat(b.send("POST","/api/cases/1/auth/login","{broken",Map.of()).status()).isEqualTo(400);
        assertThat(b.request("PUT","/api/answers",Map.of("participant","123","answers",Map.of("6","unknown"))).status()).isEqualTo(400);
        assertThat(b.request("PUT","/api/answers",Map.of("participant","123","answers",Map.of("1","x".repeat(5001)))).status()).isEqualTo(400);
        assertThat(b.send("POST","/api/cases/1/auth/login","x".repeat(65537),Map.of()).status()).isEqualTo(413);
        assertThat(new Browser().get(1,"/auth/me").status()).isEqualTo(401);
    }
    @Test void originAndStaticResourceBoundariesAreEnforced() throws Exception {
        Browser b = browser();
        assertThat(b.send("POST","/api/cases/1/reset","{}",Map.of("Origin","https://other.example")).status()).isEqualTo(403);
        for (String path : List.of("/hint.md","/content/assignment.json","/pom.xml","/data/assessment.mv.db","/h2-console/","/src/main/java/bg/tuvarna/assessment/ApiController.java"))
            assertThat(b.request("GET",path,null).status()).as(path).isEqualTo(404);
        Reply page = b.request("GET","/",null);
        assertThat(page.status()).isEqualTo(200); assertThat(page.raw()).contains("lang=\"bg\"", "Вашият извод");
        assertThat(page.headers().firstValue("Content-Security-Policy").orElse("")).contains("frame-ancestors 'none'");
        assertThat(b.login(1,"alice").headers().firstValue("Set-Cookie").orElse("")).contains("HttpOnly","SameSite=Strict","Path=/api/cases/1");
    }
    @Test void studentContentIsSynchronizedAndContainsExactlyFiveCases() throws Exception {
        Path root = Path.of("");
        assertThat(Files.readAllBytes(root.resolve("content/assignment.json"))).isEqualTo(Files.readAllBytes(root.resolve("src/main/resources/static/assignment.json")));
        JsonNode content = mapper.readTree(root.resolve("content/assignment.json").toFile());
        assertThat(content.get("cases").size()).isEqualTo(5);
        for (int i=0;i<5;i++) { assertThat(content.get("cases").get(i).get("id").asInt()).isEqualTo(i+1); assertThat(content.get("cases").get(i).get("steps").size()).isGreaterThanOrEqualTo(4); }
        try (var files = Files.walk(root.resolve("src/main/resources"))) { assertThat(files.noneMatch(path -> path.getFileName().toString().equals("hint.md"))).isTrue(); }
    }
    @Test void h2FileRetainsActualSchemaAndRecordsAfterClosingAllConnections() throws Exception {
        Path directory = Files.createTempDirectory("assessment-h2-");
        String url = "jdbc:h2:file:" + directory.resolve("database").toAbsolutePath().toString().replace('\\','/');
        try {
            try (var connection = DriverManager.getConnection(url,"sa",""); var statement = connection.createStatement()) {
                statement.execute(Files.readString(Path.of("src/main/resources/schema.sql")));
                statement.execute("INSERT INTO workspaces(id,created,participant,answers) VALUES('w','now','student','{\"1\":\"saved\"}')");
                statement.execute("INSERT INTO tasks VALUES('w',4,101,'Persisted change','text','alice')");
            }
            try (var connection = DriverManager.getConnection(url,"sa",""); var statement = connection.createStatement()) {
                try (var result = statement.executeQuery("SELECT summary FROM tasks WHERE workspace='w' AND case_no=4")) { assertThat(result.next()).isTrue(); assertThat(result.getString(1)).isEqualTo("Persisted change"); }
                try (var result = statement.executeQuery("SELECT answers FROM workspaces WHERE id='w'")) { assertThat(result.next()).isTrue(); assertThat(result.getString(1)).contains("saved"); }
            }
        } finally {
            try (var files = Files.list(directory)) { for (Path file : files.toList()) Files.delete(file); }
            Files.delete(directory);
        }
    }
}
