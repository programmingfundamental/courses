---
title: "Упражнение 5 — Безопасно търсене на задачи със Spring Data JPA"
sidebar:
  order: 5
  label: "Упражнение 5"
---

# Упражнение 5 — Безопасно търсене на задачи със Spring Data JPA

## 1. Теория


### 1.1. SQL/JPQL структура и входни стойности

1. **SQL Injection** възниква, когато вход става изпълним синтаксис. **Binding** подава стойност отделно от структурата; **placeholder** е място за параметър.
   - `WHERE t.summary = :summary` с @Param обработва O'Reilly като текст. Конкатенация `"...='"+input+"'"` смесва данни и синтаксис.
2. **JPA** работи с entities; **JPQL** използва имена на Java полета; **native query** изпълнява SQL към таблици.
   - В JPQL owner е `t.owner.username`, а в SQL е join към users чрез owner_id. @Query сам по себе си не оправдава конкатенация.
3. **LIKE wildcard** % съвпада с поредица, _ с един символ; **exact match** използва =.
   - Search допуска wildcard семантика, но никога чужд owner; lookup намира точно summary, без шаблони.
4. **Identifier** е име на колона/поле, **allowlist** допуска само избрани identifiers.
   - sort=summary може да се съпостави с фиксирано поле. Bind параметър не замества SQL ORDER BY идентификатор.
5. **Validation** ограничава размер и допустими стойности, **NUL** е нулев символ, **SQL dialect** са особености на DB.
   - q над 100 символа или с NUL получава 400. Проверяваме и PostgreSQL; H2 не доказва всички особености на реалната база.



### 1.2. Проверки и доказателства

1. **Security regression test** е автоматизиран тест на правило за сигурност, който открива повторна поява на проблем. **Assertion** сравнява очаквано и получено; **negative test** проверява отказ, **positive test** — разрешена операция.
   - Пример: GET /tasks без удостоверяване → 401, със съществуваща сесия → 200. Тестът за отказ не заменя теста за нормална работа.
2. **JUnit** изпълнява тестовете; **MockMvc** подава HTTP заявки през Spring; **H2** е базата в памет за бързи проверки. **Integration test** проверява взаимодействието на компоненти; същите тестове се изпълняват и с PostgreSQL.
   - В TaskManagerBaselineTest полето mvc е MockMvc: `mvc.perform(get("/tasks")).andExpect(status().isUnauthorized());`. Статичните imports са в готовия клас.
3. **Fixture** са началните данни на теста; **test matrix** е списък от входове и очаквания; **edge case** е граничен случай. **Regression** означава връщане на вече отстранен проблем.
   - Пример: собствена задача, чужда задача и липсващо ID се проверяват отделно; след отказана промяна записът в DB остава същият.
4. **Root cause** е първопричината, **mitigation** — защитата, **evidence** — доказателството. **Code diff** показва промяната, **test report** — резултата. **Baseline** е началната версия за сравнение.
   - Запазете заявката, очакването и отчета. Провалена компилация не е доказателство, че тестът е открил нарушение. Не представяйте непроверена хипотеза като установен дефект.

`mvn test` изпълнява тестовете с H2 и записва target/surefire-reports. `mvn -Ppostgres-tests test` използва отделната PostgreSQL тестова база, стартирана по setup.md. **Maven profile** е именуван набор от настройки. Новите класове с тестове завършват на Test. За браузърно поведение се използва и реален браузър; MockMvc не изпълнява JavaScript.


## 2. Подготовка

### Предварителни знания

Java, Spring Boot, HTTP, JPA и Task Manager от lab11. Продължете собственото решение от упражнение 4; запазете тестовете и данните с определени собственици.

### Начален проект и надграждане

След упражнение 4. Добавя се търсене по summary; запазва се owner политиката от упражнение 3. Съществуващите JPA заявки не се заменят с конкатенация.

Използвайте [Task Manager](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/task-manager/README.md) и [подготовката](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/setup.md). Всички Maven/Compose команди се изпълняват от task-manager. Работните Java класове са в src/main/java/bg/tu_varna/sit/task_manager; тестовете — в съответния src/test/java package.

**Файлове за работа:** TaskRepository, TaskService/TaskServiceImp, TaskController; нови /tasks/search и /tasks/lookup. [Архитектурната карта](https://github.com/programmingfundamental/courses/blob/main/course-materials/applied-web-security/architecture/system-overview.md) показва кои маршрути съществуват в началото и кои се добавят последователно.

JDK 17+, Maven 3.9+ или Maven Wrapper, Docker Compose и браузър са достатъчни. Преди промяна изпълнете mvn test; след промяната повторете съответните тестове и PostgreSQL профила. Новите класове/маршрути, описани като надграждане, се реализират в това упражнение.


## 3. Примерен проблем

Добавете GET /tasks/search?q= с параметризирана заявка, която връща само разрешените задачи.

### Стъпки за решаване


1. Добавете search към TaskService и TaskController. q има defaultValue=""; дължина<=100 и без NUL. Новият literal маршрут /tasks/search се различава от /tasks/{id}.
2. В TaskRepository използвайте JPQL с :q, :username и server-side :admin. Политиката е ADMIN всички, USER само t.owner.username=username.
3. В service подайте principal и isAdmin от TaskPolicy, а не от query parameters. Mapping към TaskResponseDto остава в транзакция.
4. Създайте задачи със summary „Бележки O'Reilly“ за alice и bob. Търсенето като alice трябва да върне само нейния запис.
5. Добавете TaskSearchTest за нормален текст, апостроф, празно q, SQL-подобен текст, %, _, голям вход и NUL. Изпълнете H2 и PostgreSQL тестовете.

Не очаквайте съществуващата findById заявка да е SQL injection. Оценяваме конструкцията на новата заявка и сравняваме параметризирания вариант с конкатениран SQL фрагмент в анализа.
