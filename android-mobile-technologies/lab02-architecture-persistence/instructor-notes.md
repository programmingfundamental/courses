# Lab 2 — Бележки за преподавателя

Само за преподавателя; не се публикува в сайта.

## 1. Концептуална цел

Local source of truth, реактивно наблюдение и consistency на domain operations. Room е средство за доказване на тези свойства; filter/settings задачата проверява композиция на независими sources.

## 2. Какво НЕ е основна цел

Не превръщайте часа в SQL нормализация, generic repository framework или многомодулна архитектура. Няма нужда от Hilt, сложни relations или пълен CRUD интерфейс.

## 3. Предварителна подготовка на преподавателя

Room/KSP/DataStore трябва да build-ват в шаблона. Подгответе празни DAO/repository interfaces, Database Inspector, in-memory test harness и bounded 10 000-row seeder. Emulator е достатъчен; permissions, BLE и физически sensor не се използват.

## 4. Разпределение на времето

| Дейност | Минути |
|---|---:|
| Проблем | 8 |
| Теория | 12 |
| Persistence/duplicate demo | 10 |
| Водена реализация | 35 |
| Checkpoint | 5 |
| Самостоятелни settings/filter/sort | 35 |
| Edge cases и tests | 15 |
| Inspector и измерване | 10 |
| Анализ | 5 |
| **Общо** | **135** |

## 5. Как да се въведе проблемът

Кое копие на experiment е истината след restart? Какво става при double Save? Ако selected record се изтрие от друг screen, кой ще уведоми detail UI?

## 6. Основни точки за обяснение

Facts/drafts/settings са различни; Flow не е cache за произволна mutable list; transaction покрива check+write; conflict policy е business policy; saved selection се reconcile-ва с database; query cardinality е resource constraint.

## 7. Чести грешки

- DAO се извиква от composable body.
- Две mutable lists се актуализират независимо.
- Всеки retry генерира нов ID и прави duplicate record.
- REPLACE на parent не се анализира спрямо children.
- DataStore се създава по една instance на screen.
- Стар search result замества по-новия.
- Sorting зарежда всички rows в UI memory.
- Storage exception се преобразува винаги в Empty.

## 8. Насочващи въпроси

Какво остава след process kill? Кое трябва да е в една transaction? Как ще разграничите retry от нова операция? Кой layer решава да покаже Empty? Как знаете, че query за старите настройки е прекратен?

## 9. Очаквана архитектура

```text
DataStore settings -> settings Flow --+
                                     +-> ViewModel -> UiState -> Compose
Room <- Repository -> query Flow ----+
           ^
       user actions
```

Оставете student избора дали combine/flatMapLatest е във VM или специализиран query repository, ако отговорностите и tests са ясни. Singleton-ите са application-scoped storage dependencies, не глобален business state.

## 10. Ключови части от примерно решение

За водената част: в Room transaction read parent→require open→insert bounded measurement→finish. При конфликт със същия ID се сравняват immutable fields; delete разчита на explicit FK policy.

**НЕ показвай директно на студентите преди самостоятелната задача:** normalized settings Flow може да използва distinctUntilChanged, debounce само за search text и flatMapLatest към DAO queries. Sort enum избира whitelist query, а параметри се bind-ват. State има отделни loading/error/content variants. Startup default не трябва да запише автоматично върху съществуваща preference, преди първия disk read.

## 11. Как да се демонстрират проблемните сценарии

Извикайте една action два пъти със същия UUID. Изтрийте parent през repository, докато detail screen е отворен. Seed-нете 10 000 records и покажете limit=100 в query. Подайте NaN от test, а не от text field. За restart използвайте file-backed database и нов process. За concurrent append/finish синхронизирайте два coroutine callers с test barriers; резултатът трябва да пази invariant, независимо от winner.

## 12. Очаквани наблюдения

Database остава след process recreation; ephemeral loading state не остава. Query emissions може да са повече от visual changes. Bounded projection запазва UI memory ограничена; exact timings зависят от emulator/device. Непознат settings enum не трябва да crash-ва initialization.

## 13. Проверка на самостоятелната задача

- [ ] Name/status filter и sort работят едновременно.
- [ ] Preferences се възстановяват след restart.
- [ ] Query е bounded и selected ID се валидира.
- [ ] Няма stale result overwrite при бързи промени.
- [ ] Storage error е различим от Empty.

## 14. Оценяване

| Област | Точки |
|---|---:|
| Водена реализация | 25 |
| Самостоятелна задача | 35 |
| Архитектура и code quality | 15 |
| Robustness/tests | 10 |
| Анализ и измервания | 10 |
| Устна защита | 5 |
| **Общо** | **100** |

## 15. Въпроси за устна защита

1. Кое е source of truth? — Room за records, DataStore за настройки; UI е projection.
2. Защо UUID се запазва при retry? — Retry не е нова logical operation.
3. Как предотвратявате append след finish? — Check/write в transaction, не само disabled button.
4. Защо SavedStateHandle не заменя Room? — Различна durability и обем/тип данни.
5. Как Room Flow може да emit-не без реална промяна на projection? — Table invalidation преизпълнява query; distinct projection помага.
6. Каква е ползата от DataStore тук? — Реактивни durable preferences, без measurement history в него.

## 16. Връзка със следващото упражнение

Lab 3 обработва immutable measurements на приключили experiments. Stable IDs, transaction boundaries и error states правят повторното изпълнение на Worker безопасно и наблюдаемо.
