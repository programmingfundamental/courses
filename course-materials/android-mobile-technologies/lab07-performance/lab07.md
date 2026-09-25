# Упражнение 7 — Performance, Energy и Robustness

## 1. Инженерен проблем

Mobile Context Monitor показва правилни стойности, но след няколко минути има повече allocations, database writes и UI updates, отколкото потребителят може да използва. Location и BLE могат да останат активни без реална нужда. „Работи“ не означава, че приложението използва разумно ресурсите или че оптимизацията запазва качеството на измерванията.

**Използваме:** всички pipeline-и от Lab 1–6 и техните fake traces/counters. **Краен резултат:** baseline/optimized сравнение, поне три самостоятелни оптимизации и техническа аргументация за trade-offs. Не се създава нов отделен проект.

## 2. Учебни цели

- Формулира проверима performance/resource хипотеза.
- Профилира CPU, memory, allocations и main-thread activity.
- Измерва DB operations, UI updates и acquisition/processing rates отделно.
- Реализира поне три оптимизации със запазени domain invariants.
- Разграничава energy measurement от software proxy metrics.
- Проверява lifecycle, permissions и overload след оптимизация.
- Аргументира before/after резултат с repeatability и ограничения.

## 3. Предварителни знания

Работещи sensors/location/BLE adapters или техните fakes, Room, WorkManager, Flow cancellation и basics на profiling. Преди часа regression suite от Lab 1–6 трябва да е наличен.

## 4. Необходими инструменти

Android Studio CPU/Memory Profiler, System Trace/Perfetto, Layout Inspector за Compose observations, Database Inspector и fake replay. **Физически device е силно препоръчителен**; за изводи за реална radio/energy цена е необходим. Emulator позволява software comparison, но неговата battery simulation не измерва реален разход. Запазете същия build variant/device/tooling за before/after.

## 5. Теоретична подготовка

```text
acquisition rate -> processing rate -> persistence rate -> presentation rate
      |                    |                 |                  |
  sensor/radio       CPU + queue         DB/I/O            Compose/UI
```

Тези rates не трябва да са равни. Sampling намалява downstream events, filtering променя signal, batching намалява transactions, а bounded buffers ограничават memory за сметка на explicit overflow behavior. По-малък DB operations count може да означава batching или изгубени данни — измерете и row count/quality.

