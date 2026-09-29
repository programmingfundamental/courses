# Работна карта — Task Manager, упражнение 10

След упражнение 9. Запазваме JJWT и HS256 от lab11; добавяме валидирани claims, Clock и отделна stateless верига /token-api/**. Refresh tokens се завъртат еднократно.

| Поле | Резултат |
|---|---|
| Версия/commit | |
| HTTP метод и маршрут | |
| Потребител и роля | |
| Начални данни/owner | |
| Очакван/получен статус | |
| DB преди/след | |
| Проверявано правило | |
| Test class и report | |
| Оставащ риск | |

Файлове: JwtService, JwtAuthFilter, RefreshTokenService/Repository, SecurityConfig, нов TokenTaskController.

Команди от task-manager: mvn test; след стартиране на compose.test.yml — mvn -Ppostgres-tests test. Подробности: [подготовка](../../setup.md), [условие](../lab10.md).
