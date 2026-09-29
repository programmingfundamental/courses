# Работна карта — Task Manager, упражнение 7

След упражнение 6. Включва се CSRF за съществуващата сесийна верига и се добавя HTML форма. До упражнение 9 POST/PATCH/DELETE с Bearer също изискват CSRF в тази обща верига.

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

Файлове: SecurityConfig, AuthController/AuthService, нов GET /auth/csrf, TaskPageController.

Команди от task-manager: mvn test; след стартиране на compose.test.yml — mvn -Ppostgres-tests test. Подробности: [подготовка](../../setup.md), [условие](../lab07.md).
