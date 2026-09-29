# Работна карта — Task Manager, упражнение 7

След упражнение 6. Добавя се HTML изглед /ui/tasks към същото приложение. JSON REST отговор сам по себе си не изпълнява HTML; XSS се анализира при новия изходен контекст.

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

Файлове: нов TaskPageController, TaskServiceImp, SecurityConfig; по избор Task.referenceUrl.

Команди от task-manager: mvn test; след стартиране на compose.test.yml — mvn -Ppostgres-tests test. Подробности: [подготовка](../../setup.md), [условие](../lab07.md).
