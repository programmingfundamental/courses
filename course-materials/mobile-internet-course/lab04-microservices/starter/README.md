# Starter — Microservices Architecture и Service-to-Service Communication

Използва общия [platform проект](../../README.md), без копиране или нов project setup. Запазете промените от предходните упражнения. Подробните изисквания са в [упражнението](../lab04.md).

От тази директория:
```powershell
node ../../platform/tools/lab.mjs 04 build
node ../../platform/tools/lab.mjs 04 config
```

Компонентите се стартират по README на курса; Android app се пуска от Android Studio. User lookup е готов fixture, REST Client registration/config са дадени, третият module се стартира с 501 endpoint. Няма готова independent business capability.

Файлове за работа (спрямо platform):
- `backend/activity-service/src/main/java/bg/tuvarna/mobile/UsersClient.java`
- `backend/activity-service/src/main/java/bg/tuvarna/mobile/ActivitiesResource.java`
- `backend/notification-service/src/main/java/bg/tuvarna/mobile/NotificationsResource.java`

`lab.json` е manifest, а `Lab.ps1` е launcher за общия build. TODO Lxx-G обозначава водената част, Lxx-I — самостоятелната. Не премахвайте validation/cleanup само за да мине happy path.
