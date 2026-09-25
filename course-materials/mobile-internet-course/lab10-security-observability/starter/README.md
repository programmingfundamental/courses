# Starter — Security, Observability и End-to-End Analysis

Използва общия [platform проект](../../README.md), без копиране или нов project setup. Запазете промените от предходните упражнения. Подробните изисквания са в [упражнението](../lab10.md).

От тази директория:
```powershell
node ../../platform/tools/lab.mjs 10 build
node ../../platform/tools/lab.mjs 10 config
```

Компонентите се стартират по README на курса; Android app се пуска от Android Studio. JWT/OTel/metrics dependencies, issuer/audience configuration, local key fixture и slow-user scenario са готови. Business role/ownership checks и защитени routes са TODO. Fake issuer не замества productionOIDC login.

Файлове за работа (спрямо platform):
- `backend/mobile-bff/src/main/java/bg/tuvarna/mobile/HomeResource.java`
- `backend/activity-service/src/main/java/bg/tuvarna/mobile/ActivitiesResource.java`
- `backend/user-service/src/main/java/bg/tuvarna/mobile/UsersResource.java`
- `backend/mobile-bff/src/main/resources/application.properties`
- `infra/envoy.yaml`
- `infra/otel.yaml`
- `tools/token.mjs`
- `compose.slow-user.yaml`

`lab.json` е manifest, а `Lab.ps1` е launcher за общия build. TODO Lxx-G обозначава водената част, Lxx-I — самостоятелната. Не премахвайте validation/cleanup само за да мине happy path.
