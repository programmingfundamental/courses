# Starter — Mobile Connectivity и Network State

Използва общия [platform проект](../../README.md), без копиране или нов project setup. Запазете промените от предходните упражнения. Подробните изисквания са в [упражнението](../lab01.md).

От тази директория:
```powershell
node ../../platform/tools/lab.mjs 01 build
node ../../platform/tools/lab.mjs 01 config
```

Компонентите се стартират по README на курса; Android app се пуска от Android Studio. UI shell, permission declarations и NetworkSnapshot contract са готови. TODO adapter връща Offline snapshot, докато не бъде реализиран.

Файлове за работа (спрямо platform):
- `android/app/src/main/java/bg/tuvarna/mobile/NetworkSource.kt`
- `android/app/src/main/java/bg/tuvarna/mobile/LabViewModel.kt`

`lab.json` е manifest, а `Lab.ps1` е launcher за общия build. TODO Lxx-G обозначава водената част, Lxx-I — самостоятелната. Не премахвайте validation/cleanup само за да мине happy path.
