---
title: "Лабораторно упражнение 7 — CSRF, Cookies и Browser Security"
sidebar:
  order: 7
  label: "Упражнение 7"
---

# Лабораторно упражнение 7 — CSRF, Cookies и Browser Security

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

Alice е влязла в портала. Друга локална страница съдържа форма, която променя display name през нейната session. Приложението проверява кой е user, но не проверява дали request идва от легитимния flow.

## 3. Учебни цели

След упражнението студентът:

- възпроизвежда локален CSRF с browser cookie;
- анализира SOP, origin и site;
- разграничава CORS от CSRF protection;
- защитава session mutations с CSRF token;
- тества липсващ/невалиден token и cookie flags;
- аргументира Secure, HttpOnly, SameSite и session fixation;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](/courses/bg/ueb-sigurnost/podgotovka/). Изпълнени предходните 6 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

## 5. Необходими инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](/courses/bg/ueb-sigurnost/materiali/lab07-csrf-browser-security/resources/readme/) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** Browser cookie policy → CsrfFilter → state-changing Controller. Съпоставете с [DFD и endpoints](/courses/bg/ueb-sigurnost/materiali/architecture/system-overview/); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

CSRF (Cross-Site Request Forgery) използва автоматично приложените browser credentials за нежелано действие. Same-Origin Policy (SOP) ограничава четенето на cross-origin responses; не забранява всички изпращания, например HTML form POST. Origin включва scheme, host и port, докато site не се различава само по port. localhost:8080 и localhost:8081 тук са cross-origin, но same-site, затова SameSite=Lax не спира демонстрацията.

HttpOnly ограничава достъпа до cookie през JavaScript. Secure изисква secure transport в нормалната browser policy; SameSite ограничава cross-site изпращания, но не е заместител на CSRF token. CORS определя кои origins могат да четат/използват responses през script; не доказва намерение на user. CSRF token е unpredictable session-bound стойност, която server сравнява при mutation. Session fixation означава запазване на идентификатор, наложен преди login; Spring сменя session identity при authentication.

Технически източници и version scope: [references](/courses/bg/ueb-sigurnost/materiali/architecture/references/).

## 8. Уязвим пример

```java
// Умишлено само в lab07; дефект за session chain:
http.csrf(csrf -> csrf.disable());
```

Примерът е умишлен учебен дефект. За възпроизвеждане използвайте `LAB_MODE=lab07`; не пренасяйте този switch в production.
