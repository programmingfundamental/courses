# Упражнение 3 — Удостоверяване и сесии в Task Manager — решения и насоки


## Решение на примерния проблем

Изтриваме клона с currentAuth и винаги изпълняваме authenticate. След успех:

```java
Authentication authentication;
try {
    authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
} catch (org.springframework.security.core.AuthenticationException ex) {
    throw new org.springframework.web.server.ResponseStatusException(
        org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid credentials");
}
if (httpRequest.getSession(false) != null) httpRequest.changeSessionId();
SecurityContext context = SecurityContextHolder.createEmptyContext();
context.setAuthentication(authentication);
SecurityContextHolder.setContext(context);
httpRequest.getSession(true).setAttribute(
    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
```

Следва съществуващото издаване на AuthResponse за authentication.getName(). Добавяме @Transactional към RefreshTokenService.revokeAllUserTokens. Login=200; грешен login=401 без нови tokens; logout=200 и session.isInvalid()=true. Тестовете използват JSON и реален login, а не само with(user()).

## Решение на самостоятелна задача 1

В AuthController:

```java
@GetMapping("/me")
public java.util.Map<String,Object> me(org.springframework.security.core.Authentication auth) {
    return java.util.Map.of("username", auth.getName(), "roles", auth.getAuthorities().stream()
        .map(org.springframework.security.core.GrantedAuthority::getAuthority).toList());
}
```

Преди публичното правило поставяме requestMatchers("/auth/me").authenticated(); публични остават точно register/login/refresh. Logout изисква удостоверяване. RegisterRequest има @Size(min=3,max=32) за username и @Size(min=12,max=64) за password; в register проверяваме password.getBytes(UTF_8).length<=72 преди encode. При нарушение хвърляме ResponseStatusException(BAD_REQUEST). Не добавяме role в DTO.

Матрица: anonymous /auth/me=401; alice session=200, username=alice, roles=[ROLE_USER], няма password/token; кратки полета и 80 UTF-8 байта=400 без INSERT; нормална регистрация=200 и BCrypt matches=true.

## Решение на самостоятелна задача 2

- 40 кирилски символа са 80 UTF-8 байта: отказ 400 преди encoder и без нов user.
- В сесията на alice login като bob с правилни credentials връща bob и сменя session ID; грешна парола не издава token. Предишната identity не оправдава успех.
- След logout GET /tasks без валидна сесия=401; старият session cookie не възстановява context.
- Две encoder.encode върху една парола дават различни записи, но matches е true и за двата.


## Въпроси за анализ

1. Защо BCrypt записът не трябва да е еднакъв за еднакви пароли?
2. Защо session ID се сменя след login?
3. Как се различава logout на сесия от отмяна на access token?

## Checklist

- [ ] Примерният проблем има работеща реализация в Task Manager.
- [ ] Самостоятелните задачи имат код/анализ и проверими резултати.
- [ ] Тестовете включват разрешен и отказан сценарий.
- [ ] Отказаната операция не променя DB.
- [ ] Изпълнените H2/PostgreSQL и браузърни проверки са разграничени.
- [ ] Отчетът не съдържа пароли, raw tokens или поверителни бележки.
