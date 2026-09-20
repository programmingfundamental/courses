# Starter — Resilient Mobile API Client

Използва общия [platform проект](../../README.md), без копиране или нов project setup. Запазете промените от предходните упражнения. Подробните изисквания са в [упражнението](../lab02.md).

От тази директория:
```powershell
node ../../platform/tools/lab.mjs 02 build
node ../../platform/tools/lab.mjs 02 config
```

Компонентите се стартират по README на курса; Android app се пуска от Android Studio. Fixture endpoint и пет режима са готови. ApiClient има един конфигуриран OkHttpClient, но request mapping и retry са TODO.

Файлове за работа (спрямо platform):
- `android/app/src/main/java/bg/tuvarna/mobile/ApiClient.kt`
- `android/app/src/main/java/bg/tuvarna/mobile/LabViewModel.kt`
- `backend/mobile-bff/src/main/java/bg/tuvarna/mobile/FixtureResource.java`

`lab.json` е manifest, а `Lab.ps1` е launcher за общия build. TODO Lxx-G обозначава водената част, Lxx-I — самостоятелната. Не премахвайте validation/cleanup само за да мине happy path.
