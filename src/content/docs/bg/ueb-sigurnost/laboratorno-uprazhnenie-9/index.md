---
title: "Лабораторно упражнение 9 — JWT Security и Token Manipulation"
sidebar:
  order: 9
  label: "Упражнение 9"
---

# Лабораторно упражнение 9 — JWT Security и Token Manipulation

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

API endpoint приема identity от декодиран JWT payload, без да проверява подписа. Локална промяна на sub от alice към admin се приема като нова identity, въпреки че signature вече не съответства.

## 3. Учебни цели

След упражнението студентът:

- анализира JWT header/payload/signature;
- възпроизвежда локална payload manipulation;
- разграничава decoding от verification;
- защитава decoder с доверен key и algorithm;
- тества issuer/audience/expiration/scope;
- аргументира replay, storage и revocation ограничения;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](/courses/bg/ueb-sigurnost/podgotovka/). Изпълнени предходните 8 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

## 5. Необходими инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](/courses/bg/ueb-sigurnost/materiali/lab09-jwt-security/resources/readme/) съдържа работна карта и очаквани наблюдения.

## 6. Архитектурен контекст

```text
Browser
   |
Reverse Proxy (Nginx)
   |
Spring Security
   |
Controller
   |
Service
   |
Database (PostgreSQL)
```

**Фокус:** Bearer token → JwtDecoder → claim validators → authorities. Съпоставете с [DFD и endpoints](/courses/bg/ueb-sigurnost/materiali/architecture/system-overview/); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

JWT съдържа Base64url header, payload и signature. Claims като sub (subject), iss (issuer), aud (audience) и exp (expiration) са недоверени преди cryptographic validation. Signing удостоверява integrity/origin при доверен key; не криптира payload. Header alg/kid също не трябва произволно да определя algorithm или key location.

Учебният issuer е локалният Tokens service; `https://issuer.lab.invalid` е exact identifier, не адрес за network calls. NimbusJwtDecoder използва локален RSA public key и RS256; проверява timestamp, issuer, audience, непразен subject и задължителен exp. Scope се map-ва към authority `SCOPE_documents.read`. Валиден token без required scope води до 403; invalid signature/claims — 401. Bearer token позволява replay до expiry; TLS, кратък живот, минимални claims и revocation strategy ограничават риска. Refresh tokens и rotation са концептуална тема, не custom implementation в този lab.

Технически източници и version scope: [references](/courses/bg/ueb-sigurnost/materiali/architecture/references/).

## 8. Уязвим пример

```java
SignedJWT parsed = SignedJWT.parse(raw);
return new Jwt(raw, null, null,
    parsed.getHeader().toJSONObject(), parsed.getJWTClaimsSet().getClaims());
// parse не прави verify!
```

Примерът е умишлен учебен дефект. За възпроизвеждане използвайте `LAB_MODE=lab09`; не пренасяйте този switch в production.
