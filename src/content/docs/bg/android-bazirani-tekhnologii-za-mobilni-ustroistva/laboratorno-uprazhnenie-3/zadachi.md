---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 6. Мини експеримент / демонстрация

В подготвения diagnostic екран сравнете четири режима върху synthetic input:

1. Screen coroutine: напуснете route и наблюдавайте cancellation.
2. Bound-only Service: bind/unbind без start; запишете callback sequence.
3. Started foreground Service: Start → Home → връщане → bind → Stop. Сравнете lifetime на операцията с lifetime на Activity.
4. CoroutineWorker: enqueue, напуснете UI, наблюдавайте WorkInfo и durable result.

Logcat записва instance ID, callback, startId и thread name. Не добавяйте blocking sleep на main. Преподавателят демонстрира interruption след database commit и повторен attempt. Force-stop е отделен случай, не доказателство за обикновено напускане на UI.

## 7. Водена практическа задача

### Стъпка 1 — общ processor и два execution пътя

Използвайте подготвената migration 1→2 и `ProcessingResult(measurementId, algorithmVersion, normalizedValue)` с composite key. Transformation v1 е `value / (1 + abs(value))` за finite вход. Processor чете до 200 липсващи results, изчислява извън transaction и записва с uniqueness guard. Проверява cancellation и липсващ/изтрит parent. Migration test запазва данните от Lab 2.

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

Двата пътя използват общия processor: **„Отчет сега“ → Service** и **„Обработи по-късно“ → Worker**. Учебният UI допуска само един режим за experiment, докато е активен; проверете и уникалността на записите в Room при повторение. Local export записва app-private файл с runId чрез temporary file и final rename; не изисква storage permission. Export helper е предоставен, а execution lifetime реализирате вие.

### Стъпка 2 — Service declaration и foreground start

Създайте `work.ExportForegroundService : Service`, с `exported=false`. Това е manifest fragment, не самостоятелен manifest:

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<!-- В <application>; package/namespace е този на вашия проект. -->
<service
    android:name=".work.ExportForegroundService"
    android:exported="false"
    android:foregroundServiceType="shortService" />
