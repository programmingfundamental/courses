---
title: "Лабораторно упражнение 2 — Authentication със Spring Security"
sidebar:
  order: 2
  label: "Упражнение 2"
---

# Лабораторно упражнение 2 — Authentication със Spring Security

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

В DB на портала са открити password стойности, които могат директно да се прочетат. Login работи, но изтичане на backup би разкрило паролите. Трябва да възстановите сигурното съхранение и да докажете реален session flow.

## 3. Учебни цели

След упражнението студентът:

- анализира filter chain и AuthenticationProvider;
- разграничава authentication от authorization;
- диагностицира plaintext password storage;
- защитава credentials чрез BCrypt;
- тества login, session и logout;
- аргументира 401 спрямо 403;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](/courses/bg/ueb-sigurnost/podgotovka/). Изпълнени предходните 1 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

## 5. Необходими инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](/courses/bg/ueb-sigurnost/materiali/lab02-authentication/resources/readme/) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** SecurityFilterChain → AuthenticationProvider → Accounts → SecurityContext. Съпоставете с [DFD и endpoints](/courses/bg/ueb-sigurnost/materiali/architecture/system-overview/); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

Authentication проверява identity; authorization определя позволени действия. Spring Security filters обработват request преди Controller. DaoAuthenticationProvider зарежда UserDetails и използва PasswordEncoder; успешната authentication се пази в SecurityContext, а session свързва следващите browser requests с нея.

Password hashing е еднопосочна, умишлено скъпа проверка. Salt е случайна стойност за всеки hash, която пречи еднакви пароли да имат еднакви записи; не е secret. BCrypt има cost и ограничения за дължината в bytes. DelegatingPasswordEncoder записва `{bcrypt}` prefix за алгоритъма. Base64 и reversible encryption не са password storage. Basic token/Bearer authentication предава credential при request; session authentication използва server-side state и cookie. Session identifier също е secret. 401 означава липсваща/невалидна authentication, 403 — отказ за вече установен principal или CSRF rejection.

Технически източници и version scope: [references](/courses/bg/ueb-sigurnost/materiali/architecture/references/).

## 8. Уязвим пример

```java
// Accounts.encode, активен само при LAB_MODE=lab02
return "{noop}" + password; // видимата парола е в DB
```

Примерът е умишлен учебен дефект. За възпроизвеждане използвайте `LAB_MODE=lab02`; не пренасяйте този switch в production.
