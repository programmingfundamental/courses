# Starter — Realtime Communication и Event-Driven Architecture

Използва общия [platform проект](../../README.md), без копиране или нов project setup. Запазете промените от предходните упражнения. Подробните изисквания са в [упражнението](../lab09.md).

От тази директория:
```powershell
node ../../platform/tools/lab.mjs 09 build
node ../../platform/tools/lab.mjs 09 config
```

Компонентите се стартират по README на курса; Android app се пуска от Android Studio. Broker/connector/relay и socket callback plumbing са готови. EventPolicy връща false до реализация; command/version update и reconnect са TODO. Транспортът не съдържа готова dedup policy.

Файлове за работа (спрямо platform):
- `backend/activity-service/src/main/java/bg/tuvarna/mobile/EventPublisher.java`
- `backend/activity-service/src/main/java/bg/tuvarna/mobile/ActivitiesResource.java`
- `backend/notification-service/src/main/java/bg/tuvarna/mobile/EventConsumer.java`
- `backend/mobile-bff/src/main/java/bg/tuvarna/mobile/RealtimeSocket.java`
- `android/app/src/main/java/bg/tuvarna/mobile/RealtimeSource.kt`
- `android/app/src/main/java/bg/tuvarna/mobile/EventPolicy.kt`
- `infra/envoy.yaml`

`lab.json` е manifest, а `Lab.ps1` е launcher за общия build. TODO Lxx-G обозначава водената част, Lxx-I — самостоятелната. Не премахвайте validation/cleanup само за да мине happy path.
