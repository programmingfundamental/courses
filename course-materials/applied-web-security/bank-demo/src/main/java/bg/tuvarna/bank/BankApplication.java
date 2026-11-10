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
            accounts.add("Ателие Орбита","BG00DEMO00000000000001","0000001001","Месечен абонамент за учебното ателие.");
            accounts.add("Студио Маяк","BG00DEMO00000000000002","0000001002","<b>Преглед на извлечение</b>");
            accounts.add("Фонд Хоризонт","BG00DEMO00000000000003","0000001001","Учебна бележка: [[МАРКЕР]]");
        };
    }
}
