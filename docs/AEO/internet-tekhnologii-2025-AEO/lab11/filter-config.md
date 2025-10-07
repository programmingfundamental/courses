---
layout: default
title: Filter config
parent: Laboratory excercise 11
grand_parent: Internet Technologies
nav_order: 3
---



# Filter config

The way Spring actually works with HttpSecurity is through a series of security filters. It essentially creates a security filter chain. Take a closer look at the output in the debug console when you run the application:

<figure><img src="../../../assets/image (162).png" alt=""><figcaption></figcaption></figure>

As you can see, Spring takes the settings in our custom security configuration and places them in a number of predefined (default) security filters. The list of these filters is actually an ordered list of filters that Spring manages, and they all form a DefaultSecurityFilterChain.

The new way to do security that Spring supports is to use our custom http security filters (in the security filter chain) instead of configuring them via a class that must extend WebSecurityConfigurerAdapter. Essentially, Spring allows us to configure our HttpSecurity using a bean method that constructs and returns our custom implementation of the SecurityFilterChain interface.

```java
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("api/auth/**")
                                .permitAll()
                                .requestMatchers("api/admin/**")
                                .hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE)
                                .hasRole("ADMIN")
                                .anyRequest()
                                .authenticated())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED))
                .logout(logout -> logout
                        .logoutUrl("api/auth/logout")
                        .invalidateHttpSession(true));

        return http.build();
    }
```
