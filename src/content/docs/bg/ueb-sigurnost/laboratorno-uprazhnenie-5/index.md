---
title: "Лабораторно упражнение 5 — SQL Injection"
sidebar:
  order: 5
  label: "Упражнение 5"
---

# Лабораторно упражнение 5 — SQL Injection

**Продължителност:** 110 минути. **Аудитория:** IV курс, бакалавър „Киберсигурност“.

> Всички offensive действия в курса се изпълняват единствено срещу предоставената локална лабораторна среда.

## 2. Security сценарий

Search трябва да показва само документите на текущия user. Един search string променя структурата на SQL условието и резултатите вече съдържат Bob документи при Alice session.

## 3. Учебни цели

След упражнението студентът:

- анализира unsafe SQL concatenation;
- възпроизвежда недеструктивен injection;
- диагностицира нарушената code/data граница;
- защитава query с parameter binding;
- тества SQL semantics в PostgreSQL;
- аргументира validation и least privilege като допълващи controls;

## 4. Предварителни знания

Java, Spring Boot, HTTP, SQL, client–server, Linux/Docker и мрежи на нивото, описано в [README](/courses/bg/ueb-sigurnost/podgotovka/). Изпълнени предходните 4 упражнения и съхранени техните regression tests. Елементарните Java конструкции не се преговарят.

## 5. Необходими инструменти

JDK 21, Maven 3.9+, Docker/Compose, IDE, curl.exe или PowerShell, browser DevTools, JUnit, Spring Boot Test и MockMvc. PostgreSQL работи в Compose; H2 е само бърз test backend.  За пълния security profile е нужен Testcontainers достъп до Docker. [Resources](/courses/bg/ueb-sigurnost/materiali/lab05-sql-injection/resources/readme/) съдържа работна карта и очаквани наблюдения.

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

**Фокус:** Documents.search → JDBC query → DB parser. Съпоставете с [DFD и endpoints](/courses/bg/ueb-sigurnost/materiali/architecture/system-overview/); отбележете кой input е недоверен и къде се взема security решението.

## 7. Необходима теория

SQL Injection възниква когато недоверен input става част от изпълнимата структура на SQL. Prepared statement отделя структурата от bind values. Input validation ограничава допустим domain/размер, но не заменя parameterized SQL. Escaping на apostrophe не е обща защита за всички dialects/contexts.

JPA не прави concatenated JPQL/native SQL безопасен; setParameter/позиционни placeholders са нужни и там. Bind parameters са за стойности, не за column/table/ORDER BY identifiers; dynamic identifiers изискват server-side allowlist. LIKE wildcard `%` е search semantics, не SQL injection; parameter binding запазва wildcard поведението. Runtime DB role трябва да има минимални права, но дори read-only injection може да наруши confidentiality. Generic error handling не трябва да връща SQL/schema/stack trace.

Технически източници и version scope: [references](/courses/bg/ueb-sigurnost/materiali/architecture/references/).

## 8. Уязвим пример

```java
String sql = "SELECT id,owner,title FROM documents WHERE owner='"
    + owner + "' AND title LIKE '%" + q + "%' ORDER BY id";
return db.query(sql, rowMapper);
```

Примерът е умишлен учебен дефект. За възпроизвеждане използвайте `LAB_MODE=lab05`; не пренасяйте този switch в production.
