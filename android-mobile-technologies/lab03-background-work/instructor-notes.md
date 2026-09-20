# Lab 3 — Бележки за преподавателя

Само за преподавателя; не се публикува в сайта.

## 1. Концептуална цел

Durable intent не означава uninterrupted execution или exactly-once effect. Основното е повторяема работа с bounded state и правилни transaction/cancellation граници.

## 2. Какво НЕ е основна цел

Не разглеждайте всички scheduler APIs, foreground services или remote synchronization. Няма network/backend. UI за progress е минимален; semantics са по-важни от progress animation.

## 3. Предварителна подготовка на преподавателя

WorkManager/testing dependencies, WorkerFactory wiring и Inspector трябва да са готови. Подгответе closed experiment с 1000/10 000 records, fake transient exception и interruption latch. Emulator е достатъчен; permissions/hardware не са необходими. Prepare/Export interfaces са празни, без готова chain policy.

## 4. Разпределение на времето

| Дейност | Минути |
|---|---:|
| Проблем | 8 |
| Теория | 12 |
| Coroutine/Worker demo | 10 |
| Водена обработка | 35 |
| Checkpoint | 5 |
| Самостоятелна chain | 35 |
| Edge cases/tests | 15 |
| Наблюдения | 10 |
| Анализ | 5 |
| **Общо** | **135** |

Retry policy се проверява без чакане чрез отделни Worker tests с подаден runAttemptCount и injected failure; backoff configuration се проверява отделно. TestDriver не е универсален контролер на часовника. Реалният emulator run е отделно наблюдение, а не точен stopwatch тест.

## 5. Как да се въведе проблемът

Кой ще довърши обработката, когато screen изчезне? Ако process умре след commit, какво ще направи следващият attempt? Достатъчно ли е „enqueue само веднъж“?

## 6. Основни точки за обяснение

Lifecycle scope срещу persistent scheduling; unique work срещу idempotency; cancellation е cooperative; input metadata е малка; constraints се прилагат към individual requests; progress не е durable result; database keys защитават repeated attempts.

## 7. Чести грешки

- Worker чете ViewModel или Activity state.
- Measurements се поставят в Work Data.
- Success се връща преди commit на results.
- Всеки retry append-ва нов summary с нов ID.
- Catch(Exception) поглъща cancellation.
- Constraint е зададен само на Prepare без анализ на child.
- PeriodicWork се използва като секунден timer.
- Няма max attempts за transient failure.
- Force-stop се представя като ordinary background execution.

## 8. Насочващи въпроси

Кое остава durable след crash? Как ще откриете вече обработен row? Кой key е stable между attempts? Какво вижда UI, когато няма running WorkInfo, но summary съществува? Кои constraints са смислени за изцяло локална обработка?

## 9. Очаквана архитектура

```text
UI -> Scheduler facade -> unique Work chain
                          Prepare -> Process/Export
                             |             |
                       Repository / Room transactions
                             |
                         durable results -> UI Flow
```

WorkerFactory получава application-scoped dependencies. Processor няма UI dependency; summary е keyed по runId, row results — по measurementId/version. Completed experiment е immutable, което опростява repeatable input.

## 10. Ключови части от примерно решение

Водена част: query up to 200 missing result keys→compute→transaction insert with uniqueness→publish progress→ensureActive→следващ batch. Delete на input се класифицира изрично и не води до resurrection.

**НЕ показвай директно на студентите преди самостоятелната задача:** подходяща policy е invalid/missing/open experiment→failure; no records→success; test-injected temporary storage fault→retry, ако `runAttemptCount < 2`, иначе failure; cancellation→rethrow. Initial runAttemptCount=0, така че условието допуска най-много три executions. Prepare и child share stable runId чрез metadata; durable summary се upsert-ва по същия ID. Не налагайте тази класификация без student аргументация.

## 11. Как да се демонстрират проблемните сценарии

Latch след първия transaction commit позволява cancellation точно там. TestDriver.setAllConstraintsMet допуска чакащия request; за interruption използвайте stopRunningWorkWithReason от work-testing 2.11.0 с подходящ constraint stop reason. Това симулира спиране, а не променя реалния battery/storage state; реална constraint промяна се наблюдава отделно на emulator. Вижте [TestDriver API](https://developer.android.com/reference/androidx/work/testing/TestDriver). Injected exception отказва първите два attempts, после допуска success. Enqueue-нете същото unique name 20 пъти. Force failure на Prepare и проверете, че child няма side effects. Rotation не трябва да schedule-ва работа. За restart първо запишете WorkRequest ID, background-нете, kill-нете process по system-like процедура и след relaunch проверете Room/WorkInfo; не обещавайте незабавен execution.

## 12. Очаквани наблюдения

Повторните attempts могат да се случат, но result count остава правилен. Constraint loss отлага/спира работа и може да промени timing. Batch memory е приблизително постоянна спрямо dataset size. WorkInfo progress се използва докато work върви, а final history идва от Room.

## 13. Проверка на самостоятелната задача

- [ ] Prepare и Process/Export са отделни зависими requests.
- [ ] Stable runId и unique KEEP policy са коректни.
- [ ] Constraint и bounded exponential retry са тествани.
- [ ] Parent failure не допуска child effect.
- [ ] Decision table и cancellation policy са авторско решение.
- [ ] Summary и aggregate не изискват unbounded input list.

## 14. Оценяване

| Област | Точки |
|---|---:|
| Водена реализация | 25 |
| Самостоятелна chain | 35 |
| Архитектура/code quality | 15 |
| Robustness/tests | 15 |
| Анализ | 5 |
| Устна защита | 5 |
| **Общо** | **100** |

## 15. Въпроси за устна защита

1. Защо WorkManager не гарантира exactly-once effect? — Task може да се повтори след interruption; durable idempotency е application задача.
2. Защо KEEP не е достатъчен? — Ограничаването на enqueue не предотвратява повторен execution attempt.
3. Какво означава retry? — Transient failure с scheduler-controlled delay и finite budget.
4. Какво правите с CancellationException? — Оставяме cooperative cancellation да се разпространи.
5. Къде пазите final result? — В Room с stable key, не само в временен progress.
6. Защо не Worker за accelerometer stream? — Continuous foreground resource lifetime и sampling не съвпадат с deferrable bounded work.

## 16. Връзка със следващото упражнение

Lab 4 събира foreground sensor data. Само вече записаните records могат по-късно да минат през background processing; subscriptions и WorkManager имат различни owners и lifetimes.
