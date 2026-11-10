package bg.tuvarna.bank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BankFlowTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate db;

    MockHttpSession operator() throws Exception {
        var login=mvc.perform(post("/login").with(csrf()).param("username","admin").param("password","admin"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/accounts")).andReturn();
        return (MockHttpSession)login.getRequest().getSession(false);
    }
    int count(){return db.queryForObject("select count(*) from accounts",Integer.class);}

    @Test void simulationNeverAuthenticates() throws Exception {
        var viewer=new MockHttpSession();
        mvc.perform(get("/login").param("case","altered").session(viewer))
           .andExpect(status().isOk()).andExpect(content().string(containsString("ДОПУСКА")))
           .andExpect(content().string(containsString("ОТКАЗВА")));
        mvc.perform(get("/accounts").session(viewer)).andExpect(status().is3xxRedirection());
        mvc.perform(post("/login").with(csrf()).session(viewer).param("username","admin").param("password","wrong"))
           .andExpect(redirectedUrl("/login?error"));
        mvc.perform(get("/accounts/new").session(viewer)).andExpect(status().is3xxRedirection());
        assertThat(db.queryForList("select login from bank_users",String.class)).containsExactly("admin");
        assertThat(db.queryForObject("select password_hash from bank_users",String.class)).startsWith("$2");
    }

    @Test void accountRoundTripUsesCiphertextAndDisplaysNoteAsText() throws Exception {
        var session=operator(); String iban="BG00DEMO00000000000888", egn="0000000888";
        mvc.perform(get("/accounts/new").session(session).param("example","bold"))
           .andExpect(status().isOk()).andExpect(content().string(containsString("&lt;b&gt;")));
        var res=mvc.perform(post("/accounts").with(csrf()).session(session)
           .param("alias","Тест Кедър").param("iban",iban).param("egn",egn).param("note","<b>Отчет & печат</b>"))
           .andExpect(status().is3xxRedirection()).andReturn();
        var path=res.getResponse().getRedirectedUrl(); long id=Long.parseLong(path.substring(path.lastIndexOf('/')+1));
        var stored=db.queryForMap("select iban_cipher,egn_cipher,note from accounts where id=?",id);
        assertThat(stored.get("IBAN_CIPHER")).isEqualTo(ShiftCipher.encrypt(iban)).isNotEqualTo(iban);
        assertThat(stored.get("EGN_CIPHER")).isEqualTo(ShiftCipher.encrypt(egn)).isNotEqualTo(egn);
        assertThat(stored.get("NOTE")).isEqualTo("<b>Отчет & печат</b>");
        for(String q:new String[]{"кедър",iban,egn}){
            mvc.perform(get("/accounts").session(session).param("q",q)).andExpect(status().isOk())
               .andExpect(content().string(containsString("Тест Кедър"))).andExpect(content().string(not(containsString("Студио Маяк"))));
        }
        mvc.perform(get(path).session(session).param("shift","3")).andExpect(status().isOk())
           .andExpect(content().string(containsString(iban))).andExpect(content().string(containsString(egn)))
           .andExpect(content().string(containsString("&lt;b&gt;Отчет &amp; печат&lt;/b&gt;")))
           .andExpect(content().string(containsString("<strong>Отчет &amp; печат</strong>")))
           .andExpect(header().string("Content-Security-Policy",containsString("default-src 'none'")));
        mvc.perform(get(path).session(session).param("shift","2")).andExpect(status().isOk())
           .andExpect(content().string(not(containsString(iban))));
        mvc.perform(get("/accounts/3").session(session)).andExpect(status().isOk())
           .andExpect(content().string(containsString("Модел: заглавието е сменено")));
    }

    @Test void rejectionPreservesRowsAndLogoutClosesAccess() throws Exception {
        var s=operator();int before=count();
        mvc.perform(post("/accounts").session(s).param("alias","X").param("iban","BG00DEMO00000000000044").param("egn","0000000001"))
           .andExpect(status().isForbidden());
        mvc.perform(post("/accounts").with(csrf()).session(s).param("alias","X").param("iban","BG00DEMO00000000000044").param("egn","123"))
           .andExpect(status().isBadRequest());
        mvc.perform(post("/accounts").with(csrf()).session(s).param("alias","X").param("iban","BG00DEMO00000000000001").param("egn","0000000001"))
           .andExpect(status().isConflict());
        assertThat(count()).isEqualTo(before);
        mvc.perform(get("/accounts").session(s).param("q","%")).andExpect(status().isOk())
           .andExpect(content().string(containsString("Няма съвпадения")));
        mvc.perform(post("/logout").with(csrf()).session(s)).andExpect(redirectedUrl("/login?logout"));
        assertThat(s.isInvalid()).isTrue();
    }

    @Test void weakCipherHasKnownPatternAndWraps() {
        assertThat(ShiftCipher.encrypt("BG00DEMO")).isEqualTo("EJ33GHPR");
        assertThat(ShiftCipher.encrypt("XYZ789")).isEqualTo("ABC012");
        assertThat(ShiftCipher.guess("ABC012",3)).isEqualTo("XYZ789");
    }
}
