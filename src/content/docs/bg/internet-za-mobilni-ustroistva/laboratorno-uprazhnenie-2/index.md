---
title: "Упражнение 2 — Resilient Mobile API Client"
sidebar:
  order: 2
  label: Упражнение 2
---

# Упражнение 2 — Resilient Mobile API Client

## 1. Инженерен сценарий
Списъкът с activities остава на spinner при бавна мрежа. Retry бутонът повтаря POST след timeout и понякога създава два records. Един и същ надпис „няма Internet“ се показва при DNS failure, HTTP 403 и счупен JSON. Нужен е предвидим client contract.

## 2. Учебни цели
- Класифицира transport, HTTP и parsing failures.
- Реализира cancellable HTTP call с bounded timeouts.
- Разделя request status от Connectivity state.
- Проектира retry само за подходящи операции.
- Измерва logical requests и attempts отделно.
- Проверява cancellation и uncertain POST outcome.

## 3. Предварителни знания
Lab 1, HTTP semantics, Kotlin suspend/cancellation. Не се пише backend в този час.

## 4. Необходими инструменти
Готовият Android проект с OkHttp/serialization; работещ Compose environment и fixture API; curl/Logcat. Mock responses могат да заменят network за unit tests.

## 5. Архитектурен контекст
```text
[Android ApiClient → Repository → ViewModel]
       |
Gateway /api/fixture/activities → готов fixture BFF
```
Fixture е предварително даден API, отделен от Activity Service, който ще създадем в Lab 3. Android знае само един base URL.

## 6. Кратка теория
HTTP response и IOException са различни категории. DNS/connection/TLS failure са transport problems; 4xx е contract/auth/request problem, 5xx — server failure; valid 200 с malformed JSON е decode failure. Timeout означава неизвестен outcome за mutation, не доказва rollback.

OkHttp има connect/read/call deadlines; call timeout обхваща целия call. Starter изключва implicit connection retry, за да измерваме опитите ясно. Suspend bridge трябва да свърже coroutine cancellation с Call.cancel и винаги да затваря response body. Не правете blocking execute на main thread. CancellationException се разпространява, а не се показва като service error. [OkHttp calls](https://square.github.io/okhttp/features/calls/) описва call lifecycle.

Retry има max attempts и общ deadline. GET обикновено е safe за повторение; POST не става idempotent чрез HTTP timeout setting. Backoff с jitter намалява синхронните опити; Retry-After се уважава в рамките на budget. Не retry-вайте authentication, validation, TLS verification или malformed contract без основание.
