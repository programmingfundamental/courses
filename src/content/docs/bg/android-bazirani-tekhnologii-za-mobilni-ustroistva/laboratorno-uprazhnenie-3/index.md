---
title: "Упражнение 3 — Android Service, Foreground Service и WorkManager"
sidebar:
  order: 3
  label: Упражнение 3
---

# Упражнение 3 — Android Service, Foreground Service и WorkManager

## 1. Инженерен проблем

Потребителят има две различни задачи: „Създай локален отчет сега“ и „Обработи всички записани measurements, когато ресурсите позволяват“. Първата е кратка, видима операция, която трябва да продължи при напускане на Activity и да има Stop. Втората може да изчака и трябва да се възстановява след прекъсване. Един screen-owned coroutine или един безкраен Service не решава и двата проблема.

**Използваме от Lab 2:** Room repository, stable IDs и приключили experiments с immutable input. **Добавяме:** lifecycle на Android `Service`, foreground execution, local binding и persistent WorkManager обработка. **Предаваме нататък:** cancellation, bounded processing и ясно определен owner. Live sensors/location/BLE в следващите занятия остават screen-owned.

## 2. Учебни цели

- Разграничава coroutine, started service, bound service, foreground service и WorkManager.
- Проследява Service callbacks и разликата между started и bound lifetime.
- Реализира кратък ForegroundService с notification, Stop и ограничено време.
- Свързва Activity чрез Binder/ServiceConnection без изтичане на references.
- Изпълнява обработката извън main thread с cooperative cancellation.
- Използва CoroutineWorker, unique work, constraints и progress.
- Проектира идемпотентна chain с ограничен retry и durable резултат.
- Проверява rotation, unbind, interruption и process restart.

## 3. Предварителни знания

Lab 2, coroutines/Flow, cancellation, Room transactions и explicit Intent. Преди часа са подготвени seed dataset, migration 1→2, batch processor, WorkerFactory wiring, notification builder и ServiceConnection adapter с места за попълване. Не се изграждат database/UI scaffolds от нулата.

## 4. Необходими инструменти

Android Studio, Logcat, emulator API 33 и API 34+ (основната конфигурация на курса е API 37), Background Task Inspector, WorkManager testing и Room testing. Физически device е по избор. Няма network, location или BLE permission; добавят се foreground-service declaration и notification permission flow.

## 5. Теоретична подготовка

### Избор на механизъм

| Механизъм | Lifetime и предназначение в това занятие |
|---|---|
| Lifecycle coroutine | Работа, която губи смисъл с owner-а на екрана. |
| Started `Service` | Компонент с explicit start/stop; сам по себе си не е durable scheduler. |
| Bound service | Client получава интерфейс през Binder; bound-only instance живее докато има bindings. |
| Foreground service | Service с foreground status и notification за видима текуща операция; тук кратък local export. |
| WorkManager | Persistent, отложима и повторяема обработка с constraints. |

`ForegroundService` е роля на компонент, наследяващ `android.app.Service`, а не отделен framework superclass. Started/bound описват начина на използване; foreground описва execution status. Един Service може едновременно да е started, foreground и bound.

**Service не създава автоматично thread или отделен process.** Callback-ите и local Binder method call могат да блокират UI, ако изпълняват тежка работа синхронно. Processor използва подходящ dispatcher; Service притежава cancellable scope, без Activity reference. Вижте [Services overview](https://developer.android.com/develop/background-work/services).

### Lifecycle и binding

| Callback / операция | Наблюдение |
|---|---|
| `onCreate()` | Инициализация на instance, scope и notification channel. |
| `onStartCommand(intent, flags, startId)` | Обработка на start command; повече команди не означават повече instances. |
| `onBind()` / `onUnbind()` | Предоставяне на IBinder / загуба на последния client. |
| `stopSelf()` / `stopService()` | Прекратяване на started състоянието; активен binding може да задържи instance. |
| `onDestroy()` | Cleanup при нормално унищожаване; не е гарантиран при process death. |

`bindService()` е asynchronous: интерфейсът е използваем след `onServiceConnected()`. За same-process binding е достатъчен local Binder; AIDL/remote IPC са извън обхвата. Activity се bind-ва в `onStart()` и освобождава binding в `onStop()`. Adapter следи отделно регистрацията и получаването на Binder, включително напускане преди callback. Не bind-вайте при всяка recomposition. [Bound services](https://developer.android.com/develop/background-work/services/bound-services).

### Foreground правила

Стартираме от explicit действие във **видима Activity** чрез `ContextCompat.startForegroundService()`, след което service незабавно извиква `ServiceCompat.startForeground()` с валидна notification. CPU/DB работа започва след promotion. Не се разчита на произволен background start; отказът се показва като UI error без retry loop. [Launch a foreground service](https://developer.android.com/develop/background-work/services/fgs/launch).

За демонстрацията използваме `shortService` на API 34+: кратка важна операция, която не може да бъде отложена. Системният срок е приблизително 3 минути; учебният export има собствен budget **до 60 секунди**, `START_NOT_STICKY` и timeout cleanup. На API 33 се подава type=0 и остава същият application budget. Това не е модел за непрекъснати sensors или обикновена отложима обработка. [Foreground service types](https://developer.android.com/develop/background-work/services/fgs/service-types#short-service).

`FOREGROUND_SERVICE` се декларира в manifest. За `shortService` няма допълнителна type-specific permission. `POST_NOTIFICATIONS` се заявява чрез UI на API 33+, но отказът не забранява самия FGS: notification остава задължителна, а видимостта ѝ в drawer се променя. Проверете и системния Task Manager. [Notification permission](https://developer.android.com/develop/ui/compose/notifications/notification-permission).

### Persistent обработка

WorkManager не гарантира точен старт или exactly-once effect. Unique work с `KEEP` ограничава enqueue, но не замества идемпотентност. `Data` съдържа само metadata (limit 10 KiB); measurements и final results са в Room. Progress описва текущ attempt. Constraints могат да спрат работа, а commit-натите effects остават. `CancellationException` се propagation-ва. Periodic work не е секунден timer. [Work requests](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work) и [Chaining](https://developer.android.com/develop/background-work/background-tasks/persistent/how-to/chain-work).
