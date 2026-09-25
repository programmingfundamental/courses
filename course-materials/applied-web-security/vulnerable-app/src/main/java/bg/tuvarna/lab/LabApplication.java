package bg.tuvarna.lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.Clock;

@SpringBootApplication
public class LabApplication {
    public static void main(String[] args) { SpringApplication.run(LabApplication.class, args); }
    @Bean Clock clock() { return Clock.systemUTC(); }
}
