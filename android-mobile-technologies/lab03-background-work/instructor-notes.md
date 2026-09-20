# Lab 3 — Бележки за преподавателя

Само за преподавателя; не се публикува в сайта.

## 1. Концептуална цел

Студентът избира execution mechanism според lifetime и изискването за време. Android Service е компонент, foreground е execution status, binding е интерфейс към client, а WorkManager съхранява scheduling intent. Общият processor остава cancellable и идемпотентен при всеки owner.

## 2. Какво НЕ е основна цел

Не реализираме AIDL, cross-process IPC, media playback, background location или remote synchronization. Няма backend. Service частта е кратък локален export; тя не променя foreground-only hardware collection в Lab 4–6. Не се пишат Room schema, notification framework и Compose UI от нулата в този час.

## 3. Предварителна подготовка на преподавателя

Подгответе проекта от Lab 2 със следните scaffolds:

| Предоставено преди часа | Работата на студента |
|---|---|
| Additive migration 1→2, DAO/result uniqueness, batch processor и tests | Проследява границите и свързва processor към Service/Worker. |
| App-private export helper с temporary file/final rename и cancellable fake | Реализира job ownership, budget, terminal state и Stop. |
| Notification builder/channel/icon и explicit immutable Stop PendingIntent | Добавя manifest, foreground promotion и command handling. |
| ServiceConnection adapter и diagnostic UI с места за callback wiring | Свързва local Binder, lifecycle cleanup и read-only state. |
| WorkerFactory, dependencies и scheduling facade skeleton | Реализира Worker и самостоятелната chain policy. |
| Seed 1000/10 000 records, fake clock, interruption latch/transient fault | Провежда сравними failure tests. |

Изпълнете Gradle sync и scaffolding build преди часа. Не предоставяйте готовата independent success/failure/retry таблица. Starter тук са фрагменти за общия студентски проект, както в останалите Android упражнения.

Проверете API 33 и API 34+ emulator (основен API 37), POST_NOTIFICATIONS allow/deny и Background Task Inspector. При API 33 се използва type=0; при API 34+ — shortService. Notification permission launcher и Binding adapter са инфраструктура. Adapter трябва да освобождава регистрацията и при onStop преди получаване на Binder; не използвайте само флаг „onServiceConnected вече е извикан“.

## 4. Разпределение на времето

| Дейност | Минути |
|---|---:|
| Проблем и избор на execution owner | 8 |
| Service lifecycle, foreground правила и WorkManager | 12 |
| Coroutine / bound-only / foreground / Worker demo | 10 |
| Водена Service и Worker реализация | 35 |
| Checkpoint | 5 |
| Самостоятелна chain и избор на механизъм | 35 |
| Edge cases/tests | 15 |
| Наблюдения | 10 |
| Анализ | 5 |
| **Общо** | **135** |

Водените 35 min са 5 processor review + 10 foreground start + 7 cancellation/Stop + 5 binding + 8 Worker. На 70-тата минута започва самостоятелната задача. Предоставените migration/processor/export helpers са задължителни за този обхват; при изоставане дайте guided checkpoint, без да съкращавате самостоятелните 35 min. OS timeout demonstration използва предварителен запис или отделен debug run, не чакане в основната задача.

## 5. Как да се въведе проблемът

„Затварям екрана по време на отчет: искам ли операцията да спре, да продължи веднага или да изчака условия? Кой ще помни заявката след process death?“ Поискайте прогноза за callback sequence при Home, rotation и Stop, след което я сравнете с Logcat.

## 6. Основни точки за обяснение

Разделете три решения: кой притежава job, на кой dispatcher се изпълнява и къде е durable result. Started/foreground/bound могат да описват един instance едновременно. Unbind и stopSelf решават различни lifetime условия. Stop трябва да отменя работата независимо кога ще дойде onDestroy.

FGS е видима ограничена операция с notification, а не средство за заобикаляне на scheduling restrictions. Permission denial, недопустим start и timeout са различни състояния. Worker има retryable execution; unique enqueue не прави effects exactly-once. Input metadata, progress и final result имат различни lifetimes.

## 7. Чести грешки

