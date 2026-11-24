package bg.tuvarna.bank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BankFlowTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate db;

    MockHttpSession login() throws Exception {
        return (MockHttpSession) mvc.perform(post("/login").with(csrf())
                .param("username", "user").param("password", "123456"))
            .andExpect(redirectedUrl("/accounts")).andReturn().getRequest().getSession(false);
    }

    @Test
    void loginRequiresValidCredentialsAndLogoutEndsAccess() throws Exception {
        mvc.perform(get("/accounts")).andExpect(status().is3xxRedirection());
        mvc.perform(post("/login").with(csrf()).param("username", "user").param("password", "wrong"))
            .andExpect(redirectedUrl("/login?error"));
        var session = login();
        mvc.perform(get("/accounts/4").session(session)).andExpect(status().isNotFound());
        mvc.perform(post("/logout").with(csrf()).session(session)).andExpect(redirectedUrl("/login?logout"));
        assertThat(session.isInvalid()).isTrue();
    }

    @Test
    void injectedTypeReturnsOtherOwnersAccounts() throws Exception {
        var session = login();
        mvc.perform(get("/accounts").session(session)).andExpect(status().isOk())
            .andExpect(model().attribute("rows", hasSize(3)))
            .andExpect(content().string(not(containsString("Мария Георгиева Стоянова"))));
        mvc.perform(get("/accounts").param("type", "Спестовна").session(session))
            .andExpect(status().isOk()).andExpect(model().attribute("rows", hasSize(1)));
        mvc.perform(get("/accounts").param("type", "' OR '1'='1").session(session))
            .andExpect(status().isOk()).andExpect(model().attribute("rows", hasSize(6)))
            .andExpect(content().string(containsString("Мария Георгиева Стоянова")))
            .andExpect(content().string(containsString("Николай Иванов Колев")))
            .andExpect(content().string(containsString("ROT5:5555556557")));
        mvc.perform(get("/accounts").param("type", "'").session(session))
            .andExpect(status().isBadRequest()).andExpect(content().string(containsString("невалидна SQL")));
        mvc.perform(get("/accounts").session(session))
            .andExpect(status().isOk()).andExpect(model().attribute("rows", hasSize(3)));
    }

    @Test
    void creatingAccountGeneratesIbanEncryptsEgnAndRendersStoredScript() throws Exception {
        var session = login();
        String payload = "<script>document.querySelector('h1').textContent='XSS DEMO';</script>";
        mvc.perform(get("/accounts/new").session(session)).andExpect(status().isOk())
            .andExpect(content().string(not(containsString("name=\"iban\""))));
        for (int i = 0; i < 2; i++) {
            mvc.perform(post("/accounts").with(csrf()).session(session)
                    .param("firstName", "Елена").param("middleName", "Петрова").param("lastName", "Учебна")
                    .param("type", "Спестовна").param("openedOn", "2026-11-24")
                    .param("egn", "0000001004").param("reason", payload)
                    .param("owner", "admin").param("iban", "CLIENT-SUPPLIED"))
                .andExpect(redirectedUrl("/accounts"));
        }
        var stored = db.queryForList("select * from accounts where first_name = ?", "Елена");
        assertThat(stored).hasSize(2);
        assertThat(stored.get(0).get("OWNER_LOGIN")).isEqualTo("user");
        assertThat(stored.get(0).get("EGN_CIPHER")).isEqualTo("ROT5:5555556559");
        assertThat(stored.get(0).get("REASON")).isEqualTo(payload);
        assertThat(stored.get(0).get("IBAN")).isNotEqualTo(stored.get(1).get("IBAN"));
        for (var row : stored) {
            String iban = (String) row.get("IBAN");
            assertThat(iban).matches("BG[0-9]{2}DEMO[0-9]{14}");
            StringBuilder digits = new StringBuilder();
            for (char c : (iban.substring(4) + iban.substring(0, 4)).toCharArray()) {
                digits.append(Character.digit(c, 36));
            }
            assertThat(new BigInteger(digits.toString()).mod(BigInteger.valueOf(97))).isEqualTo(BigInteger.ONE);
        }
        for (int i = 0; i < 2; i++) {
            mvc.perform(get("/accounts").session(session)).andExpect(status().isOk())
                .andExpect(model().attribute("rows", hasSize(5)))
                .andExpect(content().string(containsString(payload)))
                .andExpect(content().string(containsString("Елена Петрова Учебна")))
                .andExpect(content().string(containsString("2026-11-24")))
                .andExpect(content().string(not(containsString("0000001004"))))
                .andExpect(header().string("Content-Security-Policy", containsString("script-src 'unsafe-inline'")));
        }
    }

    @Test
    void invalidSubmissionPreservesDatabaseAndEscapesTheForm() throws Exception {
        var session = login();
        int before = db.queryForObject("select count(*) from accounts", Integer.class);
        String payload = "</textarea><script>alert(1)</script>";
        mvc.perform(post("/accounts").with(csrf()).session(session)
                .param("firstName", "Иван").param("middleName", "Петров").param("lastName", "Димов")
                .param("type", "Спестовна").param("openedOn", "2026-11-24")
                .param("egn", "123").param("reason", payload))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("&lt;script&gt;")))
            .andExpect(content().string(not(containsString(payload))));
        mvc.perform(post("/accounts").with(csrf()).session(session)
                .param("firstName", "Иван").param("middleName", "Петров").param("lastName", "Димов")
                .param("type", "Спестовна").param("openedOn", "2026-02-30")
                .param("egn", "0000001001").param("reason", "Тест"))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/accounts").session(session)).andExpect(status().isForbidden());
        assertThat(db.queryForObject("select count(*) from accounts", Integer.class)).isEqualTo(before);
    }

    @Test
    void rot5IsIdentifiableReversibleAndDeterministic() {
        assertThat(ShiftCipher.encrypt("0123456789")).isEqualTo("ROT5:5678901234");
        assertThat(ShiftCipher.decrypt("ROT5:5678901234")).isEqualTo("0123456789");
        assertThat(ShiftCipher.decrypt(ShiftCipher.encrypt("0000001001"))).isEqualTo("0000001001");
        assertThat(ShiftCipher.encrypt("0000001001")).isEqualTo(ShiftCipher.encrypt("0000001001"));
        assertThatThrownBy(() -> ShiftCipher.encrypt("letters")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ShiftCipher.decrypt("5678901234")).isInstanceOf(IllegalArgumentException.class);
    }
}
