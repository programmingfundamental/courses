---
title: "Лабораторно упражнение 4 — Brute-Force атаки и защита на Authentication"
sidebar:
  order: 4
  label: "Упражнение 4"
---

# Лабораторно упражнение 4 — Brute-Force атаки и защита на Authentication

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

Няколко последователни грешни пароли не предизвикват никакво ограничение. Трябва да ограничите guessing срещу учебен account, като не позволите permanent lockout да се превърне в лесен denial of service.

## 3. Учебни цели

След упражнението студентът:

- възпроизвежда ограничена локална login simulation;
- разграничава brute force, password guessing и credential stuffing;
- диагностицира account enumeration;
- защитава login с configurable policy;
- тества expiration с контролируем clock;
- аргументира availability trade-offs;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](/courses/bg/ueb-sigurnost/podgotovka/). Изпълнени предходните 3 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

## 5. Необходими инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](/courses/bg/ueb-sigurnost/materiali/lab04-brute-force/resources/readme/) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** AuthenticationProvider → LoginGuard → clock/counters. Съпоставете с [DFD и endpoints](/courses/bg/ueb-sigurnost/materiali/architecture/system-overview/); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

Brute force изпробва множество кандидати; password guessing подбира вероятни пароли; credential stuffing използва вече компрометирани credential pairs и тук се обсъжда само концептуално. Account enumeration е различаване на съществуващи accounts по response, timing или lockout behavior.

Rate limiting ограничава честота, throttling забавя, lockout временно отказва след праг. IP limit може да засегне NAT users и се заобикаля с различни източници; account limit защитава identity, но позволява targeted lockout. Progressive delay нараства с грешките; не блокирайте servlet threads със sleep. Lab policy брои неуспешните опити за точния username; петият failure достига прага, следващият login е blocked за 60 секунди. Отговорът остава generic 401, за да не разкрива state. Clock е dependency за детерминистични tests. Multi-instance deployment изисква споделени атомарни counters.

Технически източници и version scope: [references](/courses/bg/ueb-sigurnost/materiali/architecture/references/).

## 8. Уязвим пример

```java
// lab04 заобикаля limiter-а в provider:
Authentication result = delegate.authenticate(input);
return result; // няма counter, blocking или expiration
```

Примерът е умишлен учебен дефект. За възпроизвеждане използвайте `LAB_MODE=lab04`; не пренасяйте този switch в production.