- Service се смята за собствен background thread.
- CPU loop или local Binder method блокира main.
- Използва се START_STICKY за shortService или като гаранция за възстановяване.
- Unbind се бърка със Stop на started service.
- Stop само маха notification, а job продължава.
- Cleanup е единствено в onDestroy, докато Activity държи binding.
- Bind се повтаря при recomposition или pending binding остава при ранно onStop.
- Повторен Start пуска втори job или reset-ва budget.
- Notification permission denial се приема за разрешение да се пропусне notification.
- Worker retry записва summary с нов key или поглъща CancellationException.
- Measurements се поставят в Work Data, а constraints се задават само на parent без анализ.
- Force-stop се използва като синоним на обикновено background-ване.

## 8. Насочващи въпроси

Какво държи Service жив след stopSelf? Кой отменя job преди unbind? Получен ли е Binder, или само е регистриран connection? Как стар callback може да засегне нов run? Как ще възстановите final result без in-memory service state? Кои constraints имат смисъл за локална обработка?

## 9. Очаквана архитектура

```text
Activity lifecycle -> Binding adapter -> LocalBinder -> state / Cancel
       |                                     |
       +-- Start now -> ExportForegroundService + notification
       |                         |
       +-- Schedule -> unique Work chain: Prepare -> Process/Export
                                 |
                      shared bounded processor / export helper
                                 |
                        Repository / Room / app-private file
                                 |
                           durable result -> UI
```

Service/Worker получават application dependencies; processor не зависи от Android UI. Service притежава scope, Activity — binding. Export runId и measurementId/version защитават повторението. UI блокира едновременен избор на двата режима за един experiment; в тестовете проверете и uniqueness на durable effects.

## 10. Ключови части от примерно решение

Водена Service част — pseudocode за command/lifecycle границите:

```text
onCreate: create channel, scope, Binder; no export yet
onBind: return Binder; never start a job merely because UI binds
onStartCommand(STOP): terminal cleanup; return NOT_STICKY
onStartCommand(START):
    foreground promotion immediately
    validate metadata; invalid -> cleanup
    active/stopping run -> acknowledge/reject, never duplicate or extend budget
    launch one cancellable job with application deadline and run generation
    publish progress; persist final result; terminal cleanup
onTimeout / Cancel: same bounded, idempotent cleanup
onDestroy: fallback cancellation/release
```

При повторен Start бюджетът се мери от първия приет run. Terminal handler се сериализира; стар job/finally не трябва да спира по-нов run. Докато предишният job се освобождава, поддържайте Stopping и не приемайте нов run. Cleanup отменя job, маха foreground status и прекратява started state; unbind освобождава останалия client lifetime. Не чакайте дълъг export в timeout handler.

