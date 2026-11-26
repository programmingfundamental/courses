# Контролно: unit тестове за Task Manager — вариант А

Използвайте предоставения обновен проект Task Manager с JDK 17 или 21. Създайте `TaskManagerVariantATest.java` в `src/test/java/bg/tu_varna/sit/task_manager/` с пакет `bg.tu_varna.sit.task_manager`. Напишете точно 10 метода с `@Test` — по един за всяко условие. Използвайте посочените имена на методите.

## Общи изисквания

- Извиквайте реалните методи директно. Тестовете трябва да работят без HTTP заявки, MockMvc, Spring контекст, `@SpringBootTest` и база данни.
- Създавайте всички входни и очаквани entity, request и response обекти чрез `builder()...build()`. Не използвайте reflection, `ReflectionTestUtils` или `setField`. Ролите са enum стойности; стандартните дати и колекции се създават с обичайните Java фабрични методи.
- Подайте Mockito заместители на хранилищата чрез конструкторите на реалните услуги. Използвайте реален `new CustomMapper()`. Не замествайте проверяваната услуга или mapper-а.
- При обновяване настройте `save(...)` да връща получения като аргумент обект. Проверявайте върнатите стойности и изключения; броят на извикванията към заместителите не е предмет на задачите.
- Подготвяйте нови данни за всеки тест. Тестовете трябва да могат да се изпълняват независимо и в произволен ред.
- Използвайте `LocalDateTime` за дати на задачи и отчети, `LocalTime` за време в отделен отчет, `Duration` за общото време и `Instant` за сроковете на токените.
- Builder-ите създават обекти; анотациите за Bean Validation не се изпълняват автоматично при `build()` или при директното извикване на тези услуги.

## Тест 1. Право за обикновен потребител

Име на тестовия метод: `a01_userRoleAuthority`.

**Подготовка и извикване:** Извикайте `Role.USER.asAuthority()`.

**Очакван резултат:** Очаквайте точно низа `ROLE_USER`.

## Тест 2. Данни на активен потребител

Име на тестовия метод: `a02_activeUserDetails`.

**Подготовка и извикване:** `UserRepository.findByUsername("alice")` връща `User` с `username = "alice"`, `password = "hash-a"`, `role = USER`, `enabled = true`. Извикайте `AppUserDetailsService.loadUserByUsername("alice")`.

**Очакван резултат:** Проверете името, записаната парола, `isEnabled() = true` и точно едно право: `ROLE_USER`.

## Тест 3. Липсващ потребител

Име на тестовия метод: `a03_missingUser`.

**Подготовка и извикване:** `UserRepository.findByUsername("missing-a")` връща `Optional.empty()`. Извикайте `loadUserByUsername("missing-a")`.

**Очакван резултат:** Очаквайте `UsernameNotFoundException`.

## Тест 4. Четене на задача

Име на тестовия метод: `a04_findTask`.

**Подготовка и извикване:** `TaskRepository.findById(41L)` връща задача с `id = 41`, `summary = "Prepare exam tasks"`, `description = "Initial description for variant A"`, `deadline = 2032-05-10T17:00:00`. Извикайте `TaskServiceImp.findById(41L)`.

**Очакван резултат:** Проверете и четирите полета на върнатия `TaskResponseDto`.

## Тест 5. Липсваща задача

Име на тестовия метод: `a05_missingTask`.

**Подготовка и извикване:** `TaskRepository.findById(941L)` връща `Optional.empty()`. Извикайте `TaskServiceImp.findById(941L)`.

**Очакван резултат:** Очаквайте `java.lang.module.ResolutionException`, използвано от тази услуга.

## Тест 6. Пълно обновяване на задача

Име на тестовия метод: `a06_updateTask`.

**Подготовка и извикване:** Подгответе отново задачата от тест 4. Подайте `TaskRequestDto` със `summary = "Review exam tasks"`, `description = "Updated description for variant A"` и `deadline = 2032-05-12T18:30:00` към `TaskServiceImp.update(41L, request)`.

**Очакван резултат:** Очаквайте трите нови стойности и запазен идентификатор `41`.

## Тест 7. Обновяване с липсващо описание

Име на тестовия метод: `a07_updateTaskWithNullDescription`.

**Подготовка и извикване:** Подгответе ново копие на първоначалната задача от тест 4. Извикайте `update(41L, request)` със същите нови заглавие и срок от тест 6, но с `description = null`.

**Очакван резултат:** Очаквайте новите заглавие и срок, ID `41` и старото описание `Initial description for variant A`. Проверявате директното извикване на услугата; DTO валидацията не се задейства автоматично.

## Тест 8. Сумиране на време над 24 часа

Име на тестовия метод: `a08_totalBeyondOneDay`.

**Подготовка и извикване:** `TaskRepository.findById(41L)` връща задачата от тест 4. `ReportRepository.findByTaskId(41L)` връща два `Report` обекта с `workedTime` съответно `12:45:40` и `13:30:35`. Извикайте `ReportServiceImp.getTaskWithTotalWorkedTime(41L)`.

**Очакван резултат:** Очаквайте `Duration` от точно 26 часа, 16 минути и 15 секунди. Проверете още, че вложената задача е с ID `41` и заглавие `Prepare exam tasks`.

## Тест 9. Валиден токен

Име на тестовия метод: `a09_validRefreshToken`.

**Подготовка и извикване:** `RefreshTokenRepository.findByTokenAndRevokedFalse("token-a")` връща токен с `token = "token-a"`, `username = "alice"`, `revoked = false` и `expiryDate = 2099-01-01T00:00:00Z`. Извикайте `RefreshTokenService.validateRefreshToken("token-a")`.

**Очакван резултат:** Очаквайте същия обект `RefreshToken`; проверете идентичността му.

## Тест 10. Изтекъл токен

Име на тестовия метод: `a10_expiredRefreshToken`.

**Подготовка и извикване:** Подгответе токен със същите име, потребител и статус като в тест 9, но със срок `2000-01-01T00:00:00Z`. Хранилището връща този токен. Извикайте `validateRefreshToken("token-a")`.

**Очакван резултат:** Очаквайте `RuntimeException` със съобщение `Refresh token е изтекъл`.

## Изпълнение и предаване

От папката с `pom.xml` изпълнете:

```powershell
.\mvnw.cmd -Dtest=TaskManagerVariantATest test
```

За Linux и macOS: `sh ./mvnw -Dtest=TaskManagerVariantATest test`.

Очакваното обобщение за вашия клас е `Tests run: 10, Failures: 0, Errors: 0, Skipped: 0` и `BUILD SUCCESS`. Оригиналните седем теста от архива се изпълняват отделно с `.\mvnw.cmd test`, преди да добавите своя клас.

Предайте Java файла с всички imports и помощни методи и обобщението от Maven. За всеки тест добавете кратък коментар какви входни данни подготвяте и какъв изход проверявате.

