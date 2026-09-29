# Работна карта — Task Manager, упражнение 4

След упражнение 3. Добавят се owner към Task и TaskPolicy. Старите задачи се присвояват чрез явна миграция; новите получават текущия потребител на сървъра.

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

Файлове: Task, TaskRepository, TaskServiceImp, TaskController, SecurityConfig.

Команди от task-manager: mvn test; след стартиране на compose.test.yml — mvn -Ppostgres-tests test. Подробности: [подготовка](../../setup.md), [условие](../lab04.md).
