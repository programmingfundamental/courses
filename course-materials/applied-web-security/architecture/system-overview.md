# Task Manager — архитектура и последователност

## Начално приложение

Клиент → Spring Security/JwtAuthFilter → AuthController, TaskController, ReportController → AuthService, TaskServiceImp, ReportServiceImp → Spring Data JPA → PostgreSQL. Compose няма reverse proxy. Port: 127.0.0.1:9000; db е достъпна само по internal backend; app участва и във frontend за публикувания HTTP порт. Java 17, Spring Boot 3.4.4 и JJWT 0.12.5 са от lab11.

| Начален маршрут | Договор |
|---|---|
| POST /auth/register | JSON username/password; 200; role=USER |
| POST /auth/login | JSON credentials; 200 с tokens и сесия |
| POST /auth/refresh | JSON refreshToken; 200 с access token |
| POST /auth/logout | Сесийният контекст се изчиства |
| GET/POST /tasks | USER/ADMIN; списък/създаване |
| GET /tasks/{id} | USER/ADMIN; прочит |
| PATCH /tasks/{id}/update | USER/ADMIN; промяна |
| DELETE /tasks/{id}/delete | USER/ADMIN; изтриване |
| /reports/** | ADMIN; включва /task/{id}, /{id}, /task/{id}/summary |

Началният Task няма owner. Не приемайте роли за доказателство за ownership. Login поддържа сесия и връща JWT; CSRF е изключен в началната конфигурация и се добавя в упражнение 8.

## Надграждания по упражнения

| № | Резултат, който следващото упражнение използва |
|---|---|
| 1 | Карта на потоците и ръчна проверка на публикуваните портове |
| 2 | Валидирани credentials, смяна на session ID, GET /auth/me, регистрационни ограничения |
| 3 | Task.owner, TaskPolicy, owner-scoped списък/CRUD; ADMIN-only reports остава |
| 4 | LoginAttemptService, Clock, configurable lockout |
| 5 | GET /tasks/search?q= и /tasks/lookup?summary=, binding и sort allowlist |
| 6 | GET /ui/tasks, HTML encoding, CSP, Task.referenceUrl |
| 7 | GET /auth/csrf, session CSRF rotation, GET /ui/tasks/new и POST /ui/tasks form |
| 8 | PUT/GET /tasks/{id}/private-note, FieldCipher, key ring/migration |
| 9 | Отделен /token-api/tasks и /token-api/admin/status, строг JWT договор и refresh rotation |
| 10 | AuditFilter, обща матрица и отчет за реалния обхват |

Новите endpoints не са готови в началния starter. Студентът ги реализира последователно. Няма нова база за всяко упражнение; schema промени се прилагат чрез документирана миграция или върху ясно избрана празна база.

## Данни и изходни DTO

Task: id, summary, description, deadline; по-късно owner, referenceUrl, privateNoteCiphertext. Report: content, workedTime, task. User: username, BCrypt password, role, enabled. RefreshToken: първоначално raw token; в упражнение 10 — digest и еднократна употреба.

TaskResponseDto няма рекурсивен списък от отчети; ReportResponseDto съдържа taskId и workTime. Summary totalWorkedTime е Duration като ISO-8601 текст, например PT31H. Парола, raw refresh token и privateNoteCiphertext не се добавят в общите task/report DTO.
