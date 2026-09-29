package bg.tuvarna.lab;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest @AutoConfigureMockMvc @ExtendWith(OutputCaptureExtension.class)
class AuditTest {
    @Autowired MockMvc mvc;
    @Test void credentialsAndFieldsNeverEnterLogs(CapturedOutput output) throws Exception {
        mvc.perform(post("/login").with(csrf()).param("username","nobody").param("password","NEVER-LOG-PASSWORD"));
        mvc.perform(post("/api/sensitive").with(user("alice")).with(csrf()).param("value","NEVER-LOG-FIELD"));
        mvc.perform(get("/token-api/documents").header("Authorization","Bearer NEVER-LOG-TOKEN"));
        assertThat(output.getAll()).contains("security_event").doesNotContain("NEVER-LOG-PASSWORD","NEVER-LOG-FIELD","NEVER-LOG-TOKEN");
    }
}
