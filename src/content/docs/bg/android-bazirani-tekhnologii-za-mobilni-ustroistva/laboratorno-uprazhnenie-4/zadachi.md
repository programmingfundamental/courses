---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 6. Мини експеримент / демонстрация — 10 минути

За 20 s в покой и 10 s бавно завъртане наблюдавайте axes и magnitude. Заявете 25, после 100 Hz и изчислете действителния rate от timestamps. Background-нете неправилната demo версия с listener без cleanup и вижте продължаващ counter. После прекратете demo subscription.

Без устройство replay-нете trace: stationary `(0,0,9.81)` с seeded noise ±0.2; след това rotation със запазена приблизителна magnitude. Отбележете кои наблюдения са synthetic и не доказват реална sensor calibration.

## 7. Водена практическа задача — 35 минути

### Стъпка 1 — source boundary и status

```kotlin
data class AccelerationSample(
    val sequence: Long,
    val elapsedNanos: Long,
    val x: Float, val y: Float, val z: Float
)
interface AccelerometerSource {
    fun samples(periodUs: Int): Flow<AccelerationSample>
}
```

Добавете source status: Available, Unsupported, Stopped, Failed. Android adapter използва SensorManager/SensorEventListener; fake adapter чете trace със същите units и timing semantics. UI избира mode през dependency configuration, а не чрез raw SensorManager calls.

### Стъпка 2 — registration и cleanup

Обвийте callback с callbackFlow и `awaitClose`, който unregister-ва **същия listener instance**. Registration failure става explicit state. Channel capacity=128; callback използва non-blocking trySend, а full channel означава drop-newest и increment на dropped counter. Не стартирайте coroutine за всеки SensorEvent, която да чака безкрайно enqueue.

Един acquisition owner управлява subscription. Lifecycle bridge изпраща visible/hidden action към ViewModel/session controller; controller collect-ва repository само когато screen е STARTED и user е избрал Start. Stop, navigation away и lifecycle STOP cancel-ват acquisition, което достига awaitClose. UI collection сама по себе си не спира отделен постоянен repository collector. Тествайте, че два consumers (UI и persistence) не създават два platform listeners.

### Стъпка 3 — filter и presentation

Изберете moving average N=5 или low-pass tau=0.2 s като начален filter. Pure filter функция получава sample и връща immutable raw/filtered projection. UI показва X/Y/Z, raw magnitude и filtered values; presentation rate е максимум 10 Hz. Задаване на sampling 25/50/100 Hz и filter parameter се валидира и преконфигурира стария subscription без overlap.

Не правете Room insert в sensor callback. Водената версия пази само bounded recent window (до 200 samples) за наблюдение. Persistence на значими motion events е част от самостоятелната работа.

### Стъпка 4 — metrics

Покажете observed Hz, accepted/dropped sample counts, activeListenerCount и presentationUpdates. Monotonic intervals са в текущия boot; wall clock се добавя отделно само при persisted event. На Stop counters остават видими, но acquisition трябва да спре.

## 8. Checkpoint — 5 минути

- Raw и filtered axes/magnitude се виждат за реален или fake source.
- Промяна на filter parameter променя response, без нов leak.
- UI се обновява до 10 Hz при acquisition до 100 Hz.
- След Stop/Home/navigation activeListenerCount=0; Unsupported е нормален state.

## 9. Самостоятелна задача — 35 минути, в часа

Добавете **Shake/Motion Detector** със configurable threshold, minimum interval между събития, визуална индикация и count. Записвайте последните 100 events в Room с id, experimentId, epoch timestamp, monotonic timestamp за текущия run, peak и algorithm parameters.

**Изисквания:** stationary noise и единичен кратък spike не трябва да водят системно до false events; продължително движение не трябва да увеличава count на всеки sample; threshold и cooldown са persistent settings. Detection е преди UI sampling. Приложете migration 2→3 за MotionEvent table и bounded single-writer path.

**Ограничения:** без unbounded sample/event list, без DB write в callback, без готов sensor library. Самостоятелно изберете feature (magnitude/filtered difference), window/hysteresis/duration условие и reset policy при STOP/сменен experiment. Опишете алгоритъма и какво губи при drop на input.

**Приемане:** на предоставен stationary trace няма events; isolated 10 ms spike се отхвърля според избраната documented policy; маркиран sustained motion води до поне едно event, а интервалите спазват cooldown. Два отделени bursts дават две събития при допустим интервал. Room history остава <=100; UI count се обновява и след rotation; lifecycle STOP не оставя listener. Алгоритъмът и параметрите се аргументират, без step-by-step solution.

## 10. Edge cases

| Случай | Очакване |
|---|---|
| Sensor липсва | Unsupported и fake mode |
| Producer е по-бърз от consumer | bounded queue и видими drops |
| Home/navigation away | unregister и acquisition=0 |
| Рязък единичен spike | filter/detector policy предотвратява необоснован event |
| Rotation | без дублирани listeners; orientation ефект е обяснен |
| Non-finite value/невалиден timestamp | reject и counter |
| Parameter change по време на run | стар subscription приключва преди новия |
| Parent experiment е изтрит | persistence отказва late event, без resurrection |

## 11. Тестване

Pure tests: constant signal, step response, noise, spike, two bursts, cooldown boundary и invalid dt. Fake clock/trace премахва sleep от тестовете. Instrumented test проверява lifecycle cleanup и Room retention/migration. Happy path е controlled shake или synthetic burst; invalid state е Start при Unsupported/без experiment; resource test повтаря 20 Start/Stop/rotation cycles и проверява listener count и bounded queue.

## 12. Наблюдение и измерване — 10 минути

За 25/50/100 Hz запишете requested/observed rate, drops, UI updates/s и filter delay при step trace. За detector маркирайте expected events предварително и пребройте true positives, false positives и missed events. Не настройвайте threshold само по един удобен trace. Запишете active listeners преди/след Home, както и максимален recent-window size. `results/lab04/` съдържа параметри и real/fake mode.

## 13. Въпроси за анализ

1. Защо accelerometer magnitude при покой не е нула?
2. Как sampling frequency влияе на filter delay и detector behavior?
3. Каква цена плащате за по-силно изглаждане?
4. Защо StateFlow не е event journal?
5. Кой точно cancel path unregister-ва listener?
6. Какво става с detection при dropped samples?
7. Защо UI не трябва да се обновява при всеки raw sample?

## 14. Очакван резултат

Lifecycle-safe accelerometer monitor и самостоятелен motion detector с bounded history, explicit sampling/filter settings и измерена detection quality. Същият source/repository модел преминава към location.

## 15. Критерии за приемане

- [ ] Има реален adapter и deterministic fake fallback.
- [ ] SensorEvent values се копират и units/time са описани.
- [ ] Raw/filtered X/Y/Z и magnitude се показват.
- [ ] Acquisition и presentation rate са разделени.
- [ ] Listener и queues са bounded и се освобождават при STOP.
- [ ] Самостоятелният detector има threshold, cooldown, false-positive policy и Room history.
- [ ] Tests покриват signal, lifecycle и resource failure.
- [ ] Измерени са observed Hz, drops и detection outcomes.
