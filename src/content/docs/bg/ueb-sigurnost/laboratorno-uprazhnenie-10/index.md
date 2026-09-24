---
title: "Лабораторно упражнение 10 — Security Testing и интегрирана защита"
sidebar:
  order: 10
  label: "Упражнение 10"
---

# Лабораторно упражнение 10 — Security Testing и интегрирана защита

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

Преди учебен release е предоставен build с няколко върнати дефекта. Вашата задача е да направите bounded security review, да защитите системата и да докажете, че findings са отстранени без счупена нормална функционалност.

## 3. Учебни цели

След упражнението студентът:

- идентифицира комбинирани regression дефекти;
- класифицира findings и оценява риск;
- диагностицира root cause вместо симптом;
- защитава controls в интегрирания flow;
- тества отрицателни и положителни сценарии;
- аргументира dependency/config/logging residual risks;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](/courses/bg/ueb-sigurnost/podgotovka/). Изпълнени предходните 9 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

## 5. Необходими инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend. OWASP ZAP е незадължителен и се използва само passive срещу localhost. За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](/courses/bg/ueb-sigurnost/materiali/lab10-security-testing/resources/readme/) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** Цялата архитектура → evidence → fixes → regression suite. Съпоставете с [DFD и endpoints](/courses/bg/ueb-sigurnost/materiali/architecture/system-overview/); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

Security regression test пази вече фиксиран security contract. Negative testing проверява forbidden input/action, а positive control доказва, че бизнес функционалността остава работеща. Unit test на helper не доказва wiring в HTTP/filter/DB flow; integration test не е достатъчен за browser enforcement. Defense in depth комбинира независими controls, като всеки има собствена цел.

Dependency vulnerability review изисква конкретен component/version, advisory, affected range и reachability; scanner severity не е автоматично application risk. Configuration review покрива published ports, profiles, secrets, cookies, debug и deny-by-default. Audit event трябва да позволява разследване с correlation ID, без credentials, tokens или sensitive payloads. User-supplied log fields трябва да са ограничени/нормализирани, за да не внесат log injection.

Технически източници и version scope: [references](/courses/bg/ueb-sigurnost/materiali/architecture/references/).

## 8. Уязвим пример

```java
// Пример за дефект, който red test трябва да открие:
Document d = loadById(id);
return d; // липсва object policy
// В lab10 има още контролирани проблеми. Намерете ги чрез evidence.
```

Примерът е умишлен учебен дефект. За възпроизвеждане използвайте `LAB_MODE=lab10`; не пренасяйте този switch в production.
