package bg.tuvarna.lab;
import java.time.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc
class JwtSecurityTest {
    @Autowired MockMvc mvc; @Autowired Tokens tokens;
    private void request(String token,int status) throws Exception { mvc.perform(get("/token-api/documents").header("Authorization","Bearer "+token)).andExpect(status().is(status)); }
    @Test void validAndRequiredScope() throws Exception {
        request(tokens.issue("alice"),200);
        request(tokens.issue("alice",Tokens.ISSUER,"lab-api",Instant.now().plusSeconds(60),""),403);
    }
    @Test void expiredWrongIssuerWrongAudience() throws Exception {
        request(tokens.issue("alice",Tokens.ISSUER,"lab-api",Instant.now().minusSeconds(120),"documents.read"),401);
        request(tokens.issue("alice","https://other.invalid","lab-api",Instant.now().plusSeconds(60),"documents.read"),401);
        request(tokens.issue("alice",Tokens.ISSUER,"other",Instant.now().plusSeconds(60),"documents.read"),401);
    }
    @Test void modifiedPayloadAndForeignSignature() throws Exception {
        String[] parts=tokens.issue("alice").split("\\.");
        String json=new String(Base64.getUrlDecoder().decode(parts[1]),StandardCharsets.UTF_8).replace("alice","admin");
        request(parts[0]+"."+Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8))+"."+parts[2],401);
        request(new Tokens(new LabMode("secure"),Clock.systemUTC()).issue("alice"),401);
    }
    @Test void malformedAndNoSessionFallback() throws Exception {
        request("broken",401);
        mvc.perform(get("/token-api/documents")).andExpect(status().isUnauthorized());
    }
}