Recomposition count не е frame count, а StateFlow emission не обещава отделна recomposition за всяка стойност. Slow collector може да пропуска междинни states по conflation. Измервайте emitted, observed и committed UI updates отделно, без metrics counter сам да предизвиква feedback loop. Profiling добавя overhead; allocation recording и debug Inspector се използват за диагностика, а timing comparison — при еднакъв profileable/release-like setup. Вижте [Android profiling](https://developer.android.com/studio/profile) и [System tracing](https://developer.android.com/topic/performance/tracing).

Energy е интеграл на power по време, не синоним на CPU %. Power Profiler/ODPM на поддържани устройства показва device-level rails, включващи и други процеси. За неподдържани устройства запишете N/A за energy и използвайте обозначени proxies: active sensor time, location requests, BLE scan duty time, DB transactions и background work. Не превръщайте 1% battery change от кратък run в прецизен app energy result. Вижте [Power Profiler](https://developer.android.com/studio/profile/power-profiler).

## 6. Мини експеримент / демонстрация — 10 минути

Пуснете fake accelerometer 100 Hz за 30 s с presentation на всяка стойност и DB insert на всяка стойност. Сравнете acquired samples, committed rows, state emissions, наблюдавани UI updates и actual recompositions. Покажете memory/allocations trend при постепенно нарастваща history list.

Запишете хипотеза: кой разход доминира и коя промяна би го намалила? Преди optimization фиксирайте workload seed, duration, database initial state и build mode.

## 7. Водена практическа задача — 35 минути

### Стъпка 1 — предоставен неефективен diagnostic компонент

Добавете следния Kotlin fragment в debug-only package, с coroutines/flow imports и adapter към съществуващия repository. Той е **умишлено неефективен материал за диагностика**, не production implementation. Не променяйте самостоятелната optimized версия още.

```kotlin
data class ProbeSample(val id: Long, val value: Float)
interface ProbeStore { suspend fun insertOne(sample: ProbeSample) }

class InefficientProbe(
    private val scope: CoroutineScope,
    private val store: ProbeStore
) {
    private val mutableHistory = MutableStateFlow<List<ProbeSample>>(emptyList())
    val history: StateFlow<List<ProbeSample>> = mutableHistory.asStateFlow()

    fun onSample(sample: ProbeSample) {
        scope.launch {
            store.insertOne(sample)
            mutableHistory.update { previous -> previous + sample }
        }
    }
}
```

Тук има coroutine/insert за всеки sample и copy на все по-голям list. Caller създава отделен run scope и го cancel-ва при край; **външният harness** спира input след 6000 samples или 60 s, което настъпи първо, и ограничава drain до 5 s. След това компонентът се освобождава. Отчетете incomplete/cancelled writes. Без такъв guard моделът на растеж е неприемлив.

ProbeStore mapping използва отделен test experiment и stable sample IDs; не смесвайте с лични/production measurements. Room insert е suspend/off main. UI показва latest sample и history size; не рисува хиляди widgets само за да натовари renderer.

### Стъпка 2 — resource scenarios

Конфигурирайте diagnostic mode с три controllable сценария:

| Сценарий | Неефективно поведение | Guard на демонстрацията |
|---|---|---|
| Sensor/storage/UI | 100 Hz → insert/emission на всяка стойност; growing list | <=6000 samples, <=60 s |
| Location | request interval 1 s, дори когато task се нуждае от 5–10 s | само foreground, explicit Stop |
| BLE | scan активен през целия diagnostic window, въпреки избран peripheral | <=60 s, stopScan в finally/cleanup |

Не премахвайте permission checks или lifecycle cleanup, за да получите „лоши“ резултати. За leak демонстрация използвайте fake resource и counter, след което затворете run. При real radio спазвайте platform limits и запишете откази; scan failure не е доказателство за нулев разход.

### Стъпка 3 — instrumentation

Counters: source samples, processed samples, drops, rows attempted/committed, database transaction count, UI state emissions, observed UI updates, active subscriptions, BLE scan ms, location update count и active Worker count. Counters използват thread-safe state и се snapshot-ват на 1 s; не log-вайте всеки callback.

Room batch от 100 rows е една transaction, но не означава непременно една SQL statement; отчитайте rows и transactions отделно. Добавете trace sections около filter/process/persist за интерпретация. Memory данните разграничават Java heap, native/process memory и retained objects.

### Стъпка 4 — baseline

Warm-up 10 s, measurement 60 s, bounded drain 5 s. Изпълнете три повторения със същия seeded trace и initial database state; между тях reset-вайте само test experiment и metric counters. Отделете diagnostic allocation recording от timing runs. Запишете device/API, build type, charging/brightness, thermal condition и profiler mode. Ако run достигне guard/cap, той се отчита, а не се представя като пълно обработване.

Warm-up използва отделна кратка session. Преди measurement затворете нейния scope, изчистете warm-up records и създайте нов probe с нулеви counters; лимитът 6000 samples/60 s се отнася само за measurement window. Подготовката и cleanup между runs не участват в timing metrics.

## 8. Checkpoint — 5 минути

- Inefficient mode е възпроизводим и автоматично спира в budget.
- Има baseline table от actual measurements, а не очаквани числа.
- DB rows/transactions, state emissions/UI updates и samples/drops са разграничени.
- Студентът посочва поне два bottleneck candidates и доказателство от trace/counters.

## 9. Самостоятелна задача — 35 минути, в часа

Създайте optimized pipeline с **поне три различни техники**, избрани въз основа на baseline: bounded buffers, presentation sampling, batching, bounded UI window, по-рядка location acquisition, прекратяване на ненужен BLE scan, lifecycle-aware source management или намаляване на ненужни allocations.

**Функционални изисквания:** последните readings и значимите motion/BLE events остават видими; persistent records имат explicit retention/flush policy; STOP освобождава resources; data loss/drop е измерен. Optimized pipeline работи със същия trace и алгоритмичните tests от предходните упражнения.

**Технически ограничения:** всяка queue/list има cap; за batching дефинирайте max size и max delay, поведение при Stop/process death и durable commit boundary. Не разреждайте input преди detector без да измерите missed events. Не намалявайте throughput чрез мълчаливо отхвърляне и не скривайте errors от counters.

**Приемане:** три техники са реализирани и обяснени; before/after runs имат еднакви условия и три repeats; таблицата съдържа ресурсни и quality metrics; regression tests минават; няма resources след STOP. Няма минимален задължителен процент ускорение. Предайте свои решения за буфери/flush/quality, без готов optimized code от преподавателя.

## 10. Edge cases

| Случай | Какво проверявате |
|---|---|
| Slow database | queue cap, drops/rejection и UI responsiveness |
| Stop с partial batch | documented bounded flush или отчетена загуба |
| Process death преди commit | durable/lost distinction, без fictitious success |
| Permission revoke при active source | cleanup и recovery UI |
| Missing sensor/BLE/API support | fake/Unsupported behavior |
| High-frequency burst | bounded memory и detector quality |
| Repeated navigation/rotation | няма растящ subscription count |
| Worker и foreground pipeline едновременно | resource contention се наблюдава, не се игнорира |

## 11. Тестване

Повторете lifecycle, permissions и protocol/filter tests от Lab 1–6. Fake trace има предварително labelled events и known sample count; сравнете lost/duplicated records и detector recall преди/след. Test consumer delay причинява queue saturation. Stop/drain test проверява конкретен timeout, без assertion „всичко е записано“ при process kill. За resource test изпълнете 20 Start/Stop cycles и проверете listeners/location/GATT/jobs=0; memory snapshot сам по себе си не доказва липса на leak.

## 12. Наблюдение и измерване — 10 минути

Попълнете таблицата с median от три runs и range; пазете отделните raw values. Rate denominator е measurement duration, а drain writes се отчитат отделно. N/A е допустимо за неподдържана метрика.

| Метрика | Before | After | Метод / ограничение |
|---|---|---|---|
| CPU / CPU time | | | същият profiler/build |
| Peak heap / retained objects | | | еднакви checkpoints |
| Allocations за measurement window | | | отделен diagnostic run |
| DB rows/min | | | committed, без drain |
| DB transactions/min | | | repository counter |
| State emissions / observed UI updates/s | | | отделни counters |
| Samples acquired / processed / dropped | | | source/pipeline counters |
| Event detection: true/false/missed | | | labelled trace |
| Location delivered fixes / active ms | | | provider/source counters |
| BLE scan ms / active GATT | | | lifecycle counters |
| Energy или обозначени proxies | | | device/tool support |

Добавете една графика memory/time или DB transactions/time и кратък report `results/lab07/report.md`: хипотеза, метод, три промени, данни, correctness checks, trade-offs, ограничения. Изрично посочете дали energy conclusion е измерена, само proxy-based или недостъпна. Emulator резултат не се обобщава като реален battery life improvement.

## 13. Въпроси за анализ

1. Защо по-малко DB transactions не доказват запазени данни?
2. Кога sampling подобрява UI, но поврежда detection?
3. Какво се губи при batch преди durable commit?
4. Защо debug recomposition count не е обща performance оценка?
5. Как profiling overhead влияе на сравнението?
6. Кои metrics са енергийни proxies и кои са real measurements?
7. Каква optimization бихте отменили при по-строго изискване за data fidelity?
8. Как проверихте, че performance промяната не е lifecycle leak?

## 14. Очакван резултат

Mobile Context Monitor има контролиран diagnostic baseline, optimized режим с поне три student-authored техники и сравнителен report с quality/resource данни. Изборът е аргументиран спрямо конкретния workload и hardware.

## 15. Критерии за приемане

- [ ] Неефективният режим е измерен и има външен run guard.
- [ ] CPU/memory/allocations и application counters са разграничени.
- [ ] Самостоятелно са реализирани поне три оптимизации.
- [ ] Queues/lists, flush и overflow имат explicit policies.
- [ ] Before/after използват еднакъв workload и три повторения.
- [ ] Quality, drops и durability се отчитат заедно с performance.
- [ ] Lifecycle/permission regressions са проверени.
- [ ] Таблица, графика и кратък report с ограничения са предадени.
