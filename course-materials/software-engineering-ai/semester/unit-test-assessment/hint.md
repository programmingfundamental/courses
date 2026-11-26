# Контролно по unit тестване — преподавателски материали

Студентските задания са в [вариант А](variant-a.md) и [вариант Б](variant-b.md). Всеки вариант изисква точно 10 независими метода с `@Test`. Файловете са за предоставяне на съответната група при контролното; не са включени в публичния сайт или студентския ZIP.

## Подготовка и оценяване

Използвайте актуалния `public/downloads/task-manager-starter.zip`. Всички 15 конкретни модела предоставят Lombok builder и конструктори без аргументи. Entity класовете остават съвместими с JPA; request/response моделите — с Jackson и ModelMapper. Enum `Role` се използва директно.

За студентските данни изисквайте builders, без reflection. Услугите и `CustomMapper` са реални; само хранилищата са Mockito заместители. Вариант А упражнява промени в задача и ненулева сума над 24 часа; вариант Б — промени в отчет и празна сума. Двете задания имат еднаква структура: роля, потребител, отказ за липсващ потребител, четене, отказ за липсващ ресурс, пълно обновяване, частично обновяване, обобщение, валиден токен, невалиден токен.

Примерна рубрика: по 2 точки на тест, общо 20. За 1 точка — независима правилна подготовка с builders и реален проверяван метод; за 1 точка — всички изискани проверки на резултата/изключението. Само зелено изпълнение не е достатъчно: заместване на проверявания метод или връщане на предварително изчислен резултат от mock не доказва поведението му.

Проверката на изключение при липсващ отчет изисква коригиран `ResourceNotFoundException`: старият формат `%i` хвърляше грешка при форматиране. Поправен е и размененият ред на аргументите в `RelatedEntityException`. Договорът на `TaskServiceImp.findById` остава `ResolutionException`.

## Решение — вариант А

1: точният префикс на правото; 2: всички потребителски полета и флагът enabled; 3: липсващ потребител; 4: пълно преобразуване на задача; 5: съществуващият договор за изключение; 6: обновени стойности и запазен ID; 7: пропускане на null; 8: пренос на секунди и запазване на часове над 24; 9: идентичност на токена; 10: отказ за изтекъл токен.

