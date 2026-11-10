---
title: "Помощни материали"
sidebar:
  order: 0
---

[Изтеглете Task Manager с JUnit 5 (ZIP)](/courses/downloads/task-manager-starter.zip)

Архивът съдържа Maven проект с два самостоятелни теста на валидатор и пет интеграционни теста на REST API. Интеграционните тестове използват MockMvc и база H2 в паметта. За изпълнението им са достатъчни JDK 17 или 21 и достъп до Maven хранилището при първото стартиране.

## Стартирайте седемте теста

Разархивирайте ZIP файла в празна папка. Отворете терминал в подпапката `task-manager`, където се намират `pom.xml` и Maven Wrapper.

**Windows PowerShell**

```powershell
.\mvnw.cmd test
```

**Linux и macOS**

```bash
sh ./mvnw test
```

Потърсете `Tests run: 7, Failures: 0, Errors: 0, Skipped: 0` и `BUILD SUCCESS`. Подробните отчети са в `target/surefire-reports/`. Тестовият профил се включва автоматично. Docker, PostgreSQL и `.env` са нужни само ако решите да стартирате самото приложение по инструкциите в README.

## Добавете тест за обърнат интервал

В `src/test/java/bg/tu_varna/sit/task_manager/model/validator/DateRangeValidatorTest.java` добавете следния метод вътре в класа. Методът използва същите начална дата и валидатор като двата включени примера.

```java
@Test
void rejectsEndBeforeStart() {
    assertFalse(validator.isValid(rangeEndingAt(start.minusMinutes(1)), null));
}
```

Изпълнете само този клас с `.\mvnw.cmd -Dtest=DateRangeValidatorTest test` в PowerShell или `sh ./mvnw -Dtest=DateRangeValidatorTest test` в Bash. Очаквайте три успешни теста. При следващи примери създавайте класове с имена, завършващи на `Test`, под `src/test/java`. За изпълнение от IDE отворете `pom.xml` като Maven проект и стартирайте тестовия метод от класа.

Ако вече сте надградили Task Manager в предишно упражнение, запазете своя код. Добавете липсващите зависимости с обхват `test` от `pom.xml` и пренесете нужните файлове от `src/test`. Адаптирайте очакваните резултати на интеграционните тестове, ако сте променили договора на API.
