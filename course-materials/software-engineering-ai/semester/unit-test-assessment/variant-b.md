# Контролно: unit тестове за Task Manager — вариант Б

Използвайте предоставения обновен проект Task Manager с JDK 17 или 21. Създайте `TaskManagerVariantBTest.java` в `src/test/java/bg/tu_varna/sit/task_manager/` с пакет `bg.tu_varna.sit.task_manager`. Напишете точно 10 метода с `@Test` — по един за всяко условие. Използвайте посочените имена на методите.

## Общи изисквания

- Извиквайте реалните методи директно. Тестовете трябва да работят без HTTP заявки, MockMvc, Spring контекст, `@SpringBootTest` и база данни.
- Създавайте всички входни и очаквани entity, request и response обекти чрез `builder()...build()`. Не използвайте reflection, `ReflectionTestUtils` или `setField`. Ролите са enum стойности; стандартните дати и колекции се създават с обичайните Java фабрични методи.
- Подайте Mockito заместители на хранилищата чрез конструкторите на реалните услуги. Използвайте реален `new CustomMapper()`. Не замествайте проверяваната услуга или mapper-а.
- При обновяване настройте `save(...)` да връща получения като аргумент обект. Проверявайте върнатите стойности и изключения; броят на извикванията към заместителите не е предмет на задачите.
- Подготвяйте нови данни за всеки тест. Тестовете трябва да могат да се изпълняват независимо и в произволен ред.
- Използвайте `LocalDateTime` за дати на задачи и отчети, `LocalTime` за време в отделен отчет, `Duration` за общото време и `Instant` за сроковете на токените.
- Builder-ите създават обекти; анотациите за Bean Validation не се изпълняват автоматично при `build()` или при директното извикване на тези услуги.

## Тест 1. Право за администратор

Име на тестовия метод: `b01_adminRoleAuthority`.

**Подготовка и извикване:** Извикайте `Role.ADMIN.asAuthority()`.

**Очакван резултат:** Очаквайте точно низа `ROLE_ADMIN`.

## Тест 2. Данни на деактивиран администратор

Име на тестовия метод: `b02_disabledAdminDetails`.

**Подготовка и извикване:** `UserRepository.findByUsername("bob")` връща `User` с `username = "bob"`, `password = "hash-b"`, `role = ADMIN`, `enabled = false`. Извикайте `AppUserDetailsService.loadUserByUsername("bob")`.

**Очакван резултат:** Проверете името, записаната парола, `isEnabled() = false` и точно едно право: `ROLE_ADMIN`.

## Тест 3. Липсващ потребител

Име на тестовия метод: `b03_missingUser`.

**Подготовка и извикване:** `UserRepository.findByUsername("missing-b")` връща `Optional.empty()`. Извикайте `loadUserByUsername("missing-b")`.

**Очакван резултат:** Очаквайте `UsernameNotFoundException`.

## Тест 4. Четене на отчет

Име на тестовия метод: `b04_findReport`.

**Подготовка и извикване:** Подгответе задача с `id = 52`, `summary = "Review project report"`, `description = "Task for variant B reports"`, `deadline = 2032-06-10T17:00:00`. Към нея подгответе `Report` с `id = 82`, `content = "Initial report for variant B"`, `workedTime = 01:20:30`, `dateCreated = 2032-06-01T09:00:00`, `dateUpdated = 2032-06-01T10:20:30`. `ReportRepository.findById(82L)` връща този отчет. Извикайте `ReportServiceImp.getById(82L)`.

**Очакван резултат:** Проверете всички полета на `ReportResponseDto`: ID `82`, съдържанието, `workTime = 01:20:30`, двете дати и `taskId = 52`. В entity полето е `workedTime`, а в DTO е `workTime`.

## Тест 5. Липсващ отчет

Име на тестовия метод: `b05_missingReport`.

**Подготовка и извикване:** `ReportRepository.findById(982L)` връща `Optional.empty()`. Извикайте `ReportServiceImp.getById(982L)`.

**Очакван резултат:** Очаквайте `ResourceNotFoundException`.

## Тест 6. Пълно обновяване на отчет

Име на тестовия метод: `b06_updateReport`.

**Подготовка и извикване:** Подгответе отново отчета от тест 4. Подайте `ReportRequestDto` с `content = "Updated report for variant B"` и `workTime = 02:10:15` към `ReportServiceImp.update(82L, request)`.

**Очакван резултат:** Очаквайте новите съдържание и време, ID на отчета `82` и ID на задачата `52`.

## Тест 7. Обновяване с липсващо съдържание

Име на тестовия метод: `b07_updateReportWithNullContent`.

**Подготовка и извикване:** Подгответе ново копие на първоначалния отчет от тест 4. Извикайте `update(82L, request)` с `content = null` и `workTime = 00:45:00`.

**Очакван резултат:** Очаквайте старото съдържание `Initial report for variant B`, новото време `00:45:00`, ID `82` и `taskId = 52`. Проверявате директното извикване на услугата; DTO валидацията не се задейства автоматично.

## Тест 8. Задача без отчети

Име на тестовия метод: `b08_taskWithoutReports`.

**Подготовка и извикване:** `TaskRepository.findById(52L)` връща задачата от тест 4. `ReportRepository.findByTaskId(52L)` връща празен списък. Извикайте `ReportServiceImp.getTaskWithTotalWorkedTime(52L)`.

**Очакван резултат:** Очаквайте `Duration.ZERO`. Проверете още, че вложената задача е с ID `52` и заглавие `Review project report`.

## Тест 9. Валиден токен

Име на тестовия метод: `b09_validRefreshToken`.

**Подготовка и извикване:** `RefreshTokenRepository.findByTokenAndRevokedFalse("token-b")` връща токен с `token = "token-b"`, `username = "bob"`, `revoked = false` и `expiryDate = 2099-01-01T00:00:00Z`. Извикайте `RefreshTokenService.validateRefreshToken("token-b")`.

**Очакван резултат:** Очаквайте същия обект `RefreshToken`; проверете идентичността му.

## Тест 10. Непознат или отнет токен

Име на тестовия метод: `b10_unknownRefreshToken`.

**Подготовка и извикване:** `RefreshTokenRepository.findByTokenAndRevokedFalse("missing-token-b")` връща `Optional.empty()`. Извикайте `RefreshTokenService.validateRefreshToken("missing-token-b")`.

**Очакван резултат:** Очаквайте `RuntimeException` със съобщение `Невалиден refresh token`.

## Изпълнение и предаване

От папката с `pom.xml` изпълнете:

```powershell
.\mvnw.cmd -Dtest=TaskManagerVariantBTest test
```

За Linux и macOS: `sh ./mvnw -Dtest=TaskManagerVariantBTest test`.

Очакваното обобщение за вашия клас е `Tests run: 10, Failures: 0, Errors: 0, Skipped: 0` и `BUILD SUCCESS`. Оригиналните седем теста от архива се изпълняват отделно с `.\mvnw.cmd test`, преди да добавите своя клас.

Предайте Java файла с всички imports и помощни методи и обобщението от Maven. За всеки тест добавете кратък коментар какви входни данни подготвяте и какъв изход проверявате.