```java
package bg.tu_varna.sit.task_manager;

import bg.tu_varna.sit.task_manager.component.CustomMapper;
import bg.tu_varna.sit.task_manager.model.dto.request.*;
import bg.tu_varna.sit.task_manager.model.dto.response.*;
import bg.tu_varna.sit.task_manager.model.entity.*;
import bg.tu_varna.sit.task_manager.repository.*;
import bg.tu_varna.sit.task_manager.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskManagerVariantATest {
    private final UserRepository users = mock(UserRepository.class);
    private final TaskRepository tasks = mock(TaskRepository.class);
    private final ReportRepository reports = mock(ReportRepository.class);
    private final RefreshTokenRepository tokens = mock(RefreshTokenRepository.class);
    private final CustomMapper mapper = new CustomMapper();
    private final AppUserDetailsService userService = new AppUserDetailsService(users);
    private final TaskServiceImp taskService = new TaskServiceImp(tasks, mapper);
    private final ReportServiceImp reportService = new ReportServiceImp(reports, tasks, mapper);
    private final RefreshTokenService tokenService = new RefreshTokenService(tokens);

    @Test
    void a01_userRoleAuthority() {
        assertEquals("ROLE_USER", Role.USER.asAuthority());
    }

    @Test
    void a02_activeUserDetails() {
        var user = User.builder().username("alice").password("hash-a")
                .role(Role.USER).enabled(true).build();
        when(users.findByUsername("alice")).thenReturn(Optional.of(user));

        var result = userService.loadUserByUsername("alice");

        assertAll(
                () -> assertEquals("alice", result.getUsername()),
                () -> assertEquals("hash-a", result.getPassword()),
                () -> assertTrue(result.isEnabled()),
                () -> assertEquals(List.of("ROLE_USER"), result.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority).toList()));
    }

    @Test
    void a03_missingUser() {
        when(users.findByUsername("missing-a")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("missing-a"));
    }

    @Test
    void a04_findTask() {
        when(tasks.findById(41L)).thenReturn(Optional.of(originalTask()));

        var result = taskService.findById(41L);

        assertTask(result, "Prepare exam tasks", "Initial description for variant A",
                "2032-05-10T17:00:00");
    }

    @Test
    void a05_missingTask() {
        when(tasks.findById(941L)).thenReturn(Optional.empty());
        assertThrows(java.lang.module.ResolutionException.class,
                () -> taskService.findById(941L));
    }

    @Test
    void a06_updateTask() {
        when(tasks.findById(41L)).thenReturn(Optional.of(originalTask()));
        when(tasks.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));
        var request = TaskRequestDto.builder().summary("Review exam tasks")
                .description("Updated description for variant A")
                .deadline(LocalDateTime.parse("2032-05-12T18:30:00")).build();

        var result = taskService.update(41L, request);

        assertTask(result, "Review exam tasks", "Updated description for variant A",
                "2032-05-12T18:30:00");
    }

    @Test
    void a07_updateTaskWithNullDescription() {
        when(tasks.findById(41L)).thenReturn(Optional.of(originalTask()));
        when(tasks.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));
        var request = TaskRequestDto.builder().summary("Review exam tasks")
                .description(null)
                .deadline(LocalDateTime.parse("2032-05-12T18:30:00")).build();

        var result = taskService.update(41L, request);

        assertTask(result, "Review exam tasks", "Initial description for variant A",
                "2032-05-12T18:30:00");
    }

    @Test
    void a08_totalBeyondOneDay() throws Exception {
        var task = originalTask();
        when(tasks.findById(41L)).thenReturn(Optional.of(task));
        when(reports.findByTaskId(41L)).thenReturn(List.of(
                Report.builder().task(task).workedTime(LocalTime.of(12, 45, 40)).build(),
                Report.builder().task(task).workedTime(LocalTime.of(13, 30, 35)).build()));

        var result = reportService.getTaskWithTotalWorkedTime(41L);

        assertAll(
                () -> assertEquals(Duration.ofHours(26).plusMinutes(16).plusSeconds(15),
                        result.getTotalWorkedTime()),
                () -> assertEquals(41L, result.getTask().getId()),
                () -> assertEquals("Prepare exam tasks", result.getTask().getSummary()));
    }

    @Test
    void a09_validRefreshToken() {
        var token = token("2099-01-01T00:00:00Z");
        when(tokens.findByTokenAndRevokedFalse("token-a")).thenReturn(Optional.of(token));
        assertSame(token, tokenService.validateRefreshToken("token-a"));
    }

    @Test
    void a10_expiredRefreshToken() {
        when(tokens.findByTokenAndRevokedFalse("token-a"))
                .thenReturn(Optional.of(token("2000-01-01T00:00:00Z")));

        var error = assertThrows(RuntimeException.class,
                () -> tokenService.validateRefreshToken("token-a"));

        assertEquals("Refresh token е изтекъл", error.getMessage());
    }

    private Task originalTask() {
        return Task.builder().id(41L).summary("Prepare exam tasks")
                .description("Initial description for variant A")
                .deadline(LocalDateTime.parse("2032-05-10T17:00:00")).build();
    }

    private RefreshToken token(String expiry) {
        return RefreshToken.builder().token("token-a").username("alice").revoked(false)
                .expiryDate(Instant.parse(expiry)).build();
    }

    private void assertTask(TaskResponseDto result, String summary,
                            String description, String deadline) {
        assertAll(
                () -> assertEquals(41L, result.getId()),
                () -> assertEquals(summary, result.getSummary()),
                () -> assertEquals(description, result.getDescription()),
                () -> assertEquals(LocalDateTime.parse(deadline), result.getDeadline()));
    }
}
```

## Решение — вариант Б

1: администраторско право; 2: деактивираният потребител остава деактивиран; 3: липсващ потребител; 4: преобразуване на всички полета на отчет, включително workedTime → workTime и task.id → taskId; 5: домейн изключение; 6: обновени полета и запазени идентификатори; 7: пропускане на null съдържание; 8: нулева продължителност за съществуваща задача; 9: идентичност на токена; 10: отказ за непознат или отнет токен.

