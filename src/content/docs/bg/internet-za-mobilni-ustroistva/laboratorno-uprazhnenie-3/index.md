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
