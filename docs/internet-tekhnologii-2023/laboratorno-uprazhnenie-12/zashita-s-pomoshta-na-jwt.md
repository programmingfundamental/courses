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
@Component
public class JWTTokenProvider {

    @Value("${security.jwt.secret-key}")
    private String JWTSecret;

    @Value("${security.jwt.expiration-time}")
    private int JWTExpirationDate;

    //промяна
    public String generateToken(Authentication authentication) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + JWTExpirationDate);

        String token = Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(key())
                .compact();
        return token;
    }

    private SecretKey key() {
        byte[] keyBytes = Decoders.BASE64.decode(JWTSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

3. В персонализирана реализация на SecurityFilterChain допълнете правилата с изискване сесия да не бъде създавана или използвана от Spring Security.

```java
http.sessionManagement(session -> 
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
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
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
}
```

6. По отношение на точката за достъп за вписване на потребителя задайте отговорът да включва генерирания токен.

```java
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@RequestBody LoginDto loginDto) {
    String token = authService.login(loginDto);
    AuthResponse authResponse = new AuthResponse();
    authResponse.setAccessToken(token);
    return new ResponseEntity<>(authResponse, HttpStatus.OK);
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
       Claims claims =  Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

     public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parse(token);
            return true;
        }
        catch (MalformedJwtException ex) {
            throw new TaskApiException("Invalid JWT token");
        }
        catch (ExpiredJwtException ex) {
            throw new TaskApiException("Expired JWT token");
        }
        catch (UnsupportedJwtException ex) {
            throw new TaskApiException("Unsupported JWT token");
        }
        catch (IllegalArgumentException ex) {
            throw new TaskApiException("JWT string claims is empty");
        }
    }
}
```

3. Създайте потребителски филтър, който да използва създадения токен за удостоверяване на потребителя.

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JWTTokenProvider jwtTokenProvider;
    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JWTTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);
        if(token!=null && !token.isEmpty() && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }
        filterChain.doFilter(request,response);
    }

    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

4. Включете филтъра в SecurityFilterChain метода. Нека той да се изпълнява непосредствено преди UsernamePasswordAuthenticationFilter. Добавете и JwtAuthenticationEntryPoint като ресурс, отговарящ за обработка на изключения, свързани с оторизацията.

```
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    private JwtAuthenticationFilter authenticationFilter;

***

http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
http.exceptionHandling(exception -> exception.authenticationEntryPoint (authenticationEntryPoint));

```

5. Изпробвайте функционалността с Postman. Полученият при вписването токен се добавя към заявката към последващ ресурс посредством опцията Bearer token.

<figure><img src="../../assets/image (163).png" alt=""><figcaption></figcaption></figure>
