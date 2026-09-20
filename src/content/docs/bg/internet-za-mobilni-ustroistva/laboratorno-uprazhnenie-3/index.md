---
title: "Упражнение 3 — Quarkus Backend API и предвидим mobile contract"
sidebar:
  order: 3
  label: Упражнение 3
---

# Упражнение 3 — Quarkus Backend API и предвидим mobile contract

## 1. Инженерен сценарий
Mobile екран получава огромен списък и неразличими 500 responses при невалидни входове. Server entity добавя вътрешно поле, което неволно попада в payload. Трябва да построим API, чийто договор остава предвидим при бавна мрежа и отделно обновяване на mobile клиента.

## 2. Учебни цели
- Проектира request/response DTO отделно от storage.
- Реализира Quarkus REST и Bean Validation.
- Разграничава 201/400/404/409/500 semantics.
- Ограничава pagination и payload.
- Проектира filtering contract с allowlist.
- Проверява API evolution и mobile parsing assumptions.

## 3. Предварителни знания
Lab 2 result model, Java records, HTTP methods/status codes. H2 persistence plumbing е предварително даден.

## 4. Необходими инструменти
JDK 21, Maven, IDE, Quarkus REST/Jackson/Validator, curl и Quarkus tests. Android клиентът се използва за contract integration, без нов UI.

## 5. Архитектурен контекст
```text
Android → Gateway fixture (Lab 2)
Development curl → [Activity Service REST → ActivityStore → H2]
След Lab 5 mobile ще използва само Gateway.
```
Работим върху API contract, не върху database framework. ActivityStore има готов insert/find/recent, а resource methods са TODO.

## 6. Кратка теория
Request DTO допуска само fields, които клиентът има право да задава. Server ID/status/version са response fields. Bean Validation на request boundary не заменя business invariants; JSON parse failure трябва да получава четим client error. JAX-RS resource връща DTO, а не persistent object.

GET е read-only; create връща 201 и Location на новия resource. Empty collection е 200 с празен items, липсващ ID — 404. Error body има code/message/requestId и не излага stack/SQL. Мобилният client може да остане стара версия: добавяне на optional field е различно от промяна на enum semantics. [Quarkus REST JSON](https://quarkus.io/guides/rest-json/) и [Validation](https://quarkus.io/guides/validation/) описват използваните механизми.

Pagination е resource contract: default size, maximum, stable tie-breaker и next-page indicator. Offset работи за фиксиран dataset, но concurrent inserts могат да разместят pages. Query parameters се bind-ват; sort/status са allowlists. Payload size влияе на bandwidth, parsing и latency, не само на server memory.

## 7. Мини експеримент
Seed-нете 200 synthetic activities. Сравнете response bytes за 200 и 20 items. Подайте blank title и unknown ID към наивен endpoint; обсъдете защо еднакво 500 пречи на client retry policy.

## 8. Водена практическа задача — 35 минути, включително checkpoint
### Стъпка 1
В ActivitiesResource реализирайте GET /activities и GET /activities/{id} върху готовия store. Създайте response DTO с UTC timestamp mapping; recent query връща максимум 50, а API не излага java.sql или storage exceptions.

### Стъпка 2
POST /activities приема CreateActivity. Активирайте @Valid, trim policy и constraints title 1..120/userId<=40. В този lab userId=u1 е demo context; authentication се добавя по-късно. Върнете 201 + Location, без клиентът да задава status/version.

### Стъпка 3
Добавете exception mapping за malformed JSON/constraint violation/not-found към common error representation. requestId се взема от server correlation filter; тествайте HTTP status и body независимо.

### Стъпка 4
Свържете mobile decoder от Lab 2 към development endpoint само за contract test. Не добавяйте всички service URLs в production mobile configuration; временният development адрес се заменя с Gateway.

Работете в общия platform проект. От директорията starter изпълнете `node ../../platform/tools/lab.mjs 03 build`. Build проверява scaffold-а и вашите промени; наличието на 501 LAB_TODO означава незавършена учебна функционалност, а не успешен checkpoint.

## 9. Checkpoint
- Create→GET връща същата identity и DTO.
- Invalid title е client error, unknown ID е 404.
- Collection има finite limit и error response е predictable.

## 10. Самостоятелна задача — 20 минути в часа
**Problem statement:** Проектирайте GET /activities/search с pagination и status/name filtering.

**Functional requirements:** page>=0, size 1..50, status allowlist и title text до 60 chars; резултатът съдържа items и hasNext. Равни timestamps се подреждат чрез ID tie-breaker.

**Technical constraints:** Няма entity serialization, string-built SQL или unbounded fetch-all. Изберете literal/wildcard semantics за title и документирайте поведението при concurrent writes.

**Acceptance criteria:** При 53 matches и size=20 страниците са 20/20/13 и hasNext е правилно. Невалидни size/status връщат client error. Filtering и pagination действат заедно; няма duplicate ID при непроменен dataset.

Решението и кратката аргументация се проверяват в края на същото занятие. Това не е домашна работа.

## 11. Failure scenarios / Edge cases
| Сценарий | Очакване / наблюдение |
|---|---|
| Blank/oversized title | Validation error, без insert. |
| Malformed JSON / unknown enum | Съгласуван client error. |
| size=0/10000 или page<0 | Отказ преди expensive query. |
| Concurrent insert между pages | Описан offset drift; не се обещава snapshot isolation. |

## 12. Тестване
QuarkusTest/REST Assured за 201+Location, GET missing и invalid payload; store fixture от 53 records за самостоятелната задача. Изпратете повторен POST след client timeout и покажете, че този lab още няма idempotency guarantee. Проверете decoder със стар client и добавен optional response field.

## 13. Наблюдение и измерване
Response bytes, serialized item count и latency за size 10/20/50. Сравнението използва същите records и warm-up. Не представяйте по-малък payload като директно измерена battery икономия.

Запишете кратка таблица в `results/lab03/observations.md`: configuration, workload, expected/actual, sample count и ограничения. За latency използвайте p50/p95/max, когато имате достатъчно измервания, а не само средна стойност.

## 14. Въпроси за анализ
1. Защо DTO не е просто преименувана entity?
2. Как status codes влияят на mobile retry?
3. Защо size limit е API invariant?
4. Какъв tie-breaker предотвратява нестабилно подреждане?
5. Какво може да се случи между offset pages?
6. Коя response промяна е съвместима със стар client?

## 15. Очакван резултат
Activity Service с validated REST contract и student-authored bounded search endpoint, използваем от mobile result model.

## 16. Критерии за приемане
- [ ] GET list/detail и POST работят.
- [ ] DTO отделя contract от storage.
- [ ] Errors имат status/code/requestId.
- [ ] Pagination/filter endpoint покрива fixture.
- [ ] Няма unbounded query или raw SQL от input.
- [ ] Payload измервания и API evolution решение са записани.
