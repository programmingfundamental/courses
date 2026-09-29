# Упражнение 10 — JWT, отделен Bearer API и refresh rotation — решения и насоки


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

В @Order(1) chain поставяме securityMatcher("/token-api/**"), STATELESS, NullSecurityContextRepository, csrf.disable(), httpBasic.disable(), formLogin.disable(), requestMatchers("/token-api/admin/**").hasRole("ADMIN") и anyRequest.authenticated(); добавяме JWT filter пред UsernamePasswordAuthenticationFilter. Session chain е @Order(2), пази CSRF и няма JWT filter. TokenTaskController използва същия TaskService и TaskPolicy. За ръчна проверка на 403 може да добавим GET /token-api/admin/status, връщащ фиксиран status само за ADMIN.

## Решение на самостоятелна задача 1

Преподавателят подготвя локално примерни tokens с учебния signing key, отделно от production issue метода. Не променяме само payload за missing-exp, защото това проверява подписа. Студентите копират token в Postman Bearer Token: correct=200; чужд ключ/payload mutation/wrong issuer/wrong audience/missing exp/empty or missing sub=401; aud=[other,task-manager-api]=200; disabled user=401; валиден USER към admin status=403. Реална login сесия без Bearer към /token-api/tasks=401. Неподготвените claim случаи се отбелязват като неизпълнени.

Използваме token с кратък срок и изпращаме Postman заявка преди и след изтичането: 200, после 401. Точната граница изисква контролиран Clock в упражнение 11. При смяна на signing key старият token е 401; рестарт със същия ключ не го обезсилва. Възстановяваме конфигурацията след проверката.

## Решение на самостоятелна задача 2

Изменяме RefreshToken: tokenDigest unique вместо raw token; пазим username, createdAt, expiryDate, revoked. Миграцията отменя старите raw записи и изисква нов login. Генерираме 32 случайни байта със SecureRandom, Base64url без padding; SHA-256 digest се пази в DB. Raw стойността се връща чрез отделен IssuedRefresh(recordEntity,rawValue), не чрез getToken на entity.

Repository метод findByTokenDigest има @Lock(PESSIMISTIC_WRITE); RefreshTokenService.rotate е @Transactional: digest вход → заключен ред → !revoked и now<expiryDate → активен user → revoked=true → издаване/запис на нов digest → връщане на raw нов token и access token. AuthService.refresh делегира цялата операция на тази транзакция. Повторна употреба е 401. Revoke-all маркира редовете revoked в транзакция; редът на заключванията е постоянен. Logout също инвалидира сесията. На PostgreSQL две едновременни refresh заявки в отделни транзакции имат един успех и един 401, с точно един активен наследник.

Ръчни проверки: wrong/expired/used digest=401; disabled user=401 без нов token; след expiry отказ; valid refresh=200 с различна raw стойност; DB не съдържа нито старата, нито новата raw стойност. След logout refresh е отказан, но access token остава валиден до exp при активен user. За незабавно access revocation е нужен отделен token version/denylist, който не е част от този договор.


## Въпроси за анализ

1. Как различавате parsing от проверен подпис?
2. Защо сесията не трябва да спасява невалиден Bearer?
3. Какво става при два едновременни refresh опита?

## Checklist

- [ ] Примерният проблем има работеща реализация в Task Manager.
- [ ] Самостоятелните задачи имат код/анализ и проверими резултати.
- [ ] Ръчните проверки включват разрешен и отказан сценарий.
- [ ] Отказаната операция не променя DB.
- [ ] Ръчните API проверки с Postman, DB наблюденията и браузърните проверки са разграничени.
- [ ] Отчетът не съдържа пароли, raw tokens или поверителни бележки.
