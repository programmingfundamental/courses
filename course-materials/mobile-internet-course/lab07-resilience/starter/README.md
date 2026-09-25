# Starter — Resilience и Fault Tolerance

Използва общия [platform проект](../../README.md), без копиране или нов project setup. Запазете промените от предходните упражнения. Подробните изисквания са в [упражнението](../lab07.md).

От тази директория:
```powershell
node ../../platform/tools/lab.mjs 07 build
node ../../platform/tools/lab.mjs 07 config
```

Компонентите се стартират по README на курса; Android app се пуска от Android Studio. Unstable service и единичният client call работят. Annotations, classification и fallback са TODO; parallel request harness се подготвя предварително.

Файлове за работа (спрямо platform):
- `backend/mobile-bff/src/main/java/bg/tuvarna/mobile/DownstreamPolicy.java`
- `backend/mobile-bff/src/main/java/bg/tuvarna/mobile/UnstableClient.java`
- `backend/notification-service/src/main/java/bg/tuvarna/mobile/UnstableResource.java`

`lab.json` е manifest, а `Lab.ps1` е launcher за общия build. TODO Lxx-G обозначава водената част, Lxx-I — самостоятелната. Не премахвайте validation/cleanup само за да мине happy path.
