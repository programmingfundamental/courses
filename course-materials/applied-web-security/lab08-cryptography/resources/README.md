# Работна карта — Task Manager, упражнение 8

След упражнение 7. Добавят се PUT/GET /tasks/{id}/private-note. Бележката не се включва в TaskResponseDto или HTML списъка.

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

Файлове: нов TaskSecretService/FieldCipher, Task.privateNoteCiphertext, TaskController, TaskPolicy.

Команди от task-manager: mvn test; след стартиране на compose.test.yml — mvn -Ppostgres-tests test. Подробности: [подготовка](../../setup.md), [условие](../lab08.md).
