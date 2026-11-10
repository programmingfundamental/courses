---
title: "Study resources"
sidebar:
  order: 0
---

[Download Task Manager with JUnit 5 (ZIP)](/courses/downloads/task-manager-starter.zip)

The Maven project includes two unit tests for a date-range validator and five REST API integration tests using MockMvc and an in-memory H2 database. Use JDK 17 or 21. The first run needs access to the Maven repository to download dependencies.

## Run the seven tests

Extract the ZIP into an empty directory. Open a terminal in the `task-manager` folder containing `pom.xml` and the Maven Wrapper.

**Windows PowerShell**

```powershell
.\mvnw.cmd test
```

**Linux and macOS**

```bash
sh ./mvnw test
```

Look for `Tests run: 7, Failures: 0, Errors: 0, Skipped: 0` and `BUILD SUCCESS`. Detailed reports are in `target/surefire-reports/`. The test profile starts automatically. Docker, PostgreSQL and a local `.env` file are only needed to run the application as described in the README.

## Add a test for a reversed interval

Add this method inside the class in `src/test/java/bg/tu_varna/sit/task_manager/model/validator/DateRangeValidatorTest.java`. It uses the same start time and validator as the two existing tests.

```java
@Test
void rejectsEndBeforeStart() {
    assertFalse(validator.isValid(rangeEndingAt(start.minusMinutes(1)), null));
}
```

Run the class with `.\mvnw.cmd -Dtest=DateRangeValidatorTest test` in PowerShell or `sh ./mvnw -Dtest=DateRangeValidatorTest test` in Bash. All three tests should pass. Put additional classes under `src/test/java` and give them names ending in `Test`. To run a test from your IDE, import `pom.xml` as a Maven project and run the method from the class.

If you have already extended Task Manager in a previous lab, keep your changes. Merge the missing test-scoped dependencies from `pom.xml` and the files you need from `src/test`. Update the integration test expectations if your API contract has changed.
