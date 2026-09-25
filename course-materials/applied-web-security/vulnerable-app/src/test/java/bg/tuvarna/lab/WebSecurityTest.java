package bg.tuvarna.lab;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest @AutoConfigureMockMvc
class WebSecurityTest {
    @Autowired MockMvc mvc; @Autowired JdbcTemplate db; @Autowired LoginGuard guard; @Autowired PasswordEncoder encoder;
    @BeforeEach void reset() { guard.clear(); db.update("DELETE FROM comments"); }
    @Test void lab01_publicHealthAndDenyByDefault() throws Exception {
        mvc.perform(get("/health")).andExpect(status().isOk());
        mvc.perform(get("/unlisted").with(user("alice"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
    }
    @Test void lab02_passwordStorage() {
        String stored=db.queryForObject("SELECT password FROM app_users WHERE username='alice'",String.class);
        assertThat(stored).startsWith("{bcrypt}").doesNotContain("Lab-alice-2026!");
        assertThat(encoder.matches("Lab-alice-2026!",stored)).isTrue();
    }
    @Test void lab02_actualLoginAndLogout() throws Exception {
        var result=mvc.perform(post("/login").with(csrf()).param("username","alice").param("password","Lab-alice-2026!"))
            .andExpect(status().isNoContent()).andReturn();
        var session=(MockHttpSession)result.getRequest().getSession(false);
        assertThat(session).isNotNull();
        mvc.perform(get("/api/me").session(session)).andExpect(jsonPath("$.username").value("alice"));
        mvc.perform(post("/logout").session(session).with(csrf())).andExpect(status().isNoContent());
        assertThat(session.isInvalid()).isTrue();
        mvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
    }
    @Test void lab02_invalidAndUnknownCredentials() throws Exception {
        for(String name:new String[]{"alice","nobody"}) mvc.perform(post("/login").with(csrf()).param("username",name).param("password","incorrect"))
            .andExpect(status().isUnauthorized());
    }
    @Test void lab03_objectAndRoleMatrix() throws Exception {
        mvc.perform(get("/api/documents/1")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/documents/1").with(user("alice"))).andExpect(status().isOk());
        mvc.perform(get("/api/documents/1").with(user("bob"))).andExpect(status().isNotFound());
        mvc.perform(get("/api/documents/1").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
        mvc.perform(get("/api/documents/999").with(user("alice"))).andExpect(status().isNotFound());
        mvc.perform(get("/api/documents/not-a-number").with(user("alice"))).andExpect(status().isBadRequest());
        mvc.perform(get("/admin/status").with(user("alice"))).andExpect(status().isForbidden());
        mvc.perform(get("/admin/status").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
    }
    @Test void lab04_limitIsWiredIntoLogin() throws Exception {
        for(int i=0;i<5;i++) mvc.perform(post("/login").with(csrf()).param("username","alice").param("password","wrong"))
            .andExpect(status().isUnauthorized());
        mvc.perform(post("/login").with(csrf()).param("username","alice").param("password","Lab-alice-2026!"))
            .andExpect(status().isUnauthorized());
    }
    @Test void lab05_sqlInjectionCannotCrossOwnerBoundary() throws Exception {
        mvc.perform(get("/api/search").param("q","notes").with(user("alice"))).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/search").param("q","O'Reilly").with(user("alice"))).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/search").param("q","' OR '1'='1' -- ").with(user("alice"))).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/search").param("q","").with(user("alice"))).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/search").param("q","x".repeat(101)).with(user("alice"))).andExpect(status().isBadRequest());
        mvc.perform(get("/api/search").param("q","\0").with(user("alice"))).andExpect(status().isBadRequest());
    }
    @Test void lab06_storedAndReflectedEncoding() throws Exception {
        String payload="<script>document.title='LAB-XSS'</script>";
        mvc.perform(post("/api/comments").with(user("alice")).with(csrf()).param("body",payload)).andExpect(status().isCreated());
        mvc.perform(get("/comments").with(user("bob"))).andExpect(content().string(containsString("&lt;script&gt;")))
            .andExpect(content().string(not(containsString("<script>")))).andExpect(header().exists("Content-Security-Policy"));
        mvc.perform(get("/search").with(user("alice")).param("q","<>&\""))
            .andExpect(content().string(containsString("&lt;&gt;&amp;&quot;")));
        mvc.perform(get("/search").with(user("alice")).param("q","Здравей свят"))
            .andExpect(content().string(containsString("Здравей свят")));
    }
    @Test void lab07_csrfMustProtectState() throws Exception {
        String before=db.queryForObject("SELECT display_name FROM app_users WHERE username='alice'",String.class);
        mvc.perform(post("/api/profile").with(user("alice")).param("displayName","forged")).andExpect(status().isForbidden());
        mvc.perform(post("/api/profile").with(user("alice")).with(csrf().useInvalidToken()).param("displayName","forged")).andExpect(status().isForbidden());
        assertThat(db.queryForObject("SELECT display_name FROM app_users WHERE username='alice'",String.class)).isEqualTo(before);
        mvc.perform(post("/api/profile").with(user("alice")).with(csrf()).param("displayName","Allowed")).andExpect(status().isOk());
    }
    @Test void lab08_sensitiveFieldNotPlaintext() throws Exception {
        mvc.perform(post("/api/sensitive").with(user("alice")).with(csrf()).param("value","SYNTHETIC-123" )).andExpect(status().isOk());
        assertThat(db.queryForObject("SELECT sensitive FROM app_users WHERE username='alice'",String.class)).startsWith("v1:").doesNotContain("SYNTHETIC");
        mvc.perform(get("/api/sensitive").with(user("alice"))).andExpect(jsonPath("$.value").value("SYNTHETIC-123"));
    }
    @Test void lab10_headersAndNoAccidentalPublicApi() throws Exception {
        mvc.perform(get("/health")).andExpect(header().string("X-Content-Type-Options","nosniff"))
            .andExpect(header().exists("X-Correlation-ID"));
        mvc.perform(get("/api/sensitive")).andExpect(status().isUnauthorized());
    }
}
