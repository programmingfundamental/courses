# Работна карта — Task Manager, упражнение 5

След упражнение 4. Добавя се търсене по summary; запазва се owner политиката от упражнение 3. Съществуващите JPA заявки не се заменят с конкатенация.

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

Файлове: TaskRepository, TaskService/TaskServiceImp, TaskController; нови /tasks/search и /tasks/lookup.

Команди от task-manager: mvn test; след стартиране на compose.test.yml — mvn -Ppostgres-tests test. Подробности: [подготовка](../../setup.md), [условие](../lab05.md).
