# Android-базирани технологии за мобилни устройства

Практически курс за магистри: **7 лабораторни упражнения по 135 минути**. Общият Kotlin проект **Mobile Context Monitor** постепенно придобива управление на state, локално съхранение, background processing, sensors, location, BLE и измерване на ресурсите.

Android се разглежда като мобилна система с прекъсвания, permissions, ненадежден hardware и ограничени ресурси. Compose е интерфейс за управление и наблюдение. Всяко занятие следва **проблем → теория → мини експеримент → водена задача → самостоятелна задача → edge cases → измерване → анализ**. Самостоятелната задача е задължителна работа **в часа**, а не домашно.

## Аудитория и предварителни знания

Необходими са добро програмиране, Kotlin или близък език, ООП, lambdas, collections, basic concurrency и начален опит с Android. Преди Lab 1 студентът трябва да може да стартира Compose Empty Activity на emulator и да намери lifecycle logs. Настройването и изтеглянето на SDK/dependencies е предварителна подготовка, извън 135-те минути.

## Общи резултати от обучението

- Анализира lifetime на state, process и hardware resources.
- Реализира immutable UI state и еднопосочен data flow през ViewModel.
- Организира Room като local source of truth и DataStore като settings storage.
- Проектира идемпотентна WorkManager обработка и cancellation.
- Превръща шумни sensor streams в проверими събития с bounded buffers.
- Обработва location uncertainty, permissions и lifecycle като част от нормалната логика.
- Реализира BLE workflow с asynchronous callbacks, timeouts и recovery.
- Оптимизира resource consumption чрез before/after данни и аргументирани trade-offs.

## Програма и приемственост

| № | Упражнение | Използва | Добавя |
|---|---|---|---|
| 1 | [Lifecycle, State и Compose](lab01-lifecycle-state/lab01.md) | Kotlin и Android основи | Experiment Tracker, state ownership, два екрана |
| 2 | [Architecture и Local Persistence](lab02-architecture-persistence/lab02.md) | ViewModel/actions от Lab 1 | Experiment/Measurement, Room, settings Flow |
| 3 | [Background Execution](lab03-background-work/lab03.md) | repository и Room | idempotent processing, work chain, progress |
| 4 | [Sensors](lab04-sensors/lab04.md) | state, storage и cancellation | accelerometer source, filters, motion events |
| 5 | [Location](lab05-location/lab05.md) | source abstraction и lifecycle от Lab 4 | location logger, route filtering, distance |
| 6 | [Bluetooth Low Energy](lab06-bluetooth-le/lab06.md) | permissions, source state, history | GATT adapter, notifications, reconnect |
| 7 | [Performance, Energy и Robustness](lab07-performance/lab07.md) | всички pipeline-и | profiling, controlled inefficient mode, три оптимизации |

```text
Lifecycle + State
       |
       v
Architecture + Persistence
       |
       v
Background Execution
       |
       v
Sensors
       |
       v
Location
       |
       v
Bluetooth LE
       |
       v
Performance + Energy
```

## Хардуер и fallback

| Занятие | Emulator / fake | Физическо устройство |
|---|---|---|
| 1–3 | напълно достатъчен; API 33 и 37 AVD | по избор |
| 4 | virtual sensors + deterministic replay през същия interface | силно препоръчително за реален шум и sampling |
| 5 | emulator routes/GPX или fake location source | силно препоръчително за accuracy и battery наблюдения |
| 6 | задължително подготвен fake BLE transport; не се разчита на emulator radio | задължително само за реалния BLE вариант: API 37 central + peripheral |
| 7 | replay позволява correctness и относителни software сравнения | силно препоръчително; необходимо за изводи за реална енергия/radio |

BLE peripheral може да е ESP32 с готов firmware, втори Android телефон с BLE advertising/GATT server support или лабораторен simulator. Поддръжката на peripheral mode на втория телефон се проверява **преди часа**. Липсващ sensor/BLE feature не блокира задачата: fake source изпълнява същите contracts и failure сценарии. Report-ът обозначава реални и симулирани резултати.

## Софтуер и препоръчителна конфигурация

Използвайте Android Studio с поддръжка на избрания AGP, Android SDK, platform-tools, emulator и Android Profiler. Примерна **фиксирана учебна конфигурация**, подходяща за API използвани тук:

| Компонент | Базова версия / настройка |
|---|---|
| Android | `minSdk=33`, `compileSdk=37`, `targetSdk=37` |
| Gradle / AGP | Gradle 9.4.1, Android Gradle Plugin 9.2.1 |
| Gradle JDK / JVM target | JDK 17 / 17 |
| Kotlin | built-in Kotlin в AGP 9; фиксиран KGP/Compose compiler 2.3.10 |
| Compose | BOM 2025.12.00, Material 3, UI tooling/test artifacts |
| Lifecycle / Activity | 2.10.0 / activity-compose 1.12.1 |
| Navigation | navigation-compose 2.9.6 |
| Room / KSP | Room 2.8.5; KSP 2.3.6 (KSP2) |
| WorkManager / DataStore | work-runtime-ktx 2.11.0 / datastore-preferences 1.2.0 |

