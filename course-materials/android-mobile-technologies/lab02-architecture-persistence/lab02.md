# Упражнение 2 — Architecture и Local Persistence

## 1. Инженерен проблем

След рестартиране Experiment Tracker изгубва приключилите експерименти. Добавянето на database calls директно към бутоните решава един случай, но създава разминаване между показаното и записаното, duplicate inserts при повторно действие и блокиране на интерфейса при по-голяма история.

**Използваме от Lab 1:** actions, ViewModel и разграничението draft/committed state. **Предаваме към Lab 3:** repository, Room schema, stable operation IDs и reactive source of truth. Room е инфраструктура на проекта, а не отделен database курс.

## 2. Учебни цели

- Разделя UI, business state и durable local facts.
- Реализира repository abstraction с измерима отговорност.
- Моделира Experiment/Measurement с referential integrity.
- Наблюдава Room чрез Flow и StateFlow без main-thread I/O.
- Използва transaction за логически атомарни промени.
- Реализира persistent filter/sort настройки чрез DataStore.
- Проверява duplicates, parent deletion и bounded history query.

## 3. Предварителни знания

Lab 1 checkpoint, suspend functions, Flow, basic relational key/foreign key понятия. Не се изисква сложен SQL или dependency injection framework.

## 4. Необходими инструменти

Android Studio, emulator, Database Inspector, Room testing, coroutine test dispatcher. Физически device не е необходим. Room/KSP и DataStore dependencies трябва да са synchronized преди часа.

## 5. Теоретична подготовка

```text
Compose -> actions -> ViewModel -> Repository -> Room
Compose <- UiState <- StateFlow <- Flow <-------+
                           ^
                     DataStore settings
```

Room е local source of truth за записаните records. ViewModel не пази втора authoritative mutable list, която трябва ръчно да се синхронизира. Repository валидира domain операции, управлява transactions и отделя storage от UI; не е просто преименуван DAO без отговорност.