При shortService основният callback е onTimeout(startId), наличен от API 34; на API 35+ има и onTimeout(startId, fgsType). Един обработен callback е достатъчен за този тип; ако се override-нат и двата, cleanup трябва да е идемпотентен. На API 33 работи application deadline. Вижте [Service API](https://developer.android.com/reference/android/app/Service#onTimeout(int)).

Водена Worker част: query missing keys до 200 → compute → кратка transaction с uniqueness → progress → ensureActive → следващ batch. Foreign key/missing-parent policy не допуска възстановяване на изтрит input.

**НЕ показвай директно на студентите преди самостоятелната задача:** примерна policy: invalid/missing/open input→failure; empty→success; injected transient storage fault→retry при runAttemptCount < 2, иначе failure; cancellation→rethrow. Initial attempt е 0. Prepare и child използват един stable runId, summary се upsert-ва по него. Кратък непосредствен видим export може да използва ограничения FGS; отложима обработка с durable intent избира WorkManager; screen-only операция използва lifecycle coroutine. Изисквайте student аргументация, не възпроизвеждане на таблицата.

## 11. Как да се демонстрират проблемните сценарии

За Service използвайте cancellable fake с progress, за да остане време за Home/rotation/Stop. Bind без Start, след това unbind: няма export. При started+bound натиснете Stop, оставете Activity видима и проверете active jobs=0 преди унищожаването на instance. Напуснете веднага след bind и проверете cleanup на pending registration.

Натиснете Start 20 пъти, Cancel и бързо Start отново: няма overlap или stale completion. Deny POST_NOTIFICATIONS от системния dialog/Settings и сравнете UI, notification drawer и Task Manager. Недопустим background start се демонстрира с debug action без exemption; exact timing зависи от OS. Забавяне на foreground promotion се инжектира асинхронно само в дефектен debug вариант. Анализирайте ForegroundServiceStartNotAllowedException, startup deadline failure и timeout отделно. [FGS troubleshooting](https://developer.android.com/develop/background-work/services/fgs/troubleshooting).

Application deadline се съкращава чрез injected configuration. За действителния OS shortService timeout използвайте отделен run/запис без application deadline, със запазен onTimeout cleanup; не commit-вайте този режим като нормален. За process death използвайте контролиран debug kill, а force-stop тествайте отделно. Не обещавайте, че am kill ще прекрати FGS.

За Worker: latch след commit, injected failure за първите два attempts, 20 enqueue със същото unique name, Prepare failure, parent delete. TestDriver проверява constraints; stopRunningWorkWithReason симулира interruption, не реална промяна на battery. Retry се проверява с injected attempt count, без чакане за backoff. [TestDriver API](https://developer.android.com/reference/androidx/work/testing/TestDriver).

## 12. Очаквани наблюдения

Един Service instance получава множество start commands. Bound-only и started+bound имат различен край; UI reconnect не е нов export. Notification visibility и lifetime не са тъждествени. След Stop job приключва и при оставащ binding. При process death може да липсва onDestroy log; финалният резултат се сверява с durable storage.

Повторен Worker attempt запазва правилен result count. Constraint loss променя timing; WorkInfo progress е временен, а final history идва от Room. Memory е bounded от batch размера. Не се изисква точно измерена OS scheduling latency.

## 13. Проверка на самостоятелната задача

- [ ] Prepare и Process/Export са зависими requests.
- [ ] Stable runId, unique KEEP и bounded batch са коректни.
- [ ] Constraint и exponential retry с максимум 3 attempts са тествани.
- [ ] Parent failure не допуска child effect; cancellation се propagation-ва.
- [ ] Summary не се удвоява след commit/retry и се намира след restart.
- [ ] Decision tables аргументират errors и избор Service/WorkManager/coroutine.
- [ ] Самостоятелната chain не използва безкраен или sticky Service.

## 14. Оценяване

| Област | Точки |
|---|---:|
| Водена Service реализация: lifecycle, notification, binding, Stop | 20 |
| Воден Worker и processor integration | 10 |
| Самостоятелна chain и избор на механизъм | 30 |
| Архитектура/code quality | 15 |
| Robustness/tests | 15 |
| Анализ | 5 |
| Устна защита | 5 |
| **Общо** | **100** |

За Service изисквайте demonstrable Stop и липса на duplicate job; само видима notification не доказва завършена задача. За Worker изисквайте repeated-attempt correctness, не само happy path.

## 15. Въпроси за устна защита

1. Service има ли собствен worker thread? — Не автоматично; dispatcher/thread се избира за работата.
2. Може ли да е started, bound и foreground едновременно? — Да; това са различни характеристики.
3. Какво става при unbind? — Bound-only може да приключи; started операция има отделно stop условие.
4. Защо stopSelf може да не доведе веднага до onDestroy? — Client още държи binding; job трябва да бъде отменен изрично.
5. Достатъчен ли е stopForeground? — Премахва foreground status, но не заменя stop и job cancellation.
6. Защо onDestroy/START_STICKY не са durable contract? — Process death може да прекъсне cleanup; restart не гарантира възстановена операция или exactly-once effect.
7. Защо KEEP не премахва нуждата от идемпотентност? — Един scheduling intent може да има повторни attempts.
8. Какво означава отказ на notification permission? — Променя notification visibility; FGS продължава да изисква notification и останалите start условия.

## 16. Връзка със следващото упражнение

Lab 4 събира sensors само докато екранът е STARTED. Service демонстрацията не превръща hardware streams в постоянна background задача. Само вече записаните measurements могат да минат през WorkManager. Запазват се explicit owner, Stop, cancellation и bounded state.