Това са конкретни stable baseline версии за възпроизводим курс. Запазете wrapper и version catalog в Git; преподавателят прави Gradle sync/build на лабораторните машини преди занятията. При обновяване проверете цялата dependency комбинация и изпълнете tests. Съвместимостите са описани в [AGP 9.2](https://developer.android.com/build/releases/agp-9-2-0-release-notes), [built-in Kotlin](https://developer.android.com/build/migrate-to-built-in-kotlin), [Compose compiler setup](https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler), [Compose December 2025](https://developer.android.com/blog/posts/whats-new-in-the-jetpack-compose-december-release), [Room releases](https://developer.android.com/jetpack/androidx/releases/room) и [KSP releases](https://github.com/google/ksp/releases).

Използвайте Kotlin DSL, built-in Kotlin на AGP 9 (без отделно прилагане на `org.jetbrains.kotlin.android`), `org.jetbrains.kotlin.plugin.compose` със същата resolved Kotlin версия, `buildFeatures.compose=true`, lifecycle-runtime-compose, lifecycle-viewmodel-compose, Room runtime/compiler чрез KSP, WorkManager и Preferences DataStore. Coroutines/test dependencies се фиксират в version catalog. Не използвайте динамични `+` версии. Проверете resolved KGP в Gradle dependency report и съгласувайте Compose compiler при промяна.

API 33 остава минималната runtime версия за общия проект. Реалният BLE adapter използва новия API 37 `connectGatt(BluetoothGattConnectionSettings, Executor, BluetoothGattCallback)` зад version/capability guard. При API 33–36 този adapter е Unsupported и се използва fake; не добавяме deprecated connection overloads. Основанието е [актуалният BluetoothDevice API](https://developer.android.com/reference/android/bluetooth/BluetoothDevice). Emulator API 37 проверява guard/build, но не замества физически BLE radio test.

Location лабораторията използва platform `LocationManager` и `android.location.LocationRequest`, без зависимост от Google Play Services. Нужни са само foreground permissions. UI е Compose; кратките XML фрагменти в Lab 5–6 са **manifest declarations**, не layouts.

## Проект и общи contracts

```text
ui/                 Compose screens, navigation, permission launcher
presentation/       ViewModels, immutable UiState, user actions
data/local/         Room entities, DAOs, migrations, DataStore
data/repository/    experiments, processing, sensor/location/BLE adapters
domain/             pure filters, reducer, distance policy, BLE state model
work/               CoroutineWorkers и scheduling facade
testing/            fake clock, replay sources, fake BLE transport
```

Използвайте manual dependency injection с application-level container. ViewModel получава interfaces и clock; не пази Activity. Platform adapters използват application context, когато трябва да достъпят system service. Database/DataStore имат по една application instance.

Общите идентификатори са UUID strings; persisted timestamps са epoch milliseconds за показване, а интервали се изчисляват с monotonic clock. Sensor/location sample timestamps са elapsed realtime nanoseconds в текущия boot. След restart/reboot не изваждайте несъвместими clock domains; historical timestamps не възстановяват сами live resource subscription.

Room schema се развива с реални migrations. Lab 2 добавя Experiment/Measurement; Lab 3 добавя ProcessingResult/ProcessingRun; Lab 4 — MotionEvent; Lab 5 — RoutePoint; Lab 6 — BleMeasurement. Measurement е началният числов dataset; location и BLE запазват собствените си metadata, вместо да се побират насила в едно Double поле.

Hardware collection е **foreground-only**, докато съответният екран е STARTED и потребителят е активирал наблюдението. При напускане/STOP се освобождават sensor/location/BLE resources. При process recreation UI може да възстанови избор, но collection започва отново само след актуална permission/hardware проверка и explicit user action. WorkManager обработва вече записани данни; не служи за непрекъснат sensor, location или BLE stream.

## Организация на работата и приемане

Работете в отделен студентски Git repository и пазете tags `lab01` … `lab07`. Всеки час започва с предишния checkpoint и подготвени празни interfaces/test fixtures. Материалите дават snippets и спецификации; няма готов Android application или решение на самостоятелните задачи. Snippets се поставят в описания layer с нужните imports/dependencies, а не се третират като самостоятелен build project.

Всяка самостоятелна задача има 35 минути в часовия план, acceptance criteria и собствено архитектурно решение. Преподавателят не показва съответния solution fragment преди тази част. Setup, теглене на dependencies и настройка на peripheral са предварителни; tests и анализ са в часа.

Предайте source, test results, кратки observations и измервания в `results/labXX/`. Отбележете device/API, commit, mode (real/fake), sampling/interval настройки и ограничения. Не записвайте личен маршрут или BLE identifiers в публичния report; използвайте синтетичен маршрут и псевдоними. Няма backend и не се изпращат measurements извън устройството.

За всеки resource посочете owner, start condition, stop condition, capacity и overflow policy. Permission denial и липсващ hardware са нормални UI states. Не използвайте GlobalScope, blocking main-thread I/O или unbounded lists. Пазете raw collection rate отделен от presentation rate и измервайте загубени/отхвърлени samples.

## Поддръжка на учебните материали

`labXX.md` са източникът за студентските страници. След редакция изпълнете `node scripts/sync-android-labs.mjs` от корена на repository-то; `--check` проверява синхронизацията. `instructor-notes.md` не се включват в сайта. Файловете остават видими за всеки с достъп до самото Git repository; за ограничено преподавателско разпространение използвайте отделно хранилище.