Room поддържа suspend queries за еднократна работа и Flow за наблюдение. Наблюдаван query може да се преизпълнява при table invalidation, дори резултатът да не се е променил. Подбирайте bounded projection и при нужда `distinctUntilChanged()`. Вижте [Asynchronous Room queries](https://developer.android.com/training/data-storage/room/async-queries).

| State | Хранилище | Пример |
|---|---|---|
| Непотвърден text draft | saved UI state | бележка от Plan |
| Committed experiment и measurements | Room | история и timestamps |
| Filter/sort preference | DataStore | последно избрана подредба |
| Loading/error/selection validity | ViewModel | избраният запис вече липсва |

DataStore е за малки настройки, не за high-frequency measurement history. Използвайте една instance на файл за process и suspend update/edit. Room transaction и DataStore update **не са обща атомарна transaction**; след startup reconcile-вайте selection/settings с реалните records. Вижте [DataStore](https://developer.android.com/topic/libraries/architecture/datastore).

Инварианти: ID се създава веднъж за logical operation; Measurement принадлежи на съществуващ Experiment; timestamps са epoch ms; `endedAt == null` означава неприключил record, но не доказва жив hardware collector; `endedAt >= startedAt`; value е finite. След process restart неприключил experiment се показва като interrupted и иска explicit recovery action. Исторически timestamps не са clock за текущ elapsed timer.

## 6. Мини експеримент / демонстрация

Създайте experiment в in-memory версията и рестартирайте process: историята изчезва. В малка предварително подготвена Room demo версия запишете същия record, после reopen-нете process и проверете Database Inspector. Накрая извикайте save два пъти с един operation ID; предскажете ефекта при различни conflict policies.

Не измервайте „бързината на SQLite“ от един insert. Целта е да видите къде е authoritative state и как duplicate action става storage problem.

## 7. Водена практическа задача

### Стъпка 1 — schema

Създайте Room database version=1 с таблици `experiments` и `measurements`. Kotlin фрагментът за Experiment е модел, който се поставя с Room imports:

```kotlin
@Entity(tableName = "experiments")
data class ExperimentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val startedAt: Long,
    val endedAt: Long?
)
```

Measurement има `id: String`, `experimentId: String`, `timestamp: Long`, `value: Double`; foreign key към Experiment с CASCADE и index върху experimentId. За подредена история добавете подходящ compound index по experimentId/timestamp/id. Reject-вайте NaN/Infinity и стойности извън учебния диапазон `[-1_000_000, 1_000_000]` преди DAO.

### Стъпка 2 — DAO и repository

```kotlin
interface ExperimentRepository {
    fun observeRecentExperiments(): Flow<List<ExperimentEntity>>
    suspend fun start(id: String, name: String, startedAt: Long)
    suspend fun append(measurementId: String, experimentId: String,
                       timestamp: Long, value: Double)
    suspend fun finish(id: String, endedAt: Long)
    suspend fun delete(id: String)
}
```

Този lab може да ползва Entity като immutable projection; обяснете кога отделен domain model би намалил coupling. `observeRecentExperiments()` е bounded до последните 100, с deterministic order. DAO операции са suspend; Compose няма DAO reference. Не използвайте `allowMainThreadQueries()`.

Start валидира name и ID. Append проверява, че parent е отворен; след finish dataset-ът е immutable за нови measurements. Duplicate insert със същия ID и същите fields е no-op; със същия ID и различни fields е conflict. Не използвайте REPLACE за parent, защото replacement може да има нежелан ефект върху dependent rows.

### Стъпка 3 — transaction и lifecycle на запис

Реализирайте „append последна measurement + finish experiment“ като една Room transaction. Проверка за open parent и записът са в същата transaction, за да няма append след concurrent finish. UI показва success след commit; при error запазва retryable action със същия ID.

Delete премахва parent и children по explicit confirmation action. Active collector, ако има такъв в бъдеще, трябва да спре преди delete; late append се отхвърля, а не пресъздава parent. Transaction не включва бавна UI/hardware работа.

### Стъпка 4 — reactive UI

ViewModel преобразува repository Flow до immutable UiState с `stateIn`; UI събира lifecycle-aware. Покажете Loading, Empty, Content и StorageError. Selection пази само ID; при изтрит ID екранът преминава към „записът вече не съществува“. Не връщайте silently празен списък за всяка storage exception.

## 8. Checkpoint

- Записан experiment с 3 measurements се вижда след process restart.
- UI се обновява от Room Flow, без ръчно добавяне във втори списък.
- Duplicate logical insert не увеличава row count.
- Delete оставя нула children; invalid measurement се отхвърля.

## 9. Самостоятелна задача

Добавете History controls: филтър по name и status (all/open/finished), сортиране newest/oldest/name и persistent preferences. Избраният sort/status и последната search query до 60 символа се запазват в Preferences DataStore и се възстановяват при restart.

**Изисквания:** комбинацията от настройки управлява Room query; резултатът е ограничен до 100 records; UI ясно показва, че има limit и общ count. Бързо писане не трябва да оставя остарял query result след по-новия. Празният резултат е различен от storage failure.

**Ограничения:** без raw SQL от потребителски текст, без всеки record в memory, без database access от composable. Sort е enum/allowlist; name filter е bind parameter с описана literal/wildcard policy. Изберете къде да има debounce и как Flow cancellation осигурява latest query. Не се предоставя ready-made combine/query pipeline.

**Приемане:** настройките се възстановяват след restart; filter и sort действат заедно; duplicate settings emission не поражда business writes; invalid persisted enum връща безопасен default; изтрит selected ID не crash-ва; dataset от 10 000 синтетични records не се зарежда като неограничен UI list. Предайте кратка diagram на settings→query→UiState.

## 10. Edge cases

| Случай | Очакване |
|---|---|
| Празна database | explicit Empty state |
| Duplicate insert | idempotent no-op или conflict при различни fields |
| Parent delete при отворен detail | child cleanup и invalid selection state |
| 10 000 records | bounded query и responsive UI |
| Restart с endedAt=null | interrupted state, без автоматично Running |
| NaN/Infinity | validation error преди database write |
| DataStore read error/непознат enum | видима recovery policy, валиден default |
| Concurrent finish/append | transaction пази domain invariant |

## 11. Тестване

Room in-memory instrumented tests: happy path, duplicate ID, foreign key, cascade и atomic finish. Pure tests проверяват value/name validation. DataStore tests използват отделен temporary file и затварят scope между instances. Rotation и navigation не повтарят insert. Reopen на file-backed test database проверява persistence; in-memory database не доказва restart behavior. Fixture от 10 000 rows се seed-ва извън main thread, с фиксиран seed.

## 12. Наблюдение и измерване

В Database Inspector проверете tables, row counts и parent-child връзки. Съберете query emissions, UI emissions, query duration и displayed count при 0/100/10 000 rows. Отбележете thread на repository calls и main-thread stalls. Използвайте bounded counters, а не лог на всеки record. Запишете before/after restart settings и screenshot на валидния filter result в `results/lab02/`.

## 13. Въпроси за анализ

1. Защо persistent data и ViewModel state не са едно и също?
2. Каква реална отговорност добавя repository тук?
3. Защо stable ID е нужен при retry на Save?
4. Как transaction предотвратява append след finish?
5. Защо Room и DataStore не трябва да се третират като една transaction?
6. Какво правите, когато selected ID вече липсва?
7. Как query limit променя UI и измерването на голям dataset?

## 14. Очакван резултат

Mobile Context Monitor с durable experiment history, reactive repository и самостоятелно реализирани persistent filter/sort controls. Няма нова authoritative mutable list в UI. Closed experiments ще се обработват от WorkManager.

## 15. Критерии за приемане

- [ ] Experiment/Measurement имат keys, constraints и коректни timestamps.
- [ ] Compose не достъпва Room директно.
- [ ] Flow е source за UI; writes са suspend и извън main-thread blocking.
- [ ] Duplicate и concurrent finish/append са тествани.
- [ ] Filter/sort/DataStore задачата е завършена в часа.
- [ ] Query и memory са bounded при голяма история.
- [ ] Restart и invalid selection имат explicit behavior.
- [ ] Tests и Database Inspector наблюденията са предадени.
