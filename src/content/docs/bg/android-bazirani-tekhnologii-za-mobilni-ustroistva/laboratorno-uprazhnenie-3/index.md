---
title: "Упражнение 3 — Background Execution и WorkManager"
sidebar:
  order: 3
  label: Упражнение 3
---

# Упражнение 3 — Background Execution и WorkManager

## 1. Инженерен проблем

Потребителят започва обработка на записани measurements и напуска приложението. Coroutine, принадлежаща на екрана, може да се прекрати; при рестарт ново натискане обработва същите records втори път. Нужна е отложима работа с durable scheduling и повторяеми effects, независимо от UI lifetime.

**Използваме от Lab 2:** Room repository, stable IDs и приключили experiments с immutable input. **Предаваме към следващите лаборатории:** background обработка на вече записани данни, cancellation и bounded batch design. Live sensors/location/BLE няма да се изпълняват като постоянен Worker.

## 2. Учебни цели

- Разграничава lifecycle coroutine от persistent scheduled work.
- Реализира CoroutineWorker с bounded batch обработка.
- Осигурява идемпотентни effects при повторно изпълнение.
- Използва unique work, constraints, progress и cancellation.
- Проектира chain с success/failure/retry semantics.
- Проверява restart, interrupted work и constraint changes.
- Аргументира battery-aware scheduling и ограниченията му.

## 3. Предварителни знания

Lab 2, suspend/cancellation, Room transactions, Flow и обработка на errors. Преди часа подгответе seed dataset и WorkerFactory wiring.

## 4. Необходими инструменти

Android Studio, emulator, Background Task Inspector, WorkManager testing и Room testing. Физически device е по избор. Не се изискват network permission, BLE или sensors.

## 5. Теоретична подготовка

Lifecycle coroutine е подходяща за работа, чийто смисъл приключва с owner-а. WorkManager планира persistent, отложими задачи, които могат да бъдат повторно изпълнени при прекъсване. Не дава гаранция за точен старт или exactly-once effect. Periodic work има минимален интервал 15 минути; не го използвайте за секунден timer или sensor sampling. Constraints и system scheduling влияят кога работата може да върви. Вижте [Define work requests](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work).

```text
UI request -> scheduling facade -> WorkManager
                                    |
Room closed experiment -> bounded batch processor -> Room results
                                    |
                              WorkInfo progress -> UI
```

`CoroutineWorker` интегрира coroutine cancellation; прекъсването е cooperative. Не поглъщайте CancellationException в общ catch и не я преобразувайте в success/retry. След constraint loss running work може да бъде спряна и по-късно изпълнена отново. Commit-нати database effects не се отменят автоматично при cancellation след commit.

