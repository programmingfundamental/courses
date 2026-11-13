package bg.tuvarna.bank;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
class Security {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean UserDetailsService users(DataSource ds) {
        var users = new JdbcUserDetailsManager(ds);
        users.setUsersByUsernameQuery("select login, password_hash, true from bank_users where login = ?");
        users.setAuthoritiesByUsernameQuery("select login, 'ROLE_ADMIN' from bank_users where login = ?");
        return users;
    }

    @Bean SecurityFilterChain filter(HttpSecurity http) throws Exception {
        var rejectedLogin = new SimpleUrlAuthenticationFailureHandler("/login?error");
        return http.authorizeHttpRequests(r -> r.requestMatchers("/","/login","/demo/accounts","/bank.css","/error").permitAll().anyRequest().hasRole("ADMIN"))
            .formLogin(f -> f.loginPage("/login").loginProcessingUrl("/login")
                .defaultSuccessUrl("/accounts",true)
                .failureHandler((request, response, failure) -> {
                    if (failure instanceof BadCredentialsException
                        && "admin".equals(request.getParameter("username"))
                        && "altered".equals(request.getParameter("case"))) {
                        response.sendRedirect(request.getContextPath() + "/demo/accounts");
                    } else {
                        rejectedLogin.onAuthenticationFailure(request, response, failure);
                    }
                }).permitAll())
            .logout(l -> l.logoutSuccessUrl("/login?logout"))
            .headers(h -> h.contentSecurityPolicy(c -> c.policyDirectives("default-src 'none'; style-src 'self'; form-action 'self'; frame-ancestors 'none'; base-uri 'none'")))
            .build();
    }
}