```java
package bg.tu_varna.sit.task_manager;

import bg.tu_varna.sit.task_manager.component.CustomMapper;
import bg.tu_varna.sit.task_manager.model.dto.request.*;
import bg.tu_varna.sit.task_manager.model.dto.response.*;
import bg.tu_varna.sit.task_manager.model.entity.*;
import bg.tu_varna.sit.task_manager.repository.*;
import bg.tu_varna.sit.task_manager.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import bg.tu_varna.sit.task_manager.exception.ResourceNotFoundException;

class TaskManagerVariantBTest {
    private final UserRepository users = mock(UserRepository.class);
    private final TaskRepository tasks = mock(TaskRepository.class);
    private final ReportRepository reports = mock(ReportRepository.class);
    private final RefreshTokenRepository tokens = mock(RefreshTokenRepository.class);
    private final CustomMapper mapper = new CustomMapper();
    private final AppUserDetailsService userService = new AppUserDetailsService(users);
    private final TaskServiceImp taskService = new TaskServiceImp(tasks, mapper);
    private final ReportServiceImp reportService = new ReportServiceImp(reports, tasks, mapper);
    private final RefreshTokenService tokenService = new RefreshTokenService(tokens);

    @Test
    void b01_adminRoleAuthority() {
        assertEquals("ROLE_ADMIN", Role.ADMIN.asAuthority());
    }

    @Test
    void b02_disabledAdminDetails() {
        var user = User.builder().username("bob").password("hash-b")
                .role(Role.ADMIN).enabled(false).build();
        when(users.findByUsername("bob")).thenReturn(Optional.of(user));

        var result = userService.loadUserByUsername("bob");

        assertAll(
                () -> assertEquals("bob", result.getUsername()),
                () -> assertEquals("hash-b", result.getPassword()),
                () -> assertFalse(result.isEnabled()),
                () -> assertEquals(List.of("ROLE_ADMIN"), result.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority).toList()));
    }

    @Test
    void b03_missingUser() {
        when(users.findByUsername("missing-b")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("missing-b"));
    }

    @Test
    void b04_findReport() throws Exception {
        when(reports.findById(82L)).thenReturn(Optional.of(originalReport()));

        var result = reportService.getById(82L);

        assertReport(result, "Initial report for variant B", "01:20:30");
        assertAll(
                () -> assertEquals(LocalDateTime.parse("2032-06-01T09:00:00"),
                        result.getDateCreated()),
                () -> assertEquals(LocalDateTime.parse("2032-06-01T10:20:30"),
                        result.getDateUpdated()));
    }

    @Test
    void b05_missingReport() {
        when(reports.findById(982L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reportService.getById(982L));
    }

    @Test
    void b06_updateReport() {
        when(reports.findById(82L)).thenReturn(Optional.of(originalReport()));
        when(reports.save(any(Report.class))).thenAnswer(call -> call.getArgument(0));
        var request = ReportRequestDto.builder().content("Updated report for variant B")
                .workTime(LocalTime.of(2, 10, 15)).build();

        var result = reportService.update(82L, request);

        assertReport(result, "Updated report for variant B", "02:10:15");
    }

    @Test
    void b07_updateReportWithNullContent() {
        when(reports.findById(82L)).thenReturn(Optional.of(originalReport()));
        when(reports.save(any(Report.class))).thenAnswer(call -> call.getArgument(0));
        var request = ReportRequestDto.builder().content(null)
                .workTime(LocalTime.of(0, 45)).build();

        var result = reportService.update(82L, request);

        assertReport(result, "Initial report for variant B", "00:45:00");
    }

    @Test
    void b08_taskWithoutReports() throws Exception {
        when(tasks.findById(52L)).thenReturn(Optional.of(originalTask()));
        when(reports.findByTaskId(52L)).thenReturn(List.of());

        var result = reportService.getTaskWithTotalWorkedTime(52L);

        assertAll(
                () -> assertEquals(Duration.ZERO, result.getTotalWorkedTime()),
                () -> assertEquals(52L, result.getTask().getId()),
                () -> assertEquals("Review project report", result.getTask().getSummary()));
    }

    @Test
    void b09_validRefreshToken() {
        var token = RefreshToken.builder().token("token-b").username("bob").revoked(false)
                .expiryDate(Instant.parse("2099-01-01T00:00:00Z")).build();
        when(tokens.findByTokenAndRevokedFalse("token-b")).thenReturn(Optional.of(token));
        assertSame(token, tokenService.validateRefreshToken("token-b"));
    }

    @Test
    void b10_unknownRefreshToken() {
        when(tokens.findByTokenAndRevokedFalse("missing-token-b"))
                .thenReturn(Optional.empty());

        var error = assertThrows(RuntimeException.class,
                () -> tokenService.validateRefreshToken("missing-token-b"));

        assertEquals("Невалиден refresh token", error.getMessage());
    }

    private Task originalTask() {
        return Task.builder().id(52L).summary("Review project report")
                .description("Task for variant B reports")
                .deadline(LocalDateTime.parse("2032-06-10T17:00:00")).build();
    }

    private Report originalReport() {
        return Report.builder().id(82L).task(originalTask())
                .content("Initial report for variant B")
                .workedTime(LocalTime.of(1, 20, 30))
                .dateCreated(LocalDateTime.parse("2032-06-01T09:00:00"))
                .dateUpdated(LocalDateTime.parse("2032-06-01T10:20:30")).build();
    }

    private void assertReport(ReportResponseDto result, String content, String time) {
        assertAll(
                () -> assertEquals(82L, result.getId()),
                () -> assertEquals(52L, result.getTaskId()),
                () -> assertEquals(content, result.getContent()),
                () -> assertEquals(LocalTime.parse(time), result.getWorkTime()));
    }
}
```

## Допълнителни проверки за съвместимост на проекта

