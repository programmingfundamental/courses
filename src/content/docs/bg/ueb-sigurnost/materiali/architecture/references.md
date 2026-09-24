---
title: "Технически източници"
sidebar:
  order: 100
---

# Технически източници

Проверени при подготовката на 22.09.2026. Следвайте API на фиксираната версия в pom.xml; online документацията може да показва по-нов patch.

- [Spring Security 6.5: CSRF](https://docs.spring.io/spring-security/reference/6.5/servlet/exploits/csrf.html) — default protection на unsafe methods, session token, refresh след authentication.
- [Spring Security 6.5: JWT Resource Server](https://docs.spring.io/spring-security/reference/6.5/servlet/oauth2/resource-server/jwt.html) — decoding, validation и mapping на authorities.
- [Spring Boot 3.5 system requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html) — съвместимост на runtime/build.
- [OWASP XSS Prevention](https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html) — различните output contexts изискват различни controls; CSP е допълнение.
- [OWASP SQL Injection Prevention](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html) — parameter binding и allowlist за identifiers.
- [OWASP Top 10](https://owasp.org/www-project-top-ten/) — ориентир за threat review, не замества application-specific model.
- [Java 21 Cipher API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/crypto/Cipher.html) — GCM/AEAD, AAD и authentication failure.

Примерите в комплекта са авторски, ограничени до учебното приложение. Не прехвърляйте fixtures, ключове или vulnerable switches в production.
