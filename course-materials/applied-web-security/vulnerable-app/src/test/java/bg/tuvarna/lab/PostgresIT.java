package bg.tuvarna.lab;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.containers.PostgreSQLContainer;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest @Testcontainers
class PostgresIT {
    @Container static PostgreSQLContainer<?> postgres=new PostgreSQLContainer<>("postgres:17.6-alpine");
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url",postgres::getJdbcUrl);r.add("spring.datasource.username",postgres::getUsername);r.add("spring.datasource.password",postgres::getPassword);
    }
    @Autowired Documents documents;
    @Test void injectionAndQuotesOnRealPostgres() {
        assertThat(documents.search("O'Reilly","alice")).hasSize(1);
        assertThat(documents.search("' OR '1'='1' -- ","alice")).isEmpty();
        assertThat(documents.search("","alice")).allMatch(d->d.owner().equals("alice"));
    }
}
