package bg.tuvarna.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BankApplication {
    public static void main(String[] args) { SpringApplication.run(BankApplication.class, args); }

    @Bean ApplicationRunner seed(JdbcTemplate db, PasswordEncoder encoder, Accounts accounts) {
        return args -> {
            db.update("insert into bank_users(login,password_hash) values (?,?)","admin",encoder.encode("admin"));
            for (SampleAccount sample : SampleAccount.EXAMPLES) {
                accounts.add(sample.alias(), sample.iban(), sample.egn(), sample.note());
            }
        };
    }
}
