# Starter — API Gateway и единна входна точка

Използва общия [platform проект](../../README.md), без копиране или нов project setup. Запазете промените от предходните упражнения. Подробните изисквания са в [упражнението](../lab05.md).

От тази директория:
```powershell
node ../../platform/tools/lab.mjs 05 build
node ../../platform/tools/lab.mjs 05 config
```

Компонентите се стартират по README на курса; Android app се пуска от Android Studio. Envoy bootstrapping, clusters, tracing/access logs, health и fixture route са готови. User/Activity routes и local error policy са TODO.

Файлове за работа (спрямо platform):
- `infra/envoy.yaml`
- `compose.yaml`
- `android/app/src/main/java/bg/tuvarna/mobile/LabViewModel.kt`

`lab.json` е manifest, а `Lab.ps1` е launcher за общия build. TODO Lxx-G обозначава водената част, Lxx-I — самостоятелната. Не премахвайте validation/cleanup само за да мине happy path.
