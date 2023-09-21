# Удостоверяване със Spring Security

В изложението по-долу са представени някои от основните участници и понятия в архитектурата на Spring Security и взаимовръзките между тях. &#x20;

* SecurityContextHolder - SecurityContextHolder е мястото, където Spring Security съхранява подробности за това кой е удостоверен.
* SecurityContext - получава се от SecurityContextHolder и съдържа удостоверяването на текущо удостоверения потребител.
* Authentication - Може да бъде вход към AuthenticationManager за предоставяне на идентификационните данни, предоставени от потребител за удостоверяване или текущия потребител от SecurityContext.
* GrantedAuthority - правомощие, което се предоставя на принципала при удостоверяването (т.е. роли, обхвати и т.н.)
* AuthenticationManager - API, който определя как филтрите на Spring Security извършват удостоверяване.
* ProviderManager - най-често срещаната реализация на AuthenticationManager.
* AuthenticationProvider - използва се от ProviderManager за извършване на определен тип удостоверяване.

<figure><img src="../../../.gitbook/assets/image (157).png" alt=""><figcaption></figcaption></figure>

### Authentication Manager

AuthenticationManager е основният  интерфейс за удостоверяване. Ако принципалът на входното удостоверяване е валиден и проверен, неговият метод authenticate() връща инстанция на _Authentication_ с флаг за удостоверяване, зададен на true. В противен случай, ако принципалът не е валиден, той ще хвърли AuthenticationException. В краен случай връща нула, ако не може да реши.

ProviderManager е имплементацията по подразбиране на AuthenticationManager. Той делегира процеса на удостоверяване на списък от екземпляри на AuthenticationProvider.

Конфигуриране на имплементацията по подразбиране на AuthenticationManager.

```java
@Bean
public AuthenticationManager authenticationManager(
AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
}
```

Примерен метод за вписване на потребителя:

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

### AuthenticationProvider

Spring Security предоставя различни опции за извършване на удостоверяване. Тези опции следват проста логика: заявката за удостоверяване се обработва от AuthenticationProvider и се връща напълно удостоверен обект с пълни идентификационни данни. Стандартната и най-често срещана реализация е DaoAuthenticationProvider, която извлича потребителските данни от прост потребителски DAO само за четене - UserDetailsService. Тази услуга за потребителски подробности има достъп само до потребителското име, за да извлече пълния потребителски обект, което е достатъчно за повечето сценарии.

### UserDetailsService

UserDetailsService е основен интерфейс в Spring Security framework, който се използва за извличане на информация за удостоверяване и оторизация на потребителя. Този интерфейс има само един метод, наречен loadUserByUsername(), който можем да внедрим, за да подадем клиентската информация към API за защита на Spring. DaoAuthenticationProvider ще използва тази информация, за да зареди потребителската информация по време на процеса на удостоверяване.

Пример за имплементация на метода:

```java
// Some @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username or email" + username ));

        Set<GrantedAuthority> authorities = user
                .getRoles()
                .stream()
                .map((role)->new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }
```

### PasswordEncoder

Интерфейсът PasswordEncoder дефинира метода encode() за преобразуване на обикновената парола в кодирана форма и метода matches() за сравняване на обикновена парола с кодираната парола. Всеки конкретен енкодер има конструктор по подразбиране, който създава екземпляр с работния фактор по подразбиране.

```java
@Bean
public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```
