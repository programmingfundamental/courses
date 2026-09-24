---
title: "Лабораторно упражнение 3 — Authorization и Broken Access Control"
sidebar:
  order: 3
  label: "Упражнение 3"
---

# Лабораторно упражнение 3 — Authorization и Broken Access Control

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

Alice отваря собствен документ с ID=1. При промяна на URL към ID=2 вижда фактура на Bob. Login е правилен; дефектът е в решението дали конкретната identity има право върху конкретния object.

## 3. Учебни цели

След упражнението студентът:

- възпроизвежда локален IDOR;
- анализира endpoint и object-level authorization;
- разграничава horizontal и vertical escalation;
- защитава ownership на service ниво;
- тества policy матрица;
- аргументира 404/403 и residual risk;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](/courses/bg/ueb-sigurnost/podgotovka/). Изпълнени предходните 2 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

## 5. Необходими инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](/courses/bg/ueb-sigurnost/materiali/lab03-authorization/resources/readme/) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** Controller → Documents.get → object ownership policy. Съпоставете с [DFD и endpoints](/courses/bg/ueb-sigurnost/materiali/architecture/system-overview/); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

Role е груба роля, authority — конкретно разрешение. RBAC е управление по роли; ownership добавя връзка между principal и resource. IDOR (Insecure Direct Object Reference) възниква, когато сменяем идентификатор води до object без проверка на тази връзка. Horizontal escalation пресича users на едно ниво, vertical escalation — user/admin права.

Endpoint-level проверка от типа authenticated е необходима, но недостатъчна. Method-level @PreAuthorize покрива service entry, а object policy трябва да проверява заредения owner или да ограничи самия query. В този курс USER чете само собствен, ADMIN — всички по ID, anonymous — никой. 404 при чужд и липсващ ID намалява existence disclosure; това е договор, не заместител на policy. UUID усложнява guessing, но не е authorization.

Технически източници и version scope: [references](/courses/bg/ueb-sigurnost/materiali/architecture/references/).

## 8. Уязвим пример

```java
// Уязвимата логика в Documents.get:
Document d = loadById(id);
return d; // authenticated != owner
```

Примерът е умишлен учебен дефект. За възпроизвеждане използвайте `LAB_MODE=lab03`; не пренасяйте този switch в production.
