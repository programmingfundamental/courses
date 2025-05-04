---
layout: default
title: Security using JWT
parent: Laboratory excercise 12
grand_parent: Internet Technologies
nav_order: 2
---


# Security using JWT

JWT is a compact way to implement authentication in modern web applications. To implement it, we will use the jjwt library, which is a JWT library for Java and Android and is used to create and parse JWTs. We need to add the following dependencies.

```xml
<!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- JJWT API -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.5</version>
    </dependency>

    <!-- JJWT Impl (runtime only) -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>

    <!-- JJWT Jackson (runtime only) -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
```

The following steps demonstrate how to enable JWT authentication and authorization in the backend. To implement them, you need to have foreseen from the previous exercise:

· A class with a SecurityFilterChain method for setting authorization rules;

· A service class with a method for logging in the user;

· A class implementing UserDetailsService and its method loadUserByUsername();

· A controller with an access point for logging in the user.

### Creating a JWT token

1. Add properties to application.properties that specify the token's secret and its validity duration in milliseconds. To encrypt the key, you can use the tool: [https://emn178.github.io/online-tools/sha256.html](https://emn178.github.io/online-tools/sha256.html), and to calculate the duration of token validity in milliseconds - https://www.convertworld.com/en/time/milliseconds.html.

```
security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b
# 1h in millisecond
security.jwt.expiration-time=3600000
```

2. Create a class with a method to generate a token after successful user authentication.

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

3. In a custom implementation of SecurityFilterChain, supplement the rules with a requirement that a session should not be created or used by Spring Security.

```java
http.sessionManagement(session -> 
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
```

4. In the login method, replace adding the logged-in user's data to the session object with token creation logic. Let the method return the created token as text.

```java
 public String login(LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(), loginDto.getPassword()
                ));

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authentication);

        // Add the code
        String token = jwtTokenProvider.generateToken(authentication);
        return token;

        // Remove the code
       /* HttpSession session = req.getSession(true);
         session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
         return "User logged-in successfully!";*/
    }
```

5. Add a class to use to return the created token as a response to the client.

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

6. Regarding the user login access point, set the response to include the generated token.

```java
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@RequestBody LoginDto loginDto) {
    String token = authService.login(loginDto);
    AuthResponse authResponse = new AuthResponse();
    authResponse.setAccessToken(token);
    return new ResponseEntity<>(authResponse, HttpStatus.OK);
}
```

7. Test the created token creation functionality with Postman.

### Authorization with JWT token

1\. Create a class that implements the AuthenticationEntryPoint interface and its commence() method.

AuthenticationEntryPoint is used to send an HTTP response that requires a client to log in. Sometimes, a client proactively includes its credentials (such as username and password) to request access to a resource. In that case, Spring Security does not need to provide an HTTP response that requires credentials from the client, since they are already included.

In other cases, however, the client makes an unauthorized request to a resource that it is not authorized to access. In this case, an implementation of AuthenticationEntryPoint is used to require credentials from the client. It can redirect to a login page, respond with a WWW-Authenticate header, or take some other action. In our case, since we do not have a login page available, an authException exception will be thrown.

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

2. Add methods to the JWTTokenProvider class to:

a. return a username from a submitted token;

b. validate a submitted token;

```java
@Component
public class JWTTokenProvider {
    
 //Prevoius content

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

3. Create a user filter that will use the created user authentication token.

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

4. Include the filter in the SecurityFilterChain method. Let it run immediately before the UsernamePasswordAuthenticationFilter. Also add the JwtAuthenticationEntryPoint as a resource responsible for handling exceptions related to authorization.

```java
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    private JwtAuthenticationFilter authenticationFilter;

***

http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
http.exceptionHandling(exception -> exception.authenticationEntryPoint (authenticationEntryPoint));

```

5. Test the functionality with Postman. The token received during login is added to the request to a subsequent resource using the Bearer token option.

<figure><img src="../../../assets/image (163).png" alt=""><figcaption></figcaption></figure>
