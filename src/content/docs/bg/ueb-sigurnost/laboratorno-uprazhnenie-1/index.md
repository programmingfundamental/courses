---
title: "Лабораторно упражнение 1 — Лабораторна среда и Threat Modeling"
sidebar:
  order: 1
  label: "Упражнение 1"
---

# Лабораторно упражнение 1 — Лабораторна среда и Threat Modeling

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

Екипът добавя учебен портал и твърди, че „щом е зад Nginx, е защитен“. Преди да оцените конкретен exploit, трябва да установите кой може да достигне всеки компонент и какви данни преминават през него.

## 3. Учебни цели

След упражнението студентът:

- анализира data flows от browser до DB;
- идентифицира assets и attack surface;
- разграничава threat, vulnerability, exploit, risk и mitigation;
- диагностицира излишно публикуван port;
- аргументира пет threats чрез STRIDE;
- тества deny-by-default и network exposure;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](/courses/bg/ueb-sigurnost/podgotovka/). Не се изисква предварително познаване на конкретен exploit. Елементарните Java конструкции не се преговарят.

## 5. Необходими инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](/courses/bg/ueb-sigurnost/materiali/lab01-threat-modeling/resources/readme/) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** Compose, Nginx, endpoint inventory и trust boundaries. Съпоставете с [DFD и endpoints](/courses/bg/ueb-sigurnost/materiali/architecture/system-overview/); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

Asset е ценност: документи, самоличност, ключ, availability. Threat е нежелано събитие, vulnerability — причиняваща слабост, exploit — конкретно използване, risk — likelihood × impact, mitigation — намаляващ риска control. Threat actor има capability и достъп; не приемайте, че anonymous и authenticated user имат еднаква позиция.

Trust boundary се преминава при промяна на доверие, например client → server и app → DB. Reverse proxy не валидира ownership. STRIDE групира Spoofing, Tampering, Repudiation, Information disclosure, Denial of service и Elevation of privilege. OWASP Top 10 е ориентир за пропуски, а не доказателство за сигурност. В курса използваме likelihood/impact 1–3 с изрично описани предпоставки.

Технически източници и version scope: [references](/courses/bg/ueb-sigurnost/materiali/architecture/references/).

## 8. Уязвим пример

```yaml
# Unsafe пример за сравнение, НЕ го активирайте:
services:
  db:
    ports: ["5432:5432"]  # host interfaces, заобикаля app policy
```

Примерът е умишлен учебен дефект. Сравнявайте го като текстов fixture; не отваряйте DB port.