Този клас не е студентско решение. Той проверява, че request и response обектите, създадени с builders, запазват данните си при JSON сериализация и десериализация, както и че домейн изключенията се конструират правилно. Четирите entity builders се използват от двата варианта; базовите интеграционни тестове проверяват JPA и реалните API потоци.

```java
package bg.tu_varna.sit.task_manager;

import bg.tu_varna.sit.task_manager.model.ErrorDetail;
import bg.tu_varna.sit.task_manager.model.dto.request.*;
import bg.tu_varna.sit.task_manager.model.dto.response.*;
import bg.tu_varna.sit.task_manager.model.entity.*;
import bg.tu_varna.sit.task_manager.exception.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerModelCompatibilityTest {
    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    @Test
    void builderRequestsStillSupportJacksonRoundTrip() throws Exception {
        var date = LocalDateTime.of(2035, 5, 10, 9, 0);
        for (var value : List.of(
                TaskRequestDto.builder().summary("Example summary")
                        .description("Example description").deadline(date).build(),
                ReportRequestDto.builder().content("Example report")
                        .workTime(LocalTime.of(1, 2, 3)).build(),
                FilterReportDto.builder().time(LocalTime.of(1, 2, 3))
                        .from(date).to(date.plusDays(1)).build(),
                LoginRequest.builder().username("sample").password("example").build(),
                RegisterRequest.builder().username("sample").password("example").build(),
                RefreshRequest.builder().refreshToken("example-token").build())) {
            assertRoundTrip(value);
        }
    }

    @Test
    void builderResponsesStillSupportJacksonRoundTrip() throws Exception {
        var date = LocalDateTime.of(2035, 5, 10, 9, 0);
        var task = TaskResponseDto.builder().id(7L).summary("Example summary")
                .description("Example description").deadline(date).build();
        for (var value : List.of(
                task,
                ReportResponseDto.builder().id(8L).taskId(7L).content("Example report")
                        .workTime(LocalTime.of(1, 2, 3)).dateCreated(date)
                        .dateUpdated(date.plusHours(1)).build(),
                TaskReportResponse.builder().task(task)
                        .totalWorkedTime(Duration.ofHours(27).plusSeconds(15)).build(),
                AuthResponse.builder().username("sample").accessToken("access")
                        .refreshToken("refresh").expiresAt(123456L).message("example").build(),
                ErrorDetail.builder().time(date).message("Example error").build())) {
            assertRoundTrip(value);
        }
    }

    @Test
    void domainExceptionsKeepTheirTypeAndMessage() {
        var missing = assertDoesNotThrow(() -> new ResourceNotFoundException(9L, Report.class));
        var related = assertDoesNotThrow(() -> new RelatedEntityException(7L, Task.class));
        assertEquals("Resource with id = 9 from class = " + Report.class.getName(),
                missing.getMessage());
        assertEquals("Conflict: " + Task.class.getName()
                + " with ID = 7 is still linked to other records and cannot be deleted",
                related.getMessage());
    }

    private void assertRoundTrip(Object value) throws Exception {
        var encoded = json.writeValueAsString(value);
        var decoded = json.readValue(encoded, value.getClass());
        assertEquals(json.readTree(encoded), json.readTree(json.writeValueAsString(decoded)));
    }
}
```

## Възпроизводима проверка

От корена на хранилището изпълнете `./scripts/package-task-manager.ps1`, после `./scripts/verify-task-manager-unit-assessment.ps1` с JDK 17 или 21. Скриптът разархивира публичния ZIP в отделна папка под `.migration`, извлича трите Java класа от този файл и ги изпълнява там. Решенията не се записват в изходния starter или публичния архив.

За среда, която блокира динамичното зареждане на агента на Mockito, използвайте параметъра `-MockitoAgentPath` с абсолютния път до `byte-buddy-agent-1.15.11.jar` от локалното Maven хранилище. `-Offline` е подходящ само когато зависимостите са вече изтеглени.

Очакван резултат: 10 теста за вариант А, 10 за вариант Б, 3 проверки за съвместимост и 7 оригинални теста — общо 30 успешни изпълнения. Петте оригинални интеграционни теста използват MockMvc; двадесетте решения на контролното нямат HTTP комуникация или Spring контекст.

## Проверено на 24.11.2026

Скриптът е изпълнен върху разархивирания публичен ZIP с JDK 21, компилация за Java 17 и предварително зареден Byte Buddy агент в изолираната среда. Резултатът е `Tests run: 30, Failures: 0, Errors: 0, Skipped: 0`, `BUILD SUCCESS`. Архивът съдържа 61 файла, включително оригиналните седем теста; не съдържа класовете с решения или този файл.

SHA-256 на проверения `task-manager-starter.zip`: `A3E4C55B4255CF54615FEAFC35E6170AD0FB49177F3C44A44E646080D077CBE7`.

