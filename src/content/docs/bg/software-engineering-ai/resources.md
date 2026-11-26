---
title: "Помощни материали"
sidebar:
  order: 0
---

[Изтеглете Task Manager с JUnit 5 (ZIP)](/courses/downloads/task-manager-starter.zip)

Архивът съдържа Maven проект с два самостоятелни теста на валидатор и пет интеграционни теста на REST API. Интеграционните тестове използват MockMvc и база H2 в паметта. За изпълнението им са достатъчни JDK 17 или 21 и достъп до Maven хранилището при първото стартиране.

[Преминете към петте самостоятелни задачи за unit тестове](#самостоятелни-задачи-за-unit-тестове).

## Създаване на тестови данни с Builder

В обновения ZIP всички конкретни entity, request и response модели имат Lombok `builder()`. Използвайте го в тестовете за задаване на частните полета:

```java
var request = TaskRequestDto.builder()
        .summary("Example task title")
        .description("Example task description")
        .deadline(LocalDateTime.of(2035, 5, 10, 17, 0))
        .build();
var task = Task.builder()
        .id(7L)
        .summary(request.getSummary())
        .description(request.getDescription())
        .deadline(request.getDeadline())
        .build();
var response = TaskResponseDto.builder()
        .id(task.getId())
        .summary(task.getSummary())
        .description(task.getDescription())
        .deadline(task.getDeadline())
        .build();
System.out.println(response.getSummary()); // Example task title
```

Поставете кода в тестов метод и импортирайте `LocalDateTime` от `java.time`, `Task` от `model.entity`, `TaskRequestDto` от `model.dto.request` и `TaskResponseDto` от `model.dto.response` на пакета `bg.tu_varna.sit.task_manager`. Проверете, че се извежда `Example task title`.

| Група | Класове с `builder()` |
| --- | --- |
| Entity | `Task`, `Report`, `User`, `RefreshToken` |
| Request | `TaskRequestDto`, `ReportRequestDto`, `FilterReportDto`, `LoginRequest`, `RegisterRequest`, `RefreshRequest` |
| Response | `TaskResponseDto`, `ReportResponseDto`, `TaskReportResponse`, `AuthResponse` |
| Модел за грешка | `ErrorDetail` |

За всеки тест създавайте нови обекти. Builder-ът не изпълнява Bean Validation: непосочените референтни полета остават `null`, числовите примитиви — `0`, а булевите — `false`. При активен потребител задайте `enabled(true)`. `Role` е enum и се използва като `Role.USER` или `Role.ADMIN`. В `Report` полето е `workedTime`, а в request и response DTO за отчет — `workTime`; общото време в `TaskReportResponse` е `Duration`.

Импортирайте проекта като Maven проект. Ако IDE не разпознава `builder()`, включете annotation processing и презаредете Maven зависимостите.

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

## Какво проверява всеки от седемте теста

Отворете двата класа под `src/test/java` в архива. В `DateRangeValidatorTest` (тестове 1–2) извиквате валидатора директно и проверявате булевия му резултат. В `TaskManagerBaselineTest` (тестове 3–7) проследявате заявки през MockMvc: освен HTTP статуса можете да проверите тялото на отговора, сесията или записа в H2.

### 1. Валиден ли е интервал от една минута?

`acceptsEndAfterStart` подава на валидатора филтър за отчети (`FilterReportDto`) с начало **15.01.2026 в 09:00** и край **същия ден в 09:01**. Правилото е краят да бъде строго след началото. Една минута разлика изпълнява условието, затова `assertTrue` очаква `isValid(...)` да върне `true`.

### 2. Допуска ли се интервал с нулева продължителност?

`rejectsEndEqualToStart` използва същото начало, но задава и края на **15.01.2026 в 09:00**. Двете стойности са равни, така че краят не е по-късен. Затова `assertFalse` очаква `false`. Ако валидаторът допускаше равенство, първият тест пак щеше да мине, но този щеше да се провали.

### 3. Какви роля и парола се записват при регистрация?

`registrationStoresUserWithHashedPassword` изпраща `POST /auth/register` с име `alice`, парола `Alice-password-2026!` и поискана роля `ADMIN`. След `200 OK` тестът намира `alice` в базата. Очаква роля `USER`, защото публичната регистрация дава обикновен акаунт независимо от ролята в заявката.

За паролата има две проверки: записаната стойност се различава от подадената парола, а `encoder.matches(...)` връща `true` за тази парола и записания BCrypt хеш. Първата проверка сама по себе си би минала дори при запис на произволен текст; втората показва, че хешът съответства точно на въведената парола.

### 4. Може ли сесията от входа да се използва за следваща заявка?

`realLoginReturnsTokensAndSession` регистрира `alice` и изпраща `POST /auth/login` с име `alice` и парола `Alice-password-2026!`. Данните съвпадат с регистрирания акаунт, затова тестът очаква `200 OK`, име `alice` и непразни полета `accessToken` и `refreshToken`.

След това тестът взема създадената при входа сесия и изпраща с нея `GET /tasks`, без да подава токен. Очакваното `200 OK` показва, че приложението разпознава вече влезлия потребител по сесията.

### 5. Как два отчета дават общо 31 часа и кой може да ги прочете?

`taskCrudAndReportSummaryWork` използва два тестови потребителя: `alice` с роля `USER` и `admin` с роля `ADMIN`. Проследете един и същ идентификатор на задача през заявките:

1. `alice` изпраща `POST /tasks` със заглавие `First task summary`, описание `Description with enough characters` и срок `2099-12-31T12:00:00`. Данните са валидни: очакват се `201 Created` и същото заглавие. След това `GET /tasks/{id}` трябва да върне `200 OK` и идентификатора от първия отговор — прочетена е точно създадената задача.
2. `admin` добавя към задачата два отчета с текст `Report with enough characters` и `workTime` = `15:30:00` всеки. При всяко добавяне очакваме `201 Created` и същата стойност за `workTime`. После `GET /reports/task/{id}/summary` трябва да върне `200 OK` и `totalWorkedTime` = `PT31H`: 15 ч. 30 мин. + 15 ч. 30 мин. = 31 часа. Това е продължителност и може да надхвърли 24 часа.
3. Накрая `alice` иска списъка с отчети чрез `GET /reports/task/{id}`. Очакваме `403 Forbidden`: тя може да чете задачата, но за отчетите се изисква роля `ADMIN`.

### 6. Какво става, ако заявката за задачи е анонимна?

`anonymousCannotReadTasks` изпраща `GET /tasks` без потребител, сесия или токен. Списъкът със задачи изисква вход, затова очакваме `401 Unauthorized`. Сравнете с `403` от тест 5: там приложението знае коя е `alice`, но ролята ѝ не позволява да чете отчети.

### 7. Какво става със сесията и токена за обновяване след изход?

`logoutRevokesPersistedRefreshTokens` регистрира `logoutuser` с парола `Logout-password-2026!`, влиза с тези данни и запазва сесията и получения `refreshToken`. Със същата сесия изпраща `POST /auth/logout` и очаква `200 OK`.

Изходът трябва да прекрати сесията и да отнеме възможността за обновяване чрез издадения токен. Затова тестът проверява две последици: `session.isInvalid()` връща `true`, а търсенето в базата по стойността на токена и `revoked=false` не намира запис. Само успешният HTTP статус не би доказал нито една от тях.

## Добавете тест за интервал през полунощ

Отчетът започва в 23:59 и завършва две минути по-късно, в 00:01 на следващия ден. Интервалът е валиден. Добавете метода в `src/test/java/bg/tu_varna/sit/task_manager/model/validator/DateRangeValidatorTest.java`, за да проверите, че валидаторът сравнява и датата, и часа.

```java
@Test
void acceptsIntervalAcrossMidnight() {
    var startOfInterval = LocalDateTime.of(2030, 5, 10, 23, 59);
    var range = FilterReportDto.builder()
            .from(startOfInterval)
            .to(startOfInterval.plusMinutes(2))
            .build();

    assertTrue(validator.isValid(range, null));
}
```

Изпълнете само този клас с `.\mvnw.cmd -Dtest=DateRangeValidatorTest test` в PowerShell или `sh ./mvnw -Dtest=DateRangeValidatorTest test` в Bash. Очаквайте три успешни теста. При следващи примери създавайте класове с имена, завършващи на `Test`, под `src/test/java`. За изпълнение от IDE отворете `pom.xml` като Maven проект и стартирайте тестовия метод от класа.

Ако вече сте надградили Task Manager в предишно упражнение, запазете своя код. Добавете липсващите зависимости с обхват `test` от `pom.xml` и пренесете нужните файлове от `src/test`. Адаптирайте очакваните резултати на интеграционните тестове, ако сте променили договора на API.

## Самостоятелни задачи за unit тестове

Напишете тестове за следните пет метода от стартовия проект Task Manager. Създайте класа `TaskManagerFiveUnitTest` в `src/test/java/bg/tu_varna/sit/task_manager/` с пакет `bg.tu_varna.sit.task_manager`. Проверявайте подадените данни спрямо върнатия резултат или хвърленото изключение.

Използвайте JUnit 5 и извиквайте реалните методи директно. Тестовете трябва да работят без HTTP комуникация, `MockMvc`, Spring контекст, `@SpringBootTest` и база данни. Подайте чрез конструкторите Mockito заместители само на необходимите хранилища. За преобразуването на задачи използвайте реален `new CustomMapper()`. Не замествайте услугата или метода, който проверявате.

Всеки от описаните тестови случаи трябва да се изпълнява самостоятелно. Използвайте фиксираните данни от условията. Създавайте entity, request и response обектите чрез `builder()...build()`, както в примера по-горе. Не използвайте reflection или `ReflectionTestUtils.setField(...)`.

### Задача 1. Преобразуване на роля в право за достъп

Напишете параметризиран тест за `Role.asAuthority()` с два случая:

| Роля | Очакван резултат |
| --- | --- |
| `Role.USER` | `"ROLE_USER"` |
| `Role.ADMIN` | `"ROLE_ADMIN"` |

Проверете точното съдържание на върнатия низ, включително префикса и главните букви. За тази задача не са нужни заместители.

### Задача 2. Зареждане на данните на потребител

Проверете `AppUserDetailsService.loadUserByUsername("alice")`. Подгответе `UserRepository.findByUsername("alice")` да връща потребител с име `alice`, записана парола `stored-password-hash`, роля `USER` и `enabled = true`.

Напишете три теста:

| Данни от хранилището | Очакван резултат |
| --- | --- |
| Описаният активен потребител | `UserDetails` с име `alice`, парола `stored-password-hash`, `isEnabled() = true` и точно едно право: `ROLE_USER` |
| Същите данни, но `enabled = false` | Върнатият `UserDetails` има `isEnabled() = false` |
| `Optional.empty()` | `UsernameNotFoundException` |

Изследвайте преобразуването на данните към `UserDetails` чрез директното извикване на метода. Не изпълнявайте вход в приложението.

### Задача 3. Намиране на задача по идентификатор

Проверете `TaskServiceImp.findById(42L)`. За успешния случай задайте `TaskRepository.findById(42L)` да връща `Optional`, съдържащ задача със следните стойности:

| Поле | Стойност |
| --- | --- |
| `id` | `42` |
| `summary` | `Prepare unit tests` |
| `description` | `Write tests for task lookup` |
| `deadline` | `2030-05-10T17:00:00` |

Проверете, че върнатият `TaskResponseDto` съдържа същите четири стойности. За срока използвайте `LocalDateTime`.

В отделен тест задайте хранилището да връща `Optional.empty()`. Очаквайте `java.lang.module.ResolutionException`, каквото използва текущата реализация на метода.

### Задача 4. Обновяване на задача със запазване на идентификатора

Проверете `TaskServiceImp.update(42L, request)`. Подгответе съществуваща задача с ID `42`, заглавие `Original task title`, описание `Original task description` и срок `2030-05-10T17:00:00`. Хранилището трябва да връща тази задача при търсене по ID, а заместителят на `save(...)` — обекта, получен като аргумент.

За първия тест подайте `TaskRequestDto` със следните стойности:

| Поле | Нова стойност |
| --- | --- |
| `summary` | `Updated task title` |
| `description` | `Updated task description` |
| `deadline` | `2030-05-12T18:30:00` |

Проверете, че върнатият `TaskResponseDto` съдържа новите стойности, а ID остава `42`.

За втория тест подгответе отново първоначалната задача. Подайте същите нови заглавие и срок, но `description = null`. Очаквайте новите заглавие и срок, старото описание `Original task description` и непроменен ID `42`. `CustomMapper` пропуска `null` стойностите. Тук проверявате директно услугата; анотацията `@NotNull` върху DTO не се изпълнява автоматично при това извикване.

### Задача 5. Проверка на токен за обновяване

Проверете `RefreshTokenService.validateRefreshToken("test-token")`. Подгответе заместител на `RefreshTokenRepository.findByTokenAndRevokedFalse("test-token")` за три случая:

| Данни от хранилището | Очакван резултат |
| --- | --- |
| `Optional` с токен, чийто `expiryDate` е `2099-01-01T00:00:00Z` | Връща същия обект `RefreshToken` |
| `Optional` с токен, чийто `expiryDate` е `2000-01-01T00:00:00Z` | `RuntimeException` със съобщение `Refresh token е изтекъл` |
| `Optional.empty()` | `RuntimeException` със съобщение `Невалиден refresh token` |

За двата обекта задайте `token = "test-token"`, `username = "alice"` и `revoked = false`. Използвайте `Instant` за сроковете. При успешния случай проверете идентичността на обекта, а при неуспешните — типа и съобщението на изключението. `test-token` е обикновен тестов низ; не е нужно да генерирате JWT.

### Изпълнение и предаване

От папката с `pom.xml` стартирайте само написания от вас клас:

**Windows PowerShell**

```powershell
.\mvnw.cmd -Dtest=TaskManagerFiveUnitTest test
```

**Linux и macOS**

```bash
sh ./mvnw -Dtest=TaskManagerFiveUnitTest test
```

Очаквайте общо 12 изпълнения: 2 за задача 1, 3 за задача 2, 2 за задача 3, 2 за задача 4 и 3 за задача 5. Обобщението за този клас трябва да бъде `Tests run: 12, Failures: 0, Errors: 0, Skipped: 0` и `BUILD SUCCESS`.

Предайте `TaskManagerFiveUnitTest.java` с всички нужни imports и помощни методи, обобщението от Maven и кратко обяснение за всяка задача: какви данни подготвяте и какъв резултат проверявате.
