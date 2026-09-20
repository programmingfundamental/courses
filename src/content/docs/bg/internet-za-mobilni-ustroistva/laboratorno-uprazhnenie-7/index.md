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

## 7. Мини експеримент
GET /unstable?mode=mixed следва deterministic цикъл: 6 successes, 2×503, 2×3 s delay. Пуснете 10 calls с изключени retries. После демонстрирайте 3 retries в client×3 в BFF като максимум 9 downstream attempts, без да генерирате масов load.

## 8. Водена практическа задача — 35 минути, включително checkpoint
### Стъпка 1
Използвайте DownstreamPolicy CDI bean между HomeResource и UnstableClient. Map-нете 503/timeout към TransientDependencyFailure, а contract/validation error към terminal category. Затваряйте Response при всяка branch.

### Стъпка 2
Добавете timeout около 500 ms и максимум един допълнителен attempt само за idempotent optional read. Backoff е ограничен; измерете, че branch budget остава <=1.5 s, а Home<=2.5 s. Не retry-вайте completed successful branch.

### Стъпка 3
Добавете circuit breaker с малък учебен rolling window и explicit recovery delay; документирайте parameters. Fallback връща section unavailable със reason, не invented recommendation. Примерният annotation механизъм е @Timeout/@Retry/@CircuitBreaker върху отделен injected bean.

### Стъпка 4
Пуснете ok→fail→ok fixture и наблюдавайте closed/open/half-open recovery. Отделете measured requests от retry attempts и отбележете client cancellation. Не добавяйте retry и в Gateway.

Работете в общия platform проект. От директорията starter изпълнете `node ../../platform/tools/lab.mjs 07 build`. Build проверява scaffold-а и вашите промени; наличието на 501 LAB_TODO означава незавършена учебна функционалност, а не успешен checkpoint.

## 9. Checkpoint
- Бавната optional dependency не държи Home безкрайно.
- Fallback е обозначен и attempts са ограничени.
- Breaker recovery може да се наблюдава след възстановяване.

## 10. Самостоятелна задача — 20 минути в часа
**Problem statement:** Изберете policy за нов случай: downstream връща постоянен 422 за невалиден вход, докато 10 паралелни валидни reads понякога timeout-ват.

**Functional requirements:** Разграничете permanent input от transient capacity failure. Изберете timeout/retry/breaker/fallback и по желание bounded bulkhead; представете кратка таблица защо всеки механизъм е приложен или пропуснат.

**Technical constraints:** Работете само в policy bean; не променяйте API contract/базата. Максимум 2 attempts за подходящ read, finite concurrency/queue ако използвате bulkhead. Няма retry на mutation.

**Acceptance criteria:** 422 не се повторя; timeout приключва в budget; няма unbounded queue; partial Home показва причината. Студентът доказва downstream attempt count и обяснява един trade-off.

Решението и кратката аргументация се проверяват в края на същото занятие. Това не е домашна работа.

## 11. Failure scenarios / Edge cases
| Сценарий | Очакване / наблюдение |
|---|---|
| Permanent 422 | Terminal, без retry storm. |
| 3 s delay | Bounded response, remote work може още да тече. |
| Поредица 503 | Breaker opens по зададената policy. |
| Recovery след outage | Пробен call и връщане към normal behavior. |
| Concurrent callers | Bounded load и видими rejections. |

## 12. Тестване
Policy unit tests с fake transport/time; happy и transient fail→success; permanent error; caller cancellation. Integration с fixture modes проверява elapsed/attempt count и fallback labeling. Breaker tests изчакват configured window или използват controllable clock/harness, без hardcoded platform timing assertions.

## 13. Наблюдение и измерване
Logical request count, downstream attempts, amplification ratio, timeout/fallback/breaker counters и p50/p95/max. Запишете concurrency и queue cap. Отбележете, че бърз fallback може да намали latency за сметка на completeness.

Запишете кратка таблица в `results/lab07/observations.md`: configuration, workload, expected/actual, sample count и ограничения. За latency използвайте p50/p95/max, когато имате достатъчно измервания, а не само средна стойност.

## 14. Въпроси за анализ
1. Защо timeout и circuit breaker решават различни проблеми?
2. Кой layer притежава retry budget?
3. Защо fallback може да бъде грешен success?
4. Как се умножават retries?
5. Кога bulkhead е по-полезен от retry?
6. Може ли timeout да отмени remote side effect?

## 15. Очакван резултат
BFF с измерима optional-dependency policy и студентски вариант за permanent/capacity failure.

## 16. Критерии за приемане
- [ ] Има controllable unstable service.
- [ ] Timeout/retry/breaker са обосновани.
- [ ] Общият budget е проверен.
- [ ] Fallback не маскира липсващи данни.
- [ ] 422 и mutation не се retry-ват механично.
- [ ] Има attempts/concurrency evidence.
