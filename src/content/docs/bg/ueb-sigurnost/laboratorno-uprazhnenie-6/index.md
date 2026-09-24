---
title: "Лабораторно упражнение 6 — Cross-Site Scripting (XSS)"
sidebar:
  order: 6
  label: "Упражнение 6"
---

# Лабораторно упражнение 6 — Cross-Site Scripting (XSS)

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

Alice записва comment, който при преглед от Bob променя заглавието на browser tab. Сървърът третира user content като HTML и го изпълнява в origin на портала.

## 3. Учебни цели

След упражнението студентът:

- възпроизвежда безопасен stored XSS marker;
- разграничава stored, reflected и DOM-based XSS;
- идентифицира HTML/attribute/JavaScript contexts;
- защитава rendering с output encoding;
- тества raw response и CSP;
- аргументира ограниченията на HttpOnly и browser tests;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](/courses/bg/ueb-sigurnost/podgotovka/). Изпълнени предходните 5 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

## 5. Необходими инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](/courses/bg/ueb-sigurnost/materiali/lab06-xss/resources/readme/) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** Stored comment / reflected query → HTML rendering → browser. Съпоставете с [DFD и endpoints](/courses/bg/ueb-sigurnost/materiali/architecture/system-overview/); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

XSS означава изпълнение на недоверено browser съдържание в доверен origin. Stored XSS пази input преди output; reflected връща request input директно; DOM-based XSS възниква при unsafe client-side sinks като innerHTML. HTML text, quoted attribute, JavaScript string и URL са различни contexts.

Output encoding преобразува специалните символи според destination context. Sanitization премахва недопустима структура, когато rich HTML е изрично изискване; тук comments са plain text, затова не добавяме sanitizer библиотека. CSP (Content Security Policy) ограничава позволените sources/actions и е defense in depth, не замества encoding. HttpOnly пречи на script да прочете cookie, но XSS пак може да извиква same-origin actions. Base64 не обезврежда HTML, ако после го декодирате към unsafe sink.

Технически източници и version scope: [references](/courses/bg/ueb-sigurnost/materiali/architecture/references/).

## 8. Уязвим пример

```java
// Web.render в lab06:
return text;
// След това: "<p>" + render(commentBody) + "</p>"
```

Примерът е умишлен учебен дефект. За възпроизвеждане използвайте `LAB_MODE=lab06`; не пренасяйте този switch в production.
