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

## 7. Мини експеримент
Изпратете fixture mode=ok, client, server, slow и malformed с curl. Сравнете transport success с application failure. Стартирайте slow request, навигирайте назад и наблюдавайте дали HTTP call остава активен.

## 8. Водена практическа задача — 35 минути, включително checkpoint
### Стъпка 1
Разширете ApiResult с Success, TransportFailure(Dns/Connect/Tls), Timeout, HttpFailure(status,code), DecodeFailure; request cancellation остава control flow. UI има distinct retryable/readable error states, а не Throwable.toString.

### Стъпка 2
Реализирайте GET със starter transport: connect=2 s, read=3 s, call=5 s. Вържете cancellation към Call.cancel, затворете body в use/finally и ограничете response до 256 KiB. Validation на JSON schema е отделна стъпка.

### Стъпка 3
Използвайте serializer за fixture items. Loading→Content/Error се управлява от ViewModel; нов refresh отменя предишния и stale response не презаписва актуалния. Не блокирайте request само по VALIDATED=false — дайте explicit user retry.

### Стъпка 4
Покажете attempt count, elapsed и status category; изчиствайте activeCall counter при всички terminal paths. Успехът на fixture не е реализация на Activity Service.

Работете в общия platform проект. От директорията starter изпълнете `node ../../platform/tools/lab.mjs 02 build`. Build проверява scaffold-а и вашите промени; наличието на 501 LAB_TODO означава незавършена учебна функционалност, а не успешен checkpoint.

## 9. Checkpoint
- Петте fixture режима водят до различими outcomes.
- Cancel/Back отменя Call; няма висящ spinner.
- Compose не изпълнява blocking network работа.

## 10. Самостоятелна задача — 20 минути в часа
**Problem statement:** Добавете bounded retry за четене, без да повторите опасна mutation.

**Functional requirements:** Собствена таблица GET/POST×failure→retry/stop; максимум 3 attempts за GET и общ budget <=5 s, ограничен jitter. Retry-After, по-дълъг от оставащия budget, води до отложен/manual action.

**Technical constraints:** Не променяйте POST semantics и не добавяйте idempotency implementation още. Timeout per attempt трябва да оставя място за backoff. Inject clock/random/sleeper за deterministic tests.

**Acceptance criteria:** Transient 503→200 се възстановява; 422, malformed и Cancel не се повтарят. Постоянен outage приключва в budget. POST timeout се показва като uncertain outcome и няма automatic retry.

Решението и кратката аргументация се проверяват в края на същото занятие. Това не е домашна работа.

## 11. Failure scenarios / Edge cases
| Сценарий | Очакване / наблюдение |
|---|---|
| DNS .invalid / connection refused | Transport category, без HTTP status. |
| 503 / Retry-After | Ограничено решение според budget. |
| 200 malformed JSON | DecodeFailure, не Success(empty). |
| Cancel по време на backoff | Няма следващ attempt. |
| POST response липсва | Unknown outcome, без сляп retry. |

## 12. Тестване
Unit policy tests с fake clock и scripted responses; happy 200; 422/503/malformed; delayed callback след cancellation. Integration: emulator към fixture, airplane mode по време на request и recovery. Проверете максимум един active call за refresh и че body/resources се затварят.

## 13. Наблюдение и измерване
Logical refresh count, HTTP attempt count, per-attempt и total duration, response bytes и terminal category. Съберете поне 20 repeatable GET runs за p50/p95; обозначете малката извадка. Bytes/requests са mobile cost proxies, не директна battery енергия.

Запишете кратка таблица в `results/lab02/observations.md`: configuration, workload, expected/actual, sample count и ограничения. За latency използвайте p50/p95/max, когато имате достатъчно измервания, а не само средна стойност.

## 14. Въпроси за анализ
1. Защо HTTP 403 не е липса на Internet?
2. Как timeout създава uncertain mutation outcome?
3. Защо cancellation не е error за retry?
4. Как attempts и logical requests се различават?
5. Къде трябва да се зададе общият deadline?
6. Защо retry на няколко layers може да стане storm?

## 15. Очакван резултат
Mobile API layer с explicit failure result, cancellation и самостоятелно обоснован GET retry.

## 16. Критерии за приемане
- [ ] Timeout/transport/HTTP/decode са различими.
- [ ] Response body и Call се освобождават.
- [ ] Stale response не сменя нов UI state.
- [ ] Retry има attempts и total deadline.
- [ ] POST uncertainty е описана.
- [ ] Fake и emulator failure evidence са предадени.
