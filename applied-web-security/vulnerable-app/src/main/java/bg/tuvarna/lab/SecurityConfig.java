package bg.tuvarna.lab;

import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.*;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean static PasswordEncoder passwordEncoder() { return PasswordEncoderFactories.createDelegatingPasswordEncoder(); }
    @Bean AuthenticationProvider provider(Accounts accounts, PasswordEncoder encoder, LoginGuard guard, LabMode mode) {
        DaoAuthenticationProvider delegate = new DaoAuthenticationProvider(accounts);
        delegate.setPasswordEncoder(encoder);
        return new AuthenticationProvider() {
            public Authentication authenticate(Authentication input) {
                String name=input.getName();
                if (!mode.vulnerable(4) && guard.blocked(name)) throw new LockedException("Invalid credentials");
                try {
                    Authentication result=delegate.authenticate(input);
                    if(!mode.vulnerable(4)) guard.success(name);
                    return result;
                } catch(AuthenticationException e) {
                    if(!mode.vulnerable(4)) guard.failure(name);
                    throw e;
                }
            }
            public boolean supports(Class<?> type) { return UsernamePasswordAuthenticationToken.class.isAssignableFrom(type); }
        };
    }
    @Bean @Order(1) SecurityFilterChain tokenChain(HttpSecurity http, Tokens tokens) throws Exception {
        JwtGrantedAuthoritiesConverter grants=new JwtGrantedAuthoritiesConverter();
        grants.setAuthoritiesClaimName("scope");
        JwtAuthenticationConverter converter=new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grants);
        return http.securityMatcher("/token-api/**").csrf(c->c.disable())
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->a.anyRequest().hasAuthority("SCOPE_documents.read"))
            .oauth2ResourceServer(o->o.jwt(j->j.decoder(tokens.decoder()).jwtAuthenticationConverter(converter)))
            .build();
    }
    @Bean @Order(2) SecurityFilterChain session(HttpSecurity http, AuthenticationProvider provider, LabMode mode) throws Exception {
        http.authenticationProvider(provider)
            .authorizeHttpRequests(a->a.requestMatchers("/", "/health", "/csrf", "/login", "/register", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**", "/comments", "/profile", "/search", "/token").authenticated()
                .anyRequest().denyAll())
            .exceptionHandling(e->e.authenticationEntryPoint((q,r,x)->r.sendError(401)))
            .formLogin(f->f.successHandler((q,r,a)->r.setStatus(204))
                .failureHandler((q,r,e)->r.sendError(401,"Invalid credentials")))
            .logout(l->l.logoutSuccessHandler((q,r,a)->r.setStatus(204)));
        if(mode.vulnerable(7)) http.csrf(c->c.disable());
        if(!mode.vulnerable(6)) http.headers(h->h.contentSecurityPolicy(c->c.policyDirectives("default-src 'none'; form-action 'self'; frame-ancestors 'none'; base-uri 'none'")));
        return http.build();
    }
}
