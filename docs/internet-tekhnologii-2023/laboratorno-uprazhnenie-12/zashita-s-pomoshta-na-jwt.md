---
layout: default
title: Защита с помощта на JWT
parent: Лабораторно упражнение 12
grand_parent: Интернет технологии
nav_order: 2
---

# Защита с помощта на JWT

JWT е компактен начин за прилагане на удостоверяване в модерни уеб приложения. За да го приложим, ще използваме библиотеката jjwt, която е JWT библиотека за Java и Android и се използва за създаване и анализиране на JWT. Трябва да добавим следните зависимости.

```xml
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'io.jsonwebtoken:jjwt-api:0.12.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.5'
```

Следващите стъпки демонстрират как да активирате JWT удостоверяване и упълномощаване  в бекенда. За реализацията им са необходимо от предходното упражнение да сте предвидили:

·        Клас със SecurityFilterChain метод за задаване на правила за упълномощаване;

·        Сервизен клас с метод за вписване на потребителя;

·        Клас, имплементиращ UserDetailsService и неговия метод loadUserByUsername();

·        Контролер с точка за достъп за вписване на потребителя.

### Създаване на JWT токен

1\.      Към application.properties добавете свойства, задаващи тайния ключ (secret) на токена и продължителността на неговата валидност в милисекунди. За криптиране на ключа можете да използвате инструмента: [https://emn178.github.io/online-tools/sha256.html](https://emn178.github.io/online-tools/sha256.html), а за изчисляване на продължителността на валидност на токена в милисекунди - https://www.convertworld.com/en/time/milliseconds.html.

```
security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b
# 1h in millisecond
security.jwt.expiration-time=3600000
```

2. Създайте клас с метод за генериране на токен след успешно удостоверяване на потребителя.

```java
@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .decryptWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }
}
```

3. В персонализирана реализация на SecurityFilterChain допълнете правилата с изискване сесия да не бъде създавана или използвана от Spring Security.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                                auth
                                        .requestMatchers("/auth/**")
                                        .permitAll()
                                        .requestMatchers("api/admin/**")
                                        .hasRole("ADMIN")
                                        .requestMatchers(HttpMethod.DELETE)
                                        .hasRole("ADMIN")
                                        .anyRequest()
                                        .authenticated()
                        )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

4. В метода за вписване заменете добавянето на данните на вписания потребител към сесийния обект с логика по създаване на токен. Нека методът за връща създадения токен като текст.

```java
public String login(LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(), loginDto.getPassword()
                ));

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authentication);

	//Добавяне
        String token = jwtTokenProvider.generateToken(authentication); 
	return token;

	//Премахване
       /* HttpSession session = req.getSession(true);
         session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
         return "User logged-in successfully!";*/
    }
```

5. Добавете клас, с помощта на който създаденият токен да бъде върнат като отговор на клиента.

```java
@Getter
@Builder
public class TokenDto {
    private String token;

    private long expiresIn;
}
```

6. По отношение на точката за достъп за вписване на потребителя задайте отговорът да включва генерирания токен.

```java
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDetails> register(@RequestBody RegisterUserDto registerUserDto) {
        UserDetails registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> authenticate(@RequestBody LoginUserDto loginUserDto) {
        UserDetails authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        TokenDto loginResponse = TokenDto.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();

        return ResponseEntity.ok(loginResponse);
    }
}
```

7. Изпробвайте създадената функционалност за създаване на токен c Postman.

### Оторизация с JWT токен

1\.      Създайте клас, който имплементира интерфейса AuthenticationEntryPoint и неговия метод commence().

AuthenticationEntryPoint се използва за изпращане на HTTP отговор, който да изисква вписване от страна на клиент. Понякога клиентът проактивно включва идентификационни си данни (като потребителско име и парола), за да поиска достъп до ресурс. Тогава Spring Security не трябва да предоставя HTTP отговор, който изисква идентификационни данни от клиента, тъй като те вече са включени.

В други случаи обаче клиентът прави неупълномощена заявка към ресурс, до който не е оторизиран за достъп. В този случай се използва реализация на AuthenticationEntryPoint за изискване на идентификационни данни от клиента. Тя може да извърши пренасочване към страница за влизане, да отговори с хедър WWW-Authenticate или да предприеме друго действие. В нашия случай, тъй като нямаме налична страница за вписване, ще бъде хвърлено authException изключение.

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, 
        HttpServletResponse response, 
        AuthenticationException authException) 
        throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
```

2.      Допълнете класа JWTTokenProvider с методи за:

a.      връщане на потребителско име от подаден токен;

b.      валидиране на подаден токен;

```java
@Component
public class JWTTokenProvider {
    
 //Предходно съдържание

        public String getUsername(String token) {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            return username;
        }

       public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        }
        catch (MalformedJwtException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        }
        catch (ExpiredJwtException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Expired JWT token");
        }
        catch (UnsupportedJwtException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
        }
        catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "JWT string claims is empty");
        }
    }
}
```

3. Създайте потребителски филтър, който да използва създадения токен за удостоверяване на потребителя.

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
```

4. Включете филтъра в SecurityFilterChain метода. Нека той да се изпълнява непосредствено преди UsernamePasswordAuthenticationFilter. Добавете и JwtAuthenticationEntryPoint като ресурс, отговарящ за обработка на изключения, свързани с оторизацията.

```
private JwtAuthenticationEntryPoint authenticationEntryPoint;
private JwtAuthenticationFilter authenticationFilter;

***

http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
http.exceptionHandling(exception -> exception.authenticationEntryPoint (authenticationEntryPoint))

```

5. Изпробвайте функционалността с Postman. Полученият при вписването токен се добавя към заявката към последващ ресурс посредством опцията Bearer token.

<figure><img src="../../../assets/image (163).png" alt=""><figcaption></figcaption></figure>
