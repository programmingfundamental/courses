# Упражнение 9 — JWT Security и Token Manipulation — насоки

## Решение на примерния проблем

В Tokens конструкторът винаги задава `decoder = verified;`. Премахваме клона с SignedJWT.parse като заместител на проверката. NimbusJwtDecoder използва доверения RSA public key и валидатори за време, issuer, audience, subject и задължителен exp.

Token chain приема Bearer и изисква SCOPE_documents.read. Валиден token → 200; променен payload, чужд подпис или невалидни claims → 401; валиден token без scope → 403. Проверяваме с `mvn test '-Dlab.mode=lab09' '-Dtest=JwtSecurityTest'`.

## Решение на самостоятелна задача 1 — Договор за JWT

### Строга проверка на срока

За договор „невалиден при now ≥ exp“ добавяме изрична проверка към required validator. Така сравнението точно на границата не зависи от граничната семантика на JwtTimestampValidator:

```java
OAuth2TokenValidator<Jwt> required = jwt -> {
    boolean valid = jwt.getAudience().contains("lab-api")
        && jwt.getExpiresAt() != null
        && clock.instant().isBefore(jwt.getExpiresAt())
        && jwt.getSubject() != null && !jwt.getSubject().isBlank();
    return valid ? OAuth2TokenValidatorResult.success()
        : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
};
```

Запазваме DelegatingOAuth2TokenValidator с time, JwtIssuerValidator и required. time има Duration.ZERO и същия Clock. В тестовия контекст подаваме @Primary MutableClock от решението на упражнение 4.

### Подписани случаи с липсващи claims

Промяна само на payload проваля подписа и не доказва claim validation. Добавяме в Tokens пакетен метод за тестове в bg.tuvarna.lab, без нов HTTP endpoint:

```java
String signClaims(JwtClaimsSet claims) {
    return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
}
```

Всеки тест създава нов JwtClaimsSet от основата: issuer=Tokens.ISSUER, audience=[lab-api], subject=alice, issuedAt=t−60s, expiresAt=t+60s, scope=documents.read. За missing-exp не извикваме expiresAt; за missing-sub — subject; за missing-scope — claim("scope",...). Подписваме със signClaims и изпращаме истинска Authorization: Bearer заглавка през MockMvc.

| Единствена промяна | Очакван HTTP резултат |
|---|---|
| Друга или липсваща iss | 401 |
| Друга или липсваща aud | 401 |
| aud=[other,lab-api] | 200 |
| exp преди текущото време или липсващ exp | 401 |
| Липсващ sub или sub="" | 401 |
| Липсващ scope, scope="" или documents.write | 403 |
| Непроменена основа | 200 и subject=alice |

Всеки ред има отделен JUnit тест, чието име посочва причината. Не е необходимо клиентският error response да я разкрива; тя се доказва чрез единствената промяна спрямо валидния token.

Пример за изпълним тестов метод в JwtSecurityTest след добавяне на signClaims; той използва съществуващите tokens и request:

```java
@Test void signedTokenWithoutExpirationIsRejected() throws Exception {
    var claims = org.springframework.security.oauth2.jwt.JwtClaimsSet.builder()
        .issuer(Tokens.ISSUER).audience(java.util.List.of("lab-api"))
        .subject("alice").claim("scope", "documents.read").build();
    request(tokens.signClaims(claims), 401);
}
```

### Граница, session fallback и replay

1. Издаваме един token с exp=t+60s. С управлявания Clock при t+59s → 200, t+60s → 401, t+61s → 401. Не преиздаваме token между проверките.
2. Правим реален /login, вземаме MockHttpSession и извикваме /token-api/documents само със сесията → 401.
3. Валиден Bearer може да се преизползва преди срока. Кратък живот и TLS ограничават replay, но незабавна отмяна изисква denylist/token version или друго server-side състояние.
4. Рестартът генерира нов RSA ключ: token от старата инстанция → 401; нов token → 200. Контролирана rotation изисква постоянни ключове и доверен набор с период на припокриване.

## Решение на самостоятелна задача 2 — Гранични случаи

- **Липсващ exp:** подписаният token без expiresAt се отказва от required validator.
- **aud е списък:** contains("lab-api") приема [other,lab-api], но отказва [other]; не сравняваме сериализиран низ.
- **Точно изтичане/skew:** договорът е now < exp при нулево отклонение. Различен допустим skew се задава изрично заедно с очакванията в теста, без sleep.
- **Липсващ scope/нов signing key:** първият случай е authorization отказ 403; вторият е невалиден подпис 401.

## Въпроси за анализ

1. Защо decode не е verify?
2. Какво гарантира signature?
3. Защо aud се проверява?
4. Защо @WithMockJwt не стига?
5. Защо missing scope е 403?
6. Може ли signed token да се replay-не?

## Checklist

- [ ] Vulnerability reproduced само в lab (за lab01 — unsafe config fixture анализиран).
- [ ] Root cause и trust assumption идентифицирани.
- [ ] Correct mitigation implemented server-side.
- [ ] Regression test показва red → green.
- [ ] Положителната функционалност остава работеща.
- [ ] Edge case има автоматизирана проверка.
- [ ] Самостоятелната задача покрива acceptance criteria.
- [ ] Evidence не съдържа secrets; reset процедурата е проверена.
