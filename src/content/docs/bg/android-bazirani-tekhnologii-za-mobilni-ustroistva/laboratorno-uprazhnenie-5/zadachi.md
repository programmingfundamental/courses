---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 6. Мини експеримент / демонстрация

Подайте синтетични точки около едно място с accuracy 80 m и jitter до 30 m. Наивно сумирайте всички съседни distances и сравнете с реално нулевото движение в сценария. После добавете стар sample с timestamp отпреди 2 min и единичен далечен jump.

На emulator откажете permission, после дайте approximate. Запишете кои UI states липсват в наивната версия. Не използвайте личен реален маршрут за test report.

## 7. Водена практическа задача

### Стъпка 1 — immutable fix и status

```kotlin
data class LocationFix(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float?,
    val epochMillis: Long,
    val elapsedNanos: Long,
    val provider: String
)
interface LocationSource {
    fun updates(intervalMs: Long): Flow<LocationFix>
}
```

Status включва Stopped, AwaitingPermission, Denied, Approximate, Precise, ProviderDisabled, Unsupported и Error. Permission capability и acquisition status могат да бъдат отделни полета, за да не се смесват причина и текуща операция. Подайте immutable copy, не mutable framework Location object.

### Стъпка 2 — permission и provider workflow

Compose използва Activity Result permission contract след натискане Start; няма launcher в recomposition loop. Denial оставя usable screen с обяснение и повторен explicit action/settings route, когато е уместно. Не показвайте безкрайни dialogs. Проверете provider availability; при coarse grant изберете разрешен provider, ако има такъв, или Limited/Unsupported state с fake fallback.

### Стъпка 3 — source ownership

Repository обвива listener с callbackFlow и cleanup в awaitClose. Session controller от Lab 4 start-ва само при visible STARTED screen + user intent + grant. Home, Stop, navigation и permission loss cancel-ват updates. При interval change първо приключва старото registration. Използвайте 2, 5 и 10 s като избори; lower interval не гарантира по-добра accuracy.

Callback не пише директно в Room. Bounded writer queue до 64 fixes записва data off main; overflow се отчита и за logger означава пропуснати records. Основни guards отхвърлят invalid lat/lon, missing/negative/non-finite accuracy и backwards/duplicate monotonic timestamps.

### Стъпка 4 — локален logger

Добавете schema version=4 и RoutePoint table с id, experimentId, координати, accuracy, двата timestamps, source mode и decision metadata. Migration 3→4 запазва MotionEvent. Водената част записва basic-valid fixes и показва последна позиция, accuracy, wall timestamp и sample count. UI историята е ограничена до 200 points, durable route до 10 000 на experiment; при достигане stop recording с explicit state, вместо безкраен растеж.

## 8. Checkpoint

- Emulator/fake route води до records и актуален sample count.
- Denied и approximate grant са различими и не crash-ват.
- Interval се променя без дублирани subscriptions.
- След Home/Stop няма active location listener; историята остава.

## 9. Самостоятелна задача

Реализирайте **Mobility Tracker** върху logger-а: accepted route, total distance и обяснима rejection причина за всеки неприет sample.

**Изисквания:** правила за freshness, минимална acceptable accuracy, duplicate/out-of-order samples и unrealistic speed/jump; явна политика за approximate mode; запазване на accepted route и total след restart. UI показва total distance само когато има достатъчно качество, иначе status с причина. Route display може да е bounded list, без map SDK.

**Ограничения:** policy е pure Kotlin функция с injectable distance calculator/clock. Изберете и аргументирайте thresholds и дали отхвърлен sample променя last accepted anchor. При Home/дълга пауза започнете нов route segment, за да не сумирате непроверено движение през gap. Room transaction пази point и contribution, така че повторна обработка на същия ID да не увеличава total втори път. Няма background tracking.

**Приемане:** synthetic stationary jitter не създава голям false distance; controlled маршрут с добра accuracy дава разумно близък резултат до fixture reference; stale/duplicate/jump са открити; coarse mode остава usable, без измислена точност; permission revoke спира collection; restart запазва total и не свързва автоматично нов segment със стар anchor. Предайте policy таблица и поне пет labelled test cases; не се предоставя готов алгоритъм за thresholds.

## 10. Edge cases

| Случай | Очакване |
|---|---|
| Permission denied | usable Denied state и explicit retry |
| Permission revoked по време на run | stop, cleanup и recheck |
| Location disabled | ProviderDisabled без spinner завинаги |
| Poor/approximate accuracy | degraded output, distance може да е unavailable |
| Stale point | age-based reject с причина |
| Duplicate/out-of-order sample | няма double distance |
| Unrealistic jump | policy reject, без замърсен anchor |
| Background/restart gap | нов segment, без фиктивна връзка |

## 11. Тестване

Pure tests с synthetic latitude/longitude: straight segment, stationary jitter, zero/negative dt, stale point, missing accuracy и jump. Distance calculator се тества отделно с известни приблизителни reference distances. Permission tests на emulator проверяват deny/approximate/precise/revoke; fake capability позволява deterministic unit coverage. Lifecycle test повтаря navigation/rotation и следи registration count. Room test проверява migration, unique point и idempotent total contribution.

## 12. Наблюдение и измерване

Сравнете intervals 2/5/10 s при един и същ synthetic route: delivered fixes, accepted/rejected by reason, recorded points, total distance и active listener time. На реално устройство добавете observed accuracy distribution и provider; не извеждайте battery consumption от един кратък emulator run. Report-вайте source mode и uncertainty, а не само число за distance. Logs използват synthetic coordinates или redacted identifiers.

## 13. Въпроси за анализ

1. Защо сборът от distances при неподвижен телефон може да расте?
2. Как accuracy radius влияе на meaningful movement threshold?
3. Защо age се измерва с monotonic time, а UI показва wall time?
4. Какво прави приложението при approximate grant и защо?
5. Защо rejected point не трябва автоматично да стане нов anchor?
6. Как background gap влияе на интерпретацията на route?
7. Как по-рядък interval влияе едновременно на energy и route fidelity?

## 14. Очакван резултат

Foreground location logger и самостоятелен Mobility Tracker с permissions, persistent route, обоснован filtering и честно degraded поведение. Реалната точност е зависима от средата и е описана като ограничение.

## 15. Критерии за приемане

- [ ] UI, ViewModel, repository и platform source са разделени.
- [ ] Precise/approximate/denied/revoked се обработват.
- [ ] Location listener се освобождава при STOP и permission loss.
- [ ] Coordinates, accuracy, age и order се валидират.
- [ ] Самостоятелният tracker пази route/total и не double-count-ва.
- [ ] Segments прекъсват при gap/restart.
- [ ] История и writer queue са bounded.
- [ ] Има emulator/fake tests и измервания с обозначена uncertainty.
