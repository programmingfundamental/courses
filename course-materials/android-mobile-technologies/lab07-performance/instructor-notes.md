# Lab 7 — Бележки за преподавателя

Само за преподавателя; не се публикува в сайта.

## 1. Концептуална цел

Optimization е контролирана промяна с измерима цена и полза. Correctness, data fidelity и energy constraints не се заменят с нисък CPU процент. Студентът защитава три решения чрез before/after evidence.

## 2. Какво НЕ е основна цел

Не се състезавайте по абсолютен benchmark score и не настройвайте UI animation. Не изисквайте precise power данни от неподдържан телефон. Не въвеждайте нов profiling framework за сметка на анализа.

## 3. Предварителна подготовка на преподавателя

Осигурете working Lab 6 checkpoint или fake-only equivalent, diagnostic ProbeStore adapter, run guard и metrics skeleton. Подгответе еднакъв labelled 100 Hz trace и initial database fixture. Проверете CPU/Memory/System Trace capabilities на Android Studio. Physical device е нужен за radio/energy изводи; emulator е достатъчен за relative software tests. Документирайте build/API/tool mode и permission state предварително.

## 4. Разпределение на времето

| Дейност | Минути |
|---|---:|
| Проблем и хипотеза | 8 |
| Теория/metrics | 12 |
| Inefficient pipeline demo | 10 |
| Водено profiling и baseline | 35 |
| Checkpoint | 5 |
| Самостоятелни три оптимизации | 35 |
| Regression/edge tests | 15 |
| Before/after таблица | 10 |
| Анализ | 5 |
| **Общо** | **135** |

Три baseline и три optimized runs по 10+60+5 s отнемат около 7.5 min; останалото време е за trace interpretation и implementation. Heavy allocation captures са кратки отделни runs, а не включени във всяко повторение.

## 5. Как да се въведе проблемът

Ако намалим DB writes с 90%, подобрили ли сме приложението? Защо UI updates са по-малко от StateFlow emissions? Как ще разберем дали по-ниската CPU цена не идва от пропуснати motion events?

## 6. Основни точки за обяснение

Acquisition/processing/persistence/presentation rates; wall duration и drain; rows срещу transactions; allocations срещу retained memory; profiler overhead; bounded buffering и data-loss policy; energy proxies срещу measured device-level power; quality regression tests.

## 7. Чести грешки

- Измерва се само CPU, без samples/drops.
- Before е debug с logging, After — release без logging.
- Database fixture е различна между runs.
- Батчът се нарича „един write“, без дефиниция на метриката.
- StateFlow emissions се представят като actual recompositions.
- Оптимизацията sample-ва преди detector и губи events.
- Growing history list е преместена в repository, но не е ограничена.
- Stop изхвърля pending batch без отчет.
- Кратък battery percentage delta се представя като app energy измерване.

## 8. Насочващи въпроси

Коя хипотеза проверявате? Какво се е променило освен кода? Колко rows са commit-нати и колко липсват? Кой е owner на batch при Stop? Какви labels доказват, че detector е запазил качеството? Какво всъщност измерва power rail?

## 9. Очаквана архитектура

```text
fixed replay -> bounded acquisition -> processing/detection
                                        |           |
                              persistence policy   presentation policy
                                        |           |
                                      Room        StateFlow/UI
                         metrics snapshot -> report
```

Diagnostic и optimized modes използват еднакви source contracts и test fixtures. Metrics не трябва да притежават resources или да създават recursive UI updates. Hardware callbacks не правят blocking I/O.

## 10. Ключови части от примерно решение

Водената част използва предоставения InefficientProbe и показва list copying, coroutine count и insert rate. Guard-ът отменя целия run и отчита unfinished writes; не оставяйте демонстрацията да расте безкрайно.

**НЕ показвай директно на студентите преди самостоятелната задача:** възможна комбинация е single bounded persistence consumer с flush при 50 rows или 500 ms, UI projection до 10 Hz и recent UI window до 200 samples. STOP има bounded drain и explicit unfinished count; process kill не обещава flush. Други валидни техники са по-нисък location rate и stopScan след selection. Приемайте различни caps, ако student ги обоснове. Не изисквайте точно тази тройка и не давайте целия optimized pipeline.

## 11. Как да се демонстрират проблемните сценарии

Delay-нете fake store, за да запълните queue. Натиснете Stop с partial batch и проверете commit/unfinished accounting. Kill-нете process преди commit само в test experiment. Revoke permission и background-нете по време на active capture. Подайте labelled burst, който presentation sampling би пропуснал; event detector трябва да го обработи upstream. Повторете 20 lifecycle cycles и сравнете active resource counters, а не само heap chart.

## 12. Очаквани наблюдения

Batching обикновено намалява transaction overhead, но увеличава времето до durability. Presentation sampling намалява UI work, без задължително да намалява source cost. Bounded window спира retained history growth. Отделни CPU/memory стойности могат да варират между runs; търсете repeatable trends. Power rails са device-level и не приписват цялата енергия на приложението.

## 13. Проверка на самостоятелната задача

- [ ] Има три distinct техники и rationale.
- [ ] Comparison условията и repeats са еднакви.
- [ ] Измерени са quality/drops, не само resource savings.
- [ ] Queue/flush/retention policy е explicit.
- [ ] STOP и permission loss освобождават resources.
- [ ] Report различава energy измерване от proxies.

## 14. Оценяване

| Област | Точки |
|---|---:|
| Водено profiling/baseline | 20 |
| Самостоятелни оптимизации | 35 |
| Архитектура/code quality | 15 |
| Robustness/regression tests | 10 |
| Анализ/метод/измервания | 15 |
| Устна защита | 5 |
| **Общо** | **100** |

Няма точки за универсален процент ускорение. Обоснована промяна с малка полза и честно измерена цена е по-добра от голямо число с изгубени данни.

## 15. Въпроси за устна защита

1. Защо batching не е безплатен? — Памет, latency до commit и potential loss при process death.
2. Как доказвате data fidelity? — Labelled trace, counts, duplicates/drops и regression tests.
3. Защо emissions не са recompositions? — Conflation/scheduling/Compose observations имат различни semantics.
4. Кога lower CPU не значи lower energy? — Radios/display/duration и други процеси могат да доминират.
5. Какво прави сравнението възпроизводимо? — Еднакъв workload, fixture, build, device conditions, tools и repeats.
6. Коя optimization бихте отменили? — Конкретен trade-off при по-строга latency/durability/accuracy цел.

## 16. Връзка със следващото упражнение

Това е финалът на курса. Следващата проектна стъпка се избира от измерения bottleneck и отново минава през correctness/lifecycle tests. Mobile Context Monitor остава един цялостен проект, а не набор от независими API demonstrations.
