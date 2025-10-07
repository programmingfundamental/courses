---
layout: default
title: Authentication with Spring Security
parent: Laboratory excercise 11
grand_parent: Internet Technologies
nav_order: 5
---


# Authentication with Spring Security

The following outlines some of the key players and concepts in the Spring Security architecture and their relationships.

· SecurityContextHolder - SecurityContextHolder is where Spring Security stores details about who is authenticated.

· SecurityContext - is derived from SecurityContextHolder and contains the authentication of the currently authenticated user.

· Authentication - Can be an input to AuthenticationManager to provide the credentials provided by the authenticating user or the current user from the SecurityContext.

· GrantedAuthority - Authority granted to the principal during authentication (i.e. roles, scopes, etc.)

· AuthenticationManager - An API that defines how Spring Security filters perform authentication.

· ProviderManager - The most common implementation of AuthenticationManager.

· AuthenticationProvider - Used by ProviderManager to perform a specific type of authentication.

<figure><img src="../../../assets/security1.png" alt=""><figcaption></figcaption></figure>

## Authentication Manager

AuthenticationManager is the main authentication interface. If the input authentication principal is valid and verified, its authenticate() method returns an Authentication instance with the authentication flag set to true. Otherwise, if the principal is not valid, it will throw an AuthenticationException. In a last resort, it returns null if it cannot resolve.

ProviderManager is the default implementation of AuthenticationManager. It delegates the authentication process to a list of AuthenticationProvider instances.

Configure the default implementation of AuthenticationManager.

```java
@Bean
public AuthenticationManager authenticationManager(
AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
}
```
Example user login method:

```java
public String login(HttpServletRequest req, LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(), loginDto.getPassword()));

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authentication);

        HttpSession session = req.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
        return "User logged-in successfully!";
    }
```
## AuthenticationProvider

Spring Security provides various options for performing authentication. These options follow a simple logic: the authentication request is handled by an AuthenticationProvider and a fully authenticated object with complete credentials is returned. The standard and most common implementation is the DaoAuthenticationProvider, which retrieves the user data from a simple read-only user DAO - UserDetailsService. This user details service only has access to the username to retrieve the complete user object, which is sufficient for most scenarios.

## UserDetailsService

UserDetailsService is a core interface in the Spring Security framework that is used to retrieve user authentication and authorization information. This interface has only one method called loadUserByUsername() that we can implement to pass the client information to the Spring Security API. DaoAuthenticationProvider will use this information to load the user information during the authentication process.

Example of method implementation:

```java
// Some @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Not valid username or password"));

        Set<GrantedAuthority> authorities = user
                .getRoles()
                .stream()
                .map((role)->new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }
```

## PasswordEncoder

The PasswordEncoder interface defines the encode() method to convert the plain password to an encoded form and the matches() method to compare the plain password with the encoded password. Each specific encoder has a default constructor that creates an instance with the default work factor.

```java
@Bean
public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```
