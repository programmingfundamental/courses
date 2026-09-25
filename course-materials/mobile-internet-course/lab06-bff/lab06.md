# Упражнение 6 — Backend for Frontend и mobile-specific aggregation

## 1. Инженерен сценарий
Home екранът прави последователно profile, activities и suggestions заявки през връзка със 150 ms RTT. UI чака множество round trips и получава излишни fields. Едно огромно агрегиращо API обаче може да забави всичко, когато optional service е бавен.

## 2. Учебни цели
- Проектира BFF screen-specific DTO.
- Разграничава aggregation от domain ownership.
- Измерва requests, bytes и latency.
- Реализира orchestration с ограничен deadline.
- Проектира partial response semantics.
- Сравнява serial и parallel fan-out trade-offs.

## 3. Предварителни знания
Lab 5 Gateway, REST Clients и common errors. Готовият BFF module и clients са настроени.

## 4. Необходими инструменти
Quarkus BFF, Android client/curl, controllable delay fixture, logs и response-byte counter. Не е необходим нов screen design.

## 5. Архитектурен контекст
```text
Android → Gateway → [Mobile BFF /mobile/home]
                              +→ User Service
                              +→ Activity Service
                              +→ optional Notification Service
```
Gateway само route-ва /api/mobile/ към BFF. BFF притежава Home projection; Activity правила остават в Activity Service.

## 6. Кратка теория
BFF събира нужните данни за определен client screen. Намалява mobile round trips, но не премахва backend calls и може да стане bottleneck. DTO трябва да има минимални нужни fields и explicit section status, а не копие на всички downstream payloads.

Critical и optional sections имат различна политика. Partial response се обозначава като partial/stale/unavailable; празен items не прикрива отказ. Parallel fan-out може да намали critical path, но увеличава concurrency и downstream load; serial calls натрупват latency. Всяка branch има timeout и общ budget; не чакайте optional резултат след deadline. [REST Client](https://quarkus.io/guides/rest-client/) и [Mutiny combining items](https://smallrye.io/smallrye-mutiny/latest/guides/combining-items/) са инструментите, а не целта.

Сравнявайте bytes за целия mobile screen workload, включително всички заявки. RTT simulation трябва да е еднаква before/after; локален тест без network delay не представя cellular автоматично. P95 от малък брой runs е rough observation, не SLA.

## 7. Мини експеримент
С готов Home fixture изпълнете две mobile requests последователно с еднакво добавено 150 ms network delay. После един агрегиращ request. Пребройте backend calls отделно; покажете, че те не са изчезнали.

## 8. Водена практическа задача — 35 минути, включително checkpoint
### Стъпка 1
Дефинирайте HomeResponse с profile summary и последни максимум 5 activities. Използвайте DTO projection, generatedAt и section availability. Изискването за profile като critical и activities като explicit partial се фиксира във водената версия.

### Стъпка 2
В HomeResource реализирайте GET /mobile/home чрез UsersClient и ActivitiesClient. Задайте общ budget <=2.5 s и downstream deadlines. Response objects се затварят. Започнете с проста serial реализация; parallel вариант е допустим при същите bounds.

### Стъпка 3
Добавете Gateway /api/mobile/ route с rewrite /mobile/ и timeout=4 s. Android вече заявява един Home endpoint. UI показва partial section с причина, без да изтрива последните валидни данни.

### Стъпка 4
Изпълнете еднакви before/after сценарии, поне 20 runs след warm-up. Запишете mobile requests, bytes, backend fan-out count и p50/p95/max. Разграничете cold start/cache от steady state.

Работете в общия platform проект. От директорията starter изпълнете `node ../../platform/tools/lab.mjs 06 build`. Build проверява scaffold-а и вашите промени; наличието на 501 LAB_TODO означава незавършена учебна функционалност, а не успешен checkpoint.

## 9. Checkpoint
- Home агрегира поне две services.
- Gateway не съдържа aggregation.
- Един mobile request дава bounded Home DTO и explicit partial state.

## 10. Самостоятелна задача — 20 минути в часа
**Problem statement:** Проектирайте BFF endpoint за различен екран: Activity Overview с owner summary и notification eligibility.

**Functional requirements:** Изберете минимум две sources, собствен DTO и кои sections са critical/optional. При един failure върнете доказуемо правилна partial response или error според избрания contract.

**Technical constraints:** Един endpoint, до 10 activities, без нов service/database/UI module. Използвайте готовите clients и timeout infrastructure; архитектурното решение е projection и failure semantics.

**Acceptance criteria:** Happy path и един partial failure са проверени. Payload не е concatenation на цели service responses. Студентът обяснява запазените/изхвърлени fields и общия time budget.

Решението и кратката аргументация се проверяват в края на същото занятие. Това не е домашна работа.

## 11. Failure scenarios / Edge cases
| Сценарий | Очакване / наблюдение |
|---|---|
| Optional section timeout | Partial status, без success(empty) маскиране. |
| Critical profile недостъпен | Explicit service error според contract. |
| Downstream връща твърде голям list | Bounded projection или upstream paging. |
| Mobile отменя request | Не се стартира безконтролна допълнителна работа. |

## 12. Тестване
Happy Home съдържа profile и activity summary; stop optional service; downstream malformed JSON; critical service timeout. Проверете, че response не включва чувствителни/излишни fields. Contract tests за partial flags и max items; network test със същия delay before/after.

## 13. Наблюдение и измерване
Общ mobile request count и bytes за екран, backend call count, end-to-end duration и per-branch time. Отбележете total deadline и partial result rate. Не сумирайте parallel spans като wall-clock latency.

Запишете кратка таблица в `results/lab06/observations.md`: configuration, workload, expected/actual, sample count и ограничения. За latency използвайте p50/p95/max, когато имате достатъчно измервания, а не само средна стойност.

## 14. Въпроси за анализ
1. Защо BFF е различен от Gateway?
2. Кога един request все пак е по-бавен?
3. Кои данни BFF не трябва да притежава?
4. Как UI различава Empty от Unavailable?
5. Как parallel fan-out променя load?
6. Кога general API е по-подходящ от BFF?

## 15. Очакван резултат
Измерен Home aggregation и студентски втори screen contract с аргументиран partial failure.

## 16. Критерии за приемане
- [ ] Home използва минимум две services.
- [ ] DTO е bounded и screen-specific.
- [ ] Partial failure е explicit.
- [ ] Mobile има един entry point.
- [ ] Before/after отчита requests/bytes/latency.
- [ ] Independent endpoint и failure test са готови.
