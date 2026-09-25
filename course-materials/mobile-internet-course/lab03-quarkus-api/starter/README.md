# Starter — Quarkus Backend API и предвидим mobile contract

Използва общия [platform проект](../../README.md), без копиране или нов project setup. Запазете промените от предходните упражнения. Подробните изисквания са в [упражнението](../lab03.md).

От тази директория:
```powershell
node ../../platform/tools/lab.mjs 03 build
node ../../platform/tools/lab.mjs 03 config
```

Компонентите се стартират по README на курса; Android app се пуска от Android Studio. Maven, H2 schema и store operations са готови. Resource methods връщат 501, докато не реализирате contract; filtered query е самостоятелната част.

Файлове за работа (спрямо platform):
- `backend/activity-service/src/main/java/bg/tuvarna/mobile/ActivitiesResource.java`
- `backend/activity-service/src/main/java/bg/tuvarna/mobile/ActivityStore.java`
- `backend/activity-service/src/main/java/bg/tuvarna/mobile/Contract.java`

`lab.json` е manifest, а `Lab.ps1` е launcher за общия build. TODO Lxx-G обозначава водената част, Lxx-I — самостоятелната. Не премахвайте validation/cleanup само за да мине happy path.
