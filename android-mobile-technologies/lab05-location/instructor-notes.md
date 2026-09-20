# Lab 5 — Бележки за преподавателя

Само за преподавателя; не се публикува в сайта.

## 1. Концептуална цел

Location е uncertain measurement с permission, freshness и lifecycle constraints. Добро приложение може да откаже да изчисли distance, когато точността не го оправдава.

## 2. Какво НЕ е основна цел

Не интегрирайте map SDK, geocoding, backend или background location service. Provider API е infrastructure; основната работа е quality policy и permission state.

## 3. Предварителна подготовка на преподавателя

Проверете emulator location controls и API 33/37 permission dialogs. Подгответе synthetic route, stationary jitter, stale sample и jump fixtures. Physical phone е полезен, но fake source трябва да поддържа всички случаи. Manifest има само COARSE/FINE, без BACKGROUND_LOCATION. Подгответе empty Room migration и policy interface.

## 4. Разпределение на времето

| Дейност | Минути |
|---|---:|
| Проблем | 8 |
| Теория | 12 |
| Jitter/permission demo | 10 |
| Воден location logger | 35 |
| Checkpoint | 5 |
| Самостоятелен Mobility Tracker | 35 |
| Edge cases/tests | 15 |
| Измерване | 10 |
| Анализ | 5 |
| **Общо** | **135** |

Използвайте fake route за сигурното изпълнение в часа. Излизане навън за GPS fix не трябва да изяжда самостоятелната част.

## 5. Как да се въведе проблемът

Как може неподвижен телефон да измине 200 m? Permission grant означава ли точна позиция? Какво ще показвате, ако потребителят съзнателно избере approximate?

## 6. Основни точки за обяснение

Permission/capability/status са различни; callback е borrowed input; monotonic freshness и wall display; accuracy uncertainty; reject policy/anchor; route segments; update interval е hint; privacy-aware локален report.

## 7. Чести грешки

- Coarse grant се третира като пълна забрана.
- Permission launcher се изпълнява при всяка recomposition.
- Cached permission Boolean не се проверява отново.
- Last-known location се приема за свеж без timestamp.
- Lat/lon differences се сумират като метри.
- Rejected jump става anchor за следващата distance.
- Samples от различни boot sessions се сравняват по elapsed time.
- Location updates продължават след Home.
- Неограничена route list се пази в ViewModel.

## 8. Насочващи въпроси

Колко е стар този fix? С каква uncertainty сравнявате 5 m движение? Кой point е последният надежден anchor? Какво значи „0 m“, ако нямате достатъчна точност? Как ще спрете updates при permission race?

## 9. Очаквана архитектура

```text
permission launcher -> capability state -> session controller
                                              |
LocationSource(real/fake) -> quality policy -> Room route/contributions
                                  |
                               UiState -> Compose
```

Controller притежава subscription; repository обработва SecurityException от race между check и protected call. Pure policy се тества с injected distance/clock, без permission dialogs.

## 10. Ключови части от примерно решение

За водената част: callback copy→basic validity→bounded writer; awaitClose removeUpdates със същия listener. Permission и provider availability се проверяват преди acquisition и при resume.

**НЕ показвай директно на студентите преди самостоятелната задача:** приемлива policy първо проверява ranges/accuracy/age/time order, после оценява displacement спрямо uncertainty и maximum plausible speed. Само accepted point мести anchor; при дълъг gap се започва нов segment с нулев initial contribution. Approximate mode може да показва позиция и да disable-не fine distance accumulation с ясна причина. Point ID+transaction contribution правят retry безопасен. Thresholds се оценяват по fixtures, не по един универсален „правилен“ номер.

## 11. Как да се демонстрират проблемните сценарии

Откажете permission, разрешете approximate, после precise. Revoke през Settings по време на run и проверете cleanup при return; OS може да restart-не process при precise→approximate downgrade. Изключете Location setting. Fake trace инжектира age=120 s, duplicate timestamp и огромен jump за 1 s. Повторете Home/Resume и проверете segment boundary. Статичен jitter trace демонстрира false distance без човек да се движи.

## 12. Очаквани наблюдения

Raw distance на stationary jitter е положителна; filtering я намалява за сметка на sensitivity. Approximate mode може разумно да няма distance estimate. Requested interval не гарантира exact callbacks. Real-device accuracy и energy варират; emulator доказва workflow и policy correctness, не реален radio cost.

## 13. Проверка на самостоятелната задача

- [ ] Има обоснована quality/age/jump policy.
- [ ] Rejected sample не поврежда anchor.
- [ ] Route/total survive restart без cross-gap distance.
- [ ] Duplicate point не увеличава total втори път.
- [ ] Approximate и permission revoke имат usable state.
- [ ] Tests съдържат поне пет labelled cases.

## 14. Оценяване

| Област | Точки |
|---|---:|
| Воден logger | 25 |
| Самостоятелен tracker | 35 |
| Архитектура/code quality | 15 |
| Robustness/permissions/tests | 10 |
| Анализ/измерване | 10 |
| Устна защита | 5 |
| **Общо** | **100** |

## 15. Въпроси за устна защита

1. Какво значи accuracy? — Оценка на uncertainty, не гарантиран максимален error.
2. Защо stale fix е проблем? — Може да описва стар контекст и да изкриви текущ route.
3. Как се обработва approximate grant? — Degraded state и подходящ provider/policy, без принуда към precise.
4. Защо сегментирате route? — Няма измерени данни за gap; свързването би измислило движение.
5. Защо permission precheck не е достатъчен? — Grant може да се промени преди API call; race се обработва.
6. Какъв е trade-off на filtering? — По-малко false distance срещу пропуснато малко/реално движение.

## 16. Връзка със следващото упражнение

Lab 6 използва permission/capability state и explicit lifecycle за BLE, но добавя asynchronous protocol stages и connection recovery. Resource counters и interval trade-offs се сравняват в Lab 7.