Unique work name предотвратява паралелно натрупване на една logical scheduling заявка според policy; то не замества идемпотентния processor. При `KEEP` нова заявка не измества незавършената chain. Child започва след successful prerequisites; failure/cancellation на parent се отразява на dependent work. Вижте [Manage work](https://developer.android.com/develop/background-work/background-tasks/persistent/how-to/manage-work) и [Chaining](https://developer.android.com/develop/background-work/background-tasks/persistent/how-to/chain-work).

Input/output `Data` е малък metadata carrier с limit 10 KiB, не контейнер за measurements. Предавайте experimentId/runId/algorithmVersion; data и durable results са в Room. Progress е временен execution status; резултатът се прочита от Room и след приключване на WorkInfo.

## 6. Мини експеримент / демонстрация — 10 минути

Стартирайте бавна synthetic обработка във screen-owned coroutine, напуснете route и сравнете completed rows. После стартирайте предварително подготвена dummy CoroutineWorker, background-нете приложението и наблюдавайте WorkInfo. Не приемайте force-stop за обикновено UI напускане: force-stopped app не продължава произволно във background и възстановяването след user launch се тества отделно.

Инжектирайте interruption след първи database commit. При наивен „append result всеки път“ retry удвоява ефекта. Запишете кой identifier трябва да направи effect идемпотентен.

## 7. Водена практическа задача — 35 минути

### Стъпка 1 — резултат и migration

Добавете database version=2 с `ProcessingResult(measurementId, algorithmVersion, normalizedValue)` и composite primary key `(measurementId, algorithmVersion)`. Foreign key към Measurement с избраната cascade policy. Transformation v1 е `value / (1 + abs(value))`; входовете от Lab 2 са finite и bounded. Това е числова нормализация, не оценка на физическа енергия.

Unprocessed означава Measurement без result за същата algorithmVersion. Query връща максимум 200 rows. Добавете additive migration 1→2 и test, че старите experiments/measurements се запазват. Не използвайте destructive migration за production path.

### Стъпка 2 — processor contract

```kotlin
data class ProcessingProgress(val completed: Int, val total: Int)
interface MeasurementProcessor {
    suspend fun processClosedExperiment(
        experimentId: String,
        algorithmVersion: Int,
        onProgress: suspend (ProcessingProgress) -> Unit
    )
}
```

Processor получава repository, не Activity/Compose. Чете batch, изчислява извън Room transaction и записва results в кратка transaction с uniqueness guard. При retry вече записаните keys се пропускат. Преди commit проверява, че parent още съществува и е приключен; delete по време на работа не трябва да пресъздава изтрити данни.

Cancellation се проверява между batches и при дълги CPU loops. Не поставяйте 10 000 measurements в Work Data или memory list. Room suspend операции не изискват blocking на main; CPU transformation използва подходящ dispatcher, а filesystem export по-късно — IO dispatcher.

### Стъпка 3 — Worker и scheduling

Създайте `ProcessMeasurementsWorker : CoroutineWorker`, който валидира input metadata и извиква processor. Dependencies се подават през application WorkerFactory/container; няма reference към ViewModel. Използвайте OneTimeWorkRequest, unique name `process:<experimentId>:v1` и KEEP. Guard-нете empty/invalid ID преди scheduling.

Водената версия връща success при обработен dataset или при нула measurements, failure при invalid/missing/open experiment. За transient failure policy ще решавате в самостоятелната задача. Progress се публикува след batch, не за всеки row. UI наблюдава unique WorkInfo и durable result count без polling coroutine на всеки frame.

### Стъпка 4 — cancellation и restart

Добавете Cancel action към scheduling facade. След cancellation UI показва Cancelled, а вече commit-натите results остават валидни. При нов schedule processor довършва липсващите keys. Process restart през system kill не отменя durable scheduling intent, но моментът на изпълнение остава под control на scheduler-а.

## 8. Checkpoint — 5 минути

- 1000 measurements се обработват на bounded batches и дават 1000 уникални results.
- Повторно enqueue по време на running work не създава втора logical chain.
- Interruption след commit и retry не удвояват result count.
- Empty dataset завършва смислено; missing/open experiment дава failure.

## 9. Самостоятелна задача — 35 минути, в часа

Реализирайте **Prepare → Process/Export** chain за приключил experiment. Prepare валидира immutable input и записва `ProcessingRun(runId, experimentId, state, itemCount)` в Room; вторият Worker обработва/експортира обобщение като Room record с ключ runId. Няма изискване за public file export или storage permission.

**Функционални изисквания:** stable runId се създава при user action; metadata се предава между Workers; unique chain използва KEEP; има поне един реално релевантен constraint — BatteryNotLow или StorageNotLow. Резултатът съдържа брой measurements, min/max/mean и algorithmVersion. Повторен attempt със същия runId не създава второ summary.

**Технически ограничения:** max 200 rows в batch; aggregation е incremental; constraints се поставят на нужните WorkRequests изрично. Добавете exponential backoff с начални 30 s и максимум 3 attempts общо. Определете кои errors са permanent и кои transient; CancellationException се propagation-ва, не се класифицира като service failure. Използвайте test-injected transient storage failure за възпроизводим retry.

**Приемане:** няма child execution след parent failure; duplicate enqueue не умножава chain; transient failure се възстановява в budget; permanent error не retry-ва безкрайно; constraint change и cancellation не повреждат Room; след restart UI намира chain по identity. Предайте собствена decision table `condition → success/failure/retry`, а не готова таблица от преподавателя.

## 10. Edge cases

| Случай | Проверка |
|---|---|
| Interrupted след batch commit | няма дублирани резултати |
| Empty dataset | success с count=0, min/max=N/A |
| Constraint отпада | work спира/се отлага, UI не обещава точен старт |
| Duplicate schedule | една unfinished unique chain |
| Prepare failure | child не обработва/export-ва |
| Process restart | metadata и durable results са откриваеми |
| Parent delete | terminal missing-input behavior, без resurrection |
| CancellationException | не се поглъща като success |

## 11. Тестване

Unit tests за normalization и incremental aggregate; Room tests за composite uniqueness, migration и retry after commit. WorkManager TestDriver/TestListenableWorkerBuilder проверява constraints, return results и chain dependencies без реално чакане за system scheduler. Отделен integration run на emulator показва actual WorkInfo transitions; тестовият scheduler не доказва точни OS timings. Направете rotation по време на work и проверете, че UI не го enqueue-ва отново при recomposition.

## 12. Наблюдение и измерване — 10 минути

Съберете WorkRequest IDs, unique name, attempt count, start/stop reasons, completed rows, batch size и progress update count. Използвайте Background Task Inspector и Room counts преди/след interruption. Запишете elapsed processing duration, но не изваждайте wall timestamps за точни CPU intervals. Сравнете 1000/10 000 records и докажете, че peak batch размерът остава 200, а не целият dataset.

## 13. Въпроси за анализ

1. Защо coroutine във ViewModel не е durable work scheduler?
2. Какво гарантира unique work и какво не гарантира?
3. Коя граница прави effect идемпотентен при crash след commit?
4. Защо CancellationException не е нормален transient failure?
5. Как constraint loss се различава от permanent input error?
6. Защо measurements не се предават чрез Work Data?
7. Как бихте показали progress след process restart и след final success?

## 14. Очакван резултат

Persistent background processor и самостоятелна unique chain с constraints, bounded retry и durable summary. WorkManager е отделен от foreground UI и бъдещите hardware subscriptions.

## 15. Критерии за приемане

- [ ] UI не притежава background processing lifetime.
- [ ] Worker валидира input и обработва bounded batches.
- [ ] Effects са идемпотентни чрез durable keys/transactions.
- [ ] Progress и final result имат различни sources.
- [ ] Самостоятелната chain има поне два Workers, constraint и exponential backoff.
- [ ] Success/failure/retry са аргументирани и tested.
- [ ] Cancellation, constraint loss, restart и duplicates са проверени.
- [ ] Migration запазва данните от Lab 2.
