package bg.tu_varna.sit.task_manager;

import bg.tu_varna.sit.task_manager.model.entity.Role;
import bg.tu_varna.sit.task_manager.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskManagerBaselineTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder encoder;
    @Autowired bg.tu_varna.sit.task_manager.repository.RefreshTokenRepository refreshTokens;

    @Test void registrationStoresUserWithHashedPassword() throws Exception {
        mvc.perform(post("/auth/register").with(csrf()).contentType("application/json")
            .content("{\"username\":\"alice\",\"password\":\"Alice-password-2026!\",\"role\":\"ADMIN\"}"))
            .andExpect(status().isOk());
        var user = users.findByUsername("alice").orElseThrow();
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getPassword()).isNotEqualTo("Alice-password-2026!");
        assertThat(encoder.matches("Alice-password-2026!", user.getPassword())).isTrue();
    }

    @Test void realLoginReturnsTokensAndSession() throws Exception {
        registrationStoresUserWithHashedPassword();
        var login = mvc.perform(post("/auth/login").with(csrf()).contentType("application/json")
            .content("{\"username\":\"alice\",\"password\":\"Alice-password-2026!\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("alice"))
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty()).andReturn();
        var session = (MockHttpSession) login.getRequest().getSession(false);
        assertThat(session).isNotNull();
        mvc.perform(get("/tasks").session(session)).andExpect(status().isOk());
    }

    @Test void taskCrudAndReportSummaryWork() throws Exception {
        var created = mvc.perform(post("/tasks").with(user("alice")).with(csrf())
            .contentType("application/json").content("""
                {"summary":"First task summary","description":"Description with enough characters","deadline":"2099-12-31T12:00:00"}
                """))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.summary").value("First task summary")).andReturn();
        long id = mapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(get("/tasks/" + id).with(user("alice")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
        for (int i = 0; i < 2; i++) {
            mvc.perform(post("/reports/task/" + id).with(user("admin").roles("ADMIN")).with(csrf())
                .contentType("application/json")
                .content("{\"content\":\"Report with enough characters\",\"workTime\":\"15:30:00\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.workTime").value("15:30:00"));
        }
        mvc.perform(get("/reports/task/" + id + "/summary").with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalWorkedTime").value("PT31H"));
        mvc.perform(get("/reports/task/" + id).with(user("alice")))
            .andExpect(status().isForbidden());
    }

    @Test void anonymousCannotReadTasks() throws Exception {
        mvc.perform(get("/tasks")).andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
    void logoutRevokesPersistedRefreshTokens() throws Exception {
        try {
            mvc.perform(post("/auth/register").with(csrf()).contentType("application/json")
                .content("{\"username\":\"logoutuser\",\"password\":\"Logout-password-2026!\"}"))
                .andExpect(status().isOk());
            var login = mvc.perform(post("/auth/login").with(csrf()).contentType("application/json")
                .content("{\"username\":\"logoutuser\",\"password\":\"Logout-password-2026!\"}"))
                .andExpect(status().isOk()).andReturn();
            var session = (MockHttpSession) login.getRequest().getSession(false);
            String token = mapper.readTree(login.getResponse().getContentAsString()).get("refreshToken").asText();
            mvc.perform(post("/auth/logout").session(session).with(csrf())).andExpect(status().isOk());
            assertThat(session.isInvalid()).isTrue();
            assertThat(refreshTokens.findByTokenAndRevokedFalse(token)).isEmpty();
        } finally {
            refreshTokens.deleteAll(refreshTokens.findAll().stream()
                .filter(t -> t.getUsername().equals("logoutuser")).toList());
            users.findByUsername("logoutuser").ifPresent(users::delete);
        }
    }
}
