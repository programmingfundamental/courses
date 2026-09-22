---
title: "Упражнение 7 — Resilience и Fault Tolerance"
sidebar:
  order: 7
  label: Упражнение 7
---

# Упражнение 7 — Resilience и Fault Tolerance

## 1. Инженерен сценарий
Home BFF чака optional recommendation. Downstream редува успех, 503 и забавяне; няколко слоя започват retry и малък outage се превръща в множество заявки. Трябва да запазим полезния mobile screen с ограничена цена и ясно обозначени липсващи данни.

## 2. Учебни цели
- Разграничава timeout, retry, breaker и fallback.
- Проектира ограничен общ time budget.
- Реализира Quarkus fault tolerance върху CDI boundary.
- Измерва retry amplification.
- Избира policy според failure semantics.
- Проверява recovery и partial response correctness.

## 3. Предварителни знания
Lab 6 aggregation; exceptions, CDI и downstream HTTP semantics. Не се строи unstable service от нулата.

## 4. Необходими инструменти
Готов Notification /unstable fixture, DownstreamPolicy/UnstableClient, Quarkus SmallRye Fault Tolerance, curl и counters. Fixture modes са ok/fail/slow/mixed.

## 5. Архитектурен контекст
```text
Android → Gateway → [BFF policy]
                      +→ Fast Activity Service
                      +→ Notification /unstable
```
Устойчивостта е на границата към optional read dependency. Gateway не retry-ва, а mobile retry се изключва за измерването, за да има един owner.

## 6. Кратка теория
Timeout ограничава чакане; retry повтаря подходяща операция; circuit breaker временно отказва нови calls след достатъчно failures; fallback връща друг, честно обозначен резултат. Bulkhead ограничава concurrency/queue, не поправя upstream. Retry storm се получава от комбинирани layers и синхронни backoffs.

Quarkus FT annotations действат върху CDI invocation. Wrapper трябва да превърне само retryable downstream errors в избрания exception type; не поставяйте @Retry върху произволен POST или catch-all. HTTP client timeout остава нужен: annotation timeout не гарантира, че remote server е прекратил работата. [SmallRye Fault Tolerance](https://quarkus.io/guides/smallrye-fault-tolerance/) описва execution semantics.

Budget включва attempts, per-attempt timeout, backoff и orchestration. Fallback не представя stale/empty данни като live success. Circuit state е локален за instance; малка лабораторна извадка не описва distributed cluster availability. Измервайте attempt count и unavailable sections, не само success status.
