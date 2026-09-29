# Упражнение 9 — JWT, отделен Bearer API и refresh rotation — решения и насоки


## Решение на примерния проблем

JwtService използва Clock за издаване и parser.clock(() -> Date.from(clock.instant())). Издаването добавя .issuer("task-manager").audience().add("task-manager-api").and() към съществуващия builder. Parser verifyWith(getKey()) проверява подписа; след parseSignedClaims проверяваме header.getAlgorithm().equals("HS256"), sub!=null&&!sub.isBlank(), exp!=null&&now.before(exp), iss="task-manager" и audience съдържа task-manager-api. При която и да е грешка връщаме 401 без payload в лога. Алгоритъмът и ключът не се избират от входен URL/kid.

JwtAuthFilter получава валидирания subject, зарежда UserDetails и проверява isEnabled/isAccountNonLocked/isAccountNonExpired/isCredentialsNonExpired преди context. Unknown user/invalid signature/claim се улавят и sendError(401), без продължаване на chain или session fallback. Filter bean се изключва от автоматичната регистрация:

```java
@Bean
org.springframework.boot.web.servlet.FilterRegistrationBean<JwtAuthFilter> jwtRegistration(JwtAuthFilter filter) {
    var registration = new org.springframework.boot.web.servlet.FilterRegistrationBean<>(filter);
    registration.setEnabled(false);
    return registration;
}
```

В @Order(1) chain поставяме securityMatcher("/token-api/**"), STATELESS, NullSecurityContextRepository, csrf.disable(), httpBasic.disable(), formLogin.disable(), requestMatchers("/token-api/admin/**").hasRole("ADMIN") и anyRequest.authenticated(); добавяме JWT filter пред UsernamePasswordAuthenticationFilter. Session chain е @Order(2), пази CSRF и няма JWT filter. TokenTaskController използва същия TaskService и TaskPolicy. За 403 тест може да добавим GET /token-api/admin/status, връщащ фиксиран status само за ADMIN.

## Решение на самостоятелна задача 1

Тестовият fixture подписва claims чрез Jwts.builder().signWith(същияTestKey,Jwts.SIG.HS256), отделно от production issue метода; не променяме само payload за missing-exp, защото това би тествало подписа. Матрица: correct=200; чужд ключ/payload mutation/wrong issuer/wrong audience/missing exp/empty or missing sub=401; aud=[other,task-manager-api]=200; disabled user=401; валиден USER към admin status=403. Реален login session без Bearer към /token-api/tasks=401.

Издаваме един token с exp=t+60s и движим Clock: t+59=200, t+60=401, t+61=401. При смяна на конфигурирания signing key старият token е 401. Рестарт със същия .env ключ не го обезсилва автоматично — за разлика от временен генериран ключ. Изчистваме security context между unit cases; HTTP тестът се изпълнява през реалната chain.

## Решение на самостоятелна задача 2

Изменяме RefreshToken: tokenDigest unique вместо raw token; пазим username, createdAt, expiryDate, revoked. Миграцията отменя старите raw записи и изисква нов login. Генерираме 32 случайни байта със SecureRandom, Base64url без padding; SHA-256 digest се пази в DB. Raw стойността се връща чрез отделен IssuedRefresh(recordEntity,rawValue), не чрез getToken на entity.

Repository метод findByTokenDigest има @Lock(PESSIMISTIC_WRITE); RefreshTokenService.rotate е @Transactional: digest вход → заключен ред → !revoked и now<expiryDate → активен user → revoked=true → издаване/запис на нов digest → връщане на raw нов token и access token. AuthService.refresh делегира цялата операция на тази транзакция. Повторна употреба е 401. Revoke-all маркира редовете revoked в транзакция; редът на заключванията е постоянен. Logout също инвалидира сесията. На PostgreSQL две едновременни refresh заявки в отделни транзакции имат един успех и един 401, с точно един активен наследник.

Tests: wrong/expired/used digest=401; disabled user=401 без нов token; на expiry точно отказ; valid refresh=200 с различна raw стойност; DB не съдържа нито старата, нито новата raw стойност. След logout refresh е отказан, но access token остава валиден до exp при активен user. За незабавно access revocation е нужен отделен token version/denylist, който не е част от този договор.


## Въпроси за анализ

1. Как различавате parsing от проверен подпис?
2. Защо сесията не трябва да спасява невалиден Bearer?
3. Какво става при два едновременни refresh опита?

## Checklist

- [ ] Примерният проблем има работеща реализация в Task Manager.
- [ ] Самостоятелните задачи имат код/анализ и проверими резултати.
- [ ] Тестовете включват разрешен и отказан сценарий.
- [ ] Отказаната операция не променя DB.
- [ ] Изпълнените H2/PostgreSQL и браузърни проверки са разграничени.
- [ ] Отчетът не съдържа пароли, raw tokens или поверителни бележки.