```

От бутона Start във видимата Activity изпратете explicit Intent с action и experimentId/runId. Channel с `IMPORTANCE_LOW`, small icon, progress и Stop action са подготвени. В обработката на Start извикайте този fragment **преди** processor:

```kotlin
val type = if (Build.VERSION.SDK_INT >= 34) {
    ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
} else 0
ServiceCompat.startForeground(this, 31, notification, type)
```

`notification` идва от предоставения builder; imports са `android.os.Build`, `android.content.pm.ServiceInfo` и `androidx.core.app.ServiceCompat`. В `onStartCommand()` разграничете Start/Stop, валидирайте metadata и върнете `START_NOT_STICKY`. Поддържайте един active job: повторен Start не започва втори export и не удължава application budget. Invalid Start след foreground launch също приключва startup протокола и веднага освобождава service.

### Стъпка 3 — job, cancellation и Stop

Service има собствен `SupervisorJob`/scope. Processor е cancellation-aware; CPU и blocking file I/O използват подходящи dispatchers. Ограничете работата до 60 s и публикувайте progress най-много след batch. Не използвайте `GlobalScope`, `Thread.sleep()` или Room blocking calls на main.

Един общ terminal path обработва success, error, Cancel и timeout: отменя active job, освобождава temporary resources, премахва foreground status и прекратява started състоянието. `stopForeground(STOP_FOREGROUND_REMOVE)` само по себе си не спира Service. Ако Activity още е bound, instance може да остане, но **операцията вече трябва да е спряна**. Не отлагайте cancellation единствено за `onDestroy()`. Докато job се освобождава, състоянието е Stopping и не приема нов Start; runId/generation guard не позволява стар callback да приключи по-нова операция. [Stopping a foreground service](https://developer.android.com/develop/background-work/services/fgs/stop-fgs).

Notification Stop използва предоставения explicit immutable `PendingIntent.getService()` за `ACTION_STOP`; този command не стартира нова обработка и не прави foreground promotion. Приложете същия cleanup от `onTimeout(startId)` на API 34+ и като fallback при `onDestroy()`. Timeout handler не довършва дълга работа. На API 33 собствената времева граница е задължителна. [Service API](https://developer.android.com/reference/android/app/Service#onTimeout(int)).

### Стъпка 4 — bound interface към Activity

Определете малък интерфейс за текущ state и Cancel. Следният fragment е **част от Service класа**; `status` и `cancelExport()` се реализират в задачата:

```kotlin
inner class LocalBinder : Binder() {
    fun state(): StateFlow<ExportState> = status
    fun cancel() = cancelExport()
}
private val binder = LocalBinder()
override fun onBind(intent: Intent): IBinder = binder
```

`ExportState` различава Idle/Running/Stopping/Completed/Cancelled/Failed. `state()` връща read-only Flow; методите не извършват тежка синхронна работа. Използвайте подготвения `ServiceConnection` adapter: свържете Binder в `onServiceConnected()`, изчистете reference при disconnect/unbind и observe-вайте с lifecycle. Service не пази Activity; ViewModel не пази ServiceConnection или Service instance.

Първо bind-нете без Start и проверете, че **не се прави export**. После Start, Home и повторно отваряне показват оставащата started операция. След Stop и unbind няма active Service job. След process death Room/файлът са източник на final резултат, а in-memory Binder state не се приема за възстановен.

### Стъпка 5 — persistent Worker

В подготвения `ProcessMeasurementsWorker : CoroutineWorker` свържете processor през application WorkerFactory. Валидирайте IDs, върнете success за приключил dataset (включително празен), failure за invalid/missing/open experiment. Progress се обновява след batch.

Използвайте OneTimeWorkRequest, unique name `process:<experimentId>:v1` и `KEEP`. UI наблюдава WorkInfo и Room; Cancel извиква scheduling facade. При нов schedule processor довършва липсващите keys. Не стартирайте FGS от Worker като заобикаляне на background restrictions. Failure/retry classification и dependent chain са самостоятелната задача.

## 8. Checkpoint

- Callback trace различава bound-only от started+bound Service.
- Export стартира от visible UI, показва notification и продължава след Home.
- Stop отменя job дори при останал binding; повторен Start не дублира export.
- След rotation Activity се свързва отново без втори job или binding leak.
- Worker обработва 1000 measurements в bounded batches с 1000 уникални results.
- Interruption след commit и повторен attempt не удвояват result count.

## 9. Самостоятелна задача

Реализирайте **Prepare → Process/Export** WorkManager chain за приключил experiment. Prepare валидира immutable input и записва `ProcessingRun(runId, experimentId, state, itemCount)` в Room; вторият Worker записва summary с ключ runId. Няма изискване за public file export.

**Функционални изисквания:** stable runId от user action; metadata между Workers; unique chain с KEEP; релевантен constraint — BatteryNotLow или StorageNotLow. Summary съдържа count, min/max/mean и algorithmVersion. Повторен attempt със същия runId не създава втори summary. Добавете кратка decision table: кой механизъм избирате за кратък текущ export, обработка след време и работа само докато екранът е видим, и защо.

**Технически ограничения:** max 200 rows в batch; incremental aggregation; constraints за нужните WorkRequests. Exponential backoff започва от 30 s, максимум 3 attempts общо. Определете permanent/transient errors; CancellationException се propagation-ва. Използвайте injected transient storage failure. Не заменяйте durable chain с `START_STICKY`, безкраен Service или автоматичен FGS restart.

**Приемане:** parent failure блокира child; duplicate enqueue не умножава chain; transient failure се възстановява в budget; permanent error няма endless retry. Constraint change, cancellation и restart запазват коректни Room results. Предайте авторска таблица `condition → success/failure/retry` и аргументирайте избора Service/WorkManager спрямо изискването за време и lifetime.

## 10. Edge cases

| Случай | Проверка |
|---|---|
| Rotation / Home по време на export | unbind не отменя started работа; новият UI не стартира втори job |
| Напускане преди onServiceConnected | adapter освобождава binding registration; няма stale Binder/UI callback |
| Stop, докато Activity още е bound | job и foreground status приключват преди евентуалното onDestroy |
| Повторен Start / invalid input | един active job; explicit rejection/cleanup, без забравена notification |
| Отказан POST_NOTIFICATIONS | няма crash; различават се drawer visibility и FGS execution |
| Недопустим background FGS start | controlled error; няма retry loop |
| Application/system timeout | bounded cancellation и stop, без оставена foreground работа |
| Process death / force-stop | final state не зависи от onDestroy; не се обещава автоматичен Service restart |
| Interrupted след batch commit | няма duplicate results при повторен Worker attempt |
| Empty / missing / deleted input | count=0 или terminal failure; изтрит parent не се пресъздава |
| Constraint loss / Prepare failure | отлагане/спиране на work; child не прави side effects |

За FGS startup/timeout failure използвайте отделен debug вариант под контрол на преподавателя; не оставяйте дефектния вариант в основната задача. [Troubleshooting](https://developer.android.com/develop/background-work/services/fgs/troubleshooting).

## 11. Тестване

**Service integration:** Start → Home → return → Stop; rotation; bind-only → unbind; Stop при active binding; 20 повторни Start действия; permission allow/deny. Съберете callback sequence и active-job count. Проверете `adb shell dumpsys activity services <package>` след Stop **и** unbind. На API 33 и 34+ проверете различния foreground type path. Application timeout се тества с кратък injected budget; това не доказва OS timeout — той е отделно наблюдение.

**Processor/Worker:** unit tests за normalization/aggregate; Room tests за migration, uniqueness и retry after commit; WorkManager TestDriver/TestListenableWorkerBuilder за constraints, chain и return results. Реалният emulator run проверява WorkInfo transitions, без обещание за точни scheduler timings.

**Прекъсване:** fake processor/latch спира след commit; cancellation не добавя втори result. Process death на FGS и force-stop са отделни supervised проверки; не приемайте `am kill` за надежден начин да прекратите foreground process. След relaunch не извиквайте автоматично Start от recomposition.

## 12. Наблюдение и измерване

Запишете за Service: instance/startId, callback order, thread name, bindings, active jobs, time to first notification, duration, Stop-to-job-end latency и terminal reason. За Worker: WorkRequest/unique name, attempts, constraints, completed rows и batch size. Финалният result се проверява в Room/файла, а не само на екрана.

Сравнете 1000/10 000 records с batch<=200. Използвайте monotonic clock за intervals. Foreground status не означава нулев battery cost; не правете енергийни изводи от кратък emulator run. Запишете резултатите в `results/lab03/`.

## 13. Въпроси за анализ

1. Защо Service не е равнозначен на background thread?
2. Кога unbind унищожава Service и кога started lifetime остава?
3. Какво остава след stopForeground и след stopSelf при active binding?
4. Защо onDestroy и START_STICKY не дават durable execution contract?
5. Кога непосредствената видима операция оправдава FGS вместо Worker?
6. Какво гарантира unique work и защо все пак е нужна идемпотентност?
7. Защо cancellation след commit не отменя записаните effects?
8. Как notification denial, background-start restriction и process death се различават?

## 14. Очакван резултат

Кратък Android Service с foreground notification, local Binder, explicit Stop и проверен lifecycle; отделна persistent WorkManager chain с constraints, bounded retry и durable summary. Изборът на механизъм следва lifetime и потребителското изискване, а общият processor запазва bounded и идемпотентна обработка.

## 15. Критерии за приемане

- [ ] Service lifecycle и started/bound/foreground роли са обяснени с наблюдения.
- [ ] Manifest, channel, notification и foreground type са коректни за API 33/34+.
- [ ] Start идва от visible UI; Service има Stop, budget и timeout cleanup.
- [ ] Няма blocking main work, duplicate job или Activity/ServiceConnection leak.
- [ ] Unbind и Stop имат различни, проверени последствия.
- [ ] Worker валидира input; processor е bounded и идемпотентен.
- [ ] Самостоятелната chain има два Workers, constraint и bounded exponential retry.
- [ ] Final result е durable; migration запазва данните от Lab 2.
- [ ] Lifecycle, permissions, cancellation, restart и failure cases са проверени.
