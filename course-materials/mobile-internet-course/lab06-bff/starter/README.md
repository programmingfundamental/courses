# Starter — Backend for Frontend и mobile-specific aggregation

Използва общия [platform проект](../../README.md), без копиране или нов project setup. Запазете промените от предходните упражнения. Подробните изисквания са в [упражнението](../lab06.md).

От тази директория:
```powershell
node ../../platform/tools/lab.mjs 06 build
node ../../platform/tools/lab.mjs 06 config
```

Компонентите се стартират по README на курса; Android app се пуска от Android Studio. BFF REST clients/config са готови, Home връща 501. Запазете третата service capability от Lab 4; няма предварително реализирано aggregation решение.

Файлове за работа (спрямо platform):
- `backend/mobile-bff/src/main/java/bg/tuvarna/mobile/HomeResource.java`
- `backend/mobile-bff/src/main/java/bg/tuvarna/mobile/ActivitiesClient.java`
- `backend/mobile-bff/src/main/java/bg/tuvarna/mobile/UsersClient.java`
- `infra/envoy.yaml`

`lab.json` е manifest, а `Lab.ps1` е launcher за общия build. TODO Lxx-G обозначава водената част, Lxx-I — самостоятелната. Не премахвайте validation/cleanup само за да мине happy path.
