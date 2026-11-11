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

## What each of the seven tests checks

Open the two classes under `src/test/java` in the archive. In `DateRangeValidatorTest` (tests 1–2), you call the validator directly and check its boolean result. In `TaskManagerBaselineTest` (tests 3–7), you follow requests through MockMvc: in addition to the HTTP status, you can check the response body, the session, or a record in H2.

### 1. Is a one-minute interval valid?

`acceptsEndAfterStart` passes a report filter (`FilterReportDto`) to the validator. It starts on **15 January 2026 at 09:00** and ends **at 09:01 on the same day**. The end must be strictly later than the start. One minute is enough, so `assertTrue` expects `isValid(...)` to return `true`.

### 2. Is a zero-length interval valid?

`rejectsEndEqualToStart` uses the same start but also sets the end to **15 January 2026 at 09:00**. The values are equal, so the end is not later than the start. That is why `assertFalse` expects `false`. A validator that allowed equality would pass the first test but fail this one.

### 3. Which role and password does registration store?

`registrationStoresUserWithHashedPassword` sends `POST /auth/register` with username `alice`, password `Alice-password-2026!`, and requested role `ADMIN`. After `200 OK`, the test looks up `alice` in the database. It expects `USER` because public registration creates an ordinary account regardless of the role in the request.

The test makes two password checks: the stored value differs from the submitted password, and `encoder.matches(...)` returns `true` for that password and the stored BCrypt hash. The first assertion alone would pass even for a random string; the second shows that the hash actually matches the submitted password.

### 4. Does the login session work on the next request?

`realLoginReturnsTokensAndSession` registers `alice`, then sends `POST /auth/login` with username `alice` and password `Alice-password-2026!`. The credentials match the registered account, so the test expects `200 OK`, username `alice`, and nonempty `accessToken` and `refreshToken` fields.

The test then takes the session created during login and uses it for `GET /tasks`, without sending a token. The expected `200 OK` shows that the application recognizes the signed-in user through the session.

### 5. How do two reports add up to 31 hours, and who can read them?

`taskCrudAndReportSummaryWork` uses two test users: `alice` with role `USER` and `admin` with role `ADMIN`. Follow the same task ID across the requests:

1. `alice` sends `POST /tasks` with summary `First task summary`, description `Description with enough characters`, and deadline `2099-12-31T12:00:00`. These are valid inputs: expect `201 Created` and the submitted summary. Next, `GET /tasks/{id}` must return `200 OK` and the ID from the first response, confirming that the test read back the task it just created.
2. `admin` adds two reports to that task, each with content `Report with enough characters` and `workTime` of `15:30:00`. Both requests must return `201 Created` and the submitted work time. Then `GET /reports/task/{id}/summary` must return `200 OK` and `totalWorkedTime` of `PT31H`: 15 h 30 min + 15 h 30 min = 31 hours. This is a duration, so it can exceed 24 hours.
3. Finally, `alice` requests the report list with `GET /reports/task/{id}`. Expect `403 Forbidden`: she can read the task, but reading its reports requires the `ADMIN` role.

### 6. What happens when an anonymous caller requests the task list?

`anonymousCannotReadTasks` sends `GET /tasks` without a user, session, or token. The task list requires a signed-in user, so expect `401 Unauthorized`. Compare this with the `403` in test 5: there the application knows who `alice` is, but her role does not grant access to reports.

### 7. What happens to the session and refresh token after logout?

`logoutRevokesPersistedRefreshTokens` registers `logoutuser` with password `Logout-password-2026!`, logs in with those credentials, and keeps the session and the returned `refreshToken`. It sends `POST /auth/logout` with that session and expects `200 OK`.

Logging out must end the session and prevent renewal through the issued token. The test therefore checks two effects: `session.isInvalid()` returns `true`, and a database lookup for that token with `revoked=false` finds no record. A successful HTTP response alone would prove neither.

## Add a test for an interval crossing midnight

A report starts at 23:59 and ends two minutes later at 00:01 the next day. This is a valid interval. Add the method to `src/test/java/bg/tu_varna/sit/task_manager/model/validator/DateRangeValidatorTest.java` to check that the validator compares the date as well as the time.

```java
@Test
void acceptsIntervalAcrossMidnight() {
    var startOfInterval = LocalDateTime.of(2030, 5, 10, 23, 59);
    var range = new FilterReportDto();
    range.setFrom(startOfInterval);
    range.setTo(startOfInterval.plusMinutes(2));

    assertTrue(validator.isValid(range, null));
}
```

Run the class with `.\mvnw.cmd -Dtest=DateRangeValidatorTest test` in PowerShell or `sh ./mvnw -Dtest=DateRangeValidatorTest test` in Bash. All three tests should pass. Put additional classes under `src/test/java` and give them names ending in `Test`. To run a test from your IDE, import `pom.xml` as a Maven project and run the method from the class.

If you have already extended Task Manager in a previous lab, keep your changes. Merge the missing test-scoped dependencies from `pom.xml` and the files you need from `src/test`. Update the integration test expectations if your API contract has changed.
