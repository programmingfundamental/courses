# Task Manager — стартов проект

Проектът предоставя Java/Spring API за задачи, отчети и потребители. Отворете папката с `pom.xml` като Maven проект с JDK 17 или 21.

## Първи JUnit тест

От папката `task-manager` изпълнете `.\mvnw.cmd test` в PowerShell или `sh ./mvnw test` в Bash. При първия старт Maven Wrapper изтегля Maven и зависимостите. Тестовете работят локално без Docker или файл `.env`.

Очаквайте 7 успешни теста. Двата теста в `DateRangeValidatorTest` проверяват допустим и граничен интервал. Петте в `TaskManagerBaselineTest` изпращат заявки през MockMvc и използват H2 в паметта с профил `test`. Отчетите са в `target/surefire-reports/`.

Създайте клас с име, завършващо на `Test`, в `src/test/java/bg/tu_varna/sit/task_manager/`. Използвайте `org.junit.jupiter.api.Test` за тестовите методи. За да изпълните само примерния валидатор, използвайте `.\mvnw.cmd -Dtest=DateRangeValidatorTest test` или `sh ./mvnw -Dtest=DateRangeValidatorTest test`.

## Тестови данни с Builder

Всички entity, request и response класове, както и `ErrorDetail`, предоставят Lombok `builder()`. Създавайте тестовите обекти чрез него, без `ReflectionTestUtils` или достъп до частни полета:

```java
var request = TaskRequestDto.builder()
        .summary("Example task title")
        .description("Example task description")
        .deadline(LocalDateTime.of(2035, 5, 10, 17, 0))
        .build();
System.out.println(request.getSummary()); // Example task title
```

Импортирайте `java.time.LocalDateTime` и `bg.tu_varna.sit.task_manager.model.dto.request.TaskRequestDto`. Същият синтаксис важи за `Task`, `Report`, `User`, `RefreshToken`, останалите request и response DTO и `ErrorDetail`. При `Report` задавайте `workedTime(...)`, а при `ReportRequestDto` и `ReportResponseDto` — `workTime(...)`. За `TaskReportResponse.totalWorkedTime(...)` използвайте `Duration`. `Role` е enum: използвайте `Role.USER` или `Role.ADMIN`.

Builder-ът не изпълнява Bean Validation автоматично. Непосочените референтни полета остават `null`, числовите примитиви — `0`, а булевите — `false`. Задавайте всички стойности, нужни за конкретния тест, включително `enabled(true)` за активен потребител. Конструкторите без аргументи остават достъпни за JPA, Jackson и ModelMapper.

За unit тестове създавайте услугите с `new`, подавайте Mockito заместители на хранилищата и реален `new CustomMapper()`. Стартирайте само своя клас с `.\mvnw.cmd -Dtest=ИмеНаКласаTest test`, без Spring контекст, HTTP заявки и база данни. Отворете проекта като Maven проект и включете annotation processing в IDE, ако тя не разпознава генерираните от Lombok методи.

## Стартиране

От тази директория: `./init-environment.ps1` (PowerShell) или `sh ./init-environment.sh` (Bash), после `docker compose up -d --build --wait`. Приложението слуша на http://localhost:9000; базата не публикува порт. Изчакването --wait удостоверява DB health; проверете допълнително GET /tasks → 401, за да установите, че Spring е готов.

Подробните JSON заявки и подготовката на потребители са в [setup.md](../setup.md). Не се изисква локален Maven за Docker build; за локална компилация са нужни JDK 17+ и Maven 3.9+ или включеният wrapper.

## Ръчни проверки

В упражнения 2–10 изпращайте API заявките с Postman и попълвайте протокол с очаквани и действителни статуси, отговори и състояние на данните. Подготовката на сесии, CSRF и Bearer tokens е в setup.md. За XSS и cookie поведение използвайте браузър.

В упражнение 11 по Уеб сигурност използвайте `task-manager-testing.zip` за отделната PostgreSQL тестова база и адаптирайте интеграционните тестове към своето решение.

## Какво се добавя в упражненията

Началната версия няма owner на Task, search/lookup, HTML изглед, CSRF endpoint, rate limiter, private note и отделен token-api. Те се добавят в описания ред в [архитектурната карта](../architecture/system-overview.md). Студентът продължава своето решение от предходното упражнение. Няма LAB_MODE и не се връща автоматично към първоначалното копие.

След добавянето на owner създавайте задачи от реални регистрирани потребители. След включване на CSRF вземайте token преди и след login. Запазвайте Postman заявките и резултатите за упражнение 11.
