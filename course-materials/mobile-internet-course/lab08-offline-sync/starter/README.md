# Starter — Offline-First и Synchronization

Използва общия [platform проект](../../README.md), без копиране или нов project setup. Запазете промените от предходните упражнения. Подробните изисквания са в [упражнението](../lab08.md).

От тази директория:
```powershell
node ../../platform/tools/lab.mjs 08 build
node ../../platform/tools/lab.mjs 08 config
```

Компонентите се стартират по README на курса; Android app се пуска от Android Studio. Room entities/DAO, Worker shell, H2 ledger schema и response-drop proxy са готови. HTTP helper е решението от Lab2; queue transitions и atomic dedup са TODO.

Файлове за работа (спрямо platform):
- `android/app/src/main/java/bg/tuvarna/mobile/LocalDatabase.kt`
- `android/app/src/main/java/bg/tuvarna/mobile/SyncWorker.kt`
- `backend/mobile-bff/src/main/java/bg/tuvarna/mobile/HomeResource.java`
- `backend/activity-service/src/main/java/bg/tuvarna/mobile/ActivityStore.java`
- `backend/activity-service/src/main/java/bg/tuvarna/mobile/ActivitiesResource.java`
- `tools/drop-response-proxy.mjs`

`lab.json` е manifest, а `Lab.ps1` е launcher за общия build. TODO Lxx-G обозначава водената част, Lxx-I — самостоятелната. Не премахвайте validation/cleanup само за да мине happy path.
