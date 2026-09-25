# Lab 5 — Бележки за преподавателя

Само за преподавателя; не се включва в студентския сайт.

## 1. Цел на упражнението

Да се види, че asynchronous не означава unlimited. Всеки handoff има капацитет, ownership и terminal path. Conceptual outcome е аргументирана overload policy и измерване на цената ѝ.

## 2. Очаквано предварително ниво

Работещ NIO decoder/output loop, executors и request correlation. Подгответе controlled-rate driver skeleton, CSV writer и workload stub. Студентите реализират admission, completion и policy.

## 3. Препоръчително разпределение на времето

| Дейност | Минути |
|---|---:|
| Сценарий и capacity хипотеза | 10 |
| Pipeline и budgets | 15 |
| Blocking event loop / saturation експеримент | 15 |
| Async реализация и policy | 50 |
| Failure и cleanup tests | 20 |
| Измервания | 15 |
| Анализ | 10 |
| **Общо** | **135** |

На 40-тата минута трябва да е наблюдавано забавяне на независим PING; на 90-тата — bounded worker completion path. Ако времето е ограничено, fail-fast е приемливата основна policy; pause-reads е самостоятелното надграждане.

## 4. Как да бъде въведен проблемът

Изчислете идеализираните 200 requests/s и попитайте какво ще стане при 400 requests/s за минута. Поискайте прогноза за queue depth, memory и latency отделно. После наблюдавайте cap и counters.

## 5. Основни точки за обяснение

Capacity се измерва; queues абсорбират bursts; downstream buffers са част от бюджета; completion queue изисква reservation; event-loop ownership се запазва; timeouts не отменят автоматично work; latency на успешните requests не описва rejected requests; fairness е отделна цел.

## 6. Чести грешки на студентите

Unbounded default executor; `join` в event loop; CallerRunsPolicy там; worker writes към channel; повторна употреба на mutable input buffer; completion след reconnect отива в нова session; early credit release при cancel; unbounded ERROR queue; read resume само на един client; спиране на OP_WRITE заедно с OP_READ; сумиране на p99 queue и p99 service.

## 7. Насочващи въпроси

Къде ще стои резултатът, ако мрежата е бавна? Как гарантирате място за всеки accepted task? Кой връща credit при exception? Какво става, ако cancel не спре service? Имате ли proof, че reserved+queued bytes никога не прескачат cap?

## 8. Очаквана архитектура на решението

Event loop е единствен owner на connection state. WorkItem и Completion носят immutable snapshot и connection generation. Worker queue и completion queue са bounded; global credit остава за task от admission до terminal completion consumption. Per-client byte reservation остава до conversion в output и реално изпращане. Затворена connection освобождава output, а късната completion освобождава останалите task credits.

## 9. Ключови части от примерно решение

**Не показвайте преди студентите да открият completion queue проблема.**

```text
on request:
    validate; require client credit, global credit, response bytes
    reserve all
    try submit task
    catch rejection: rollback all; bounded BUSY or close
worker:
    try execute -> success completion
    catch service failure -> failed completion
    finally publish exactly one terminal completion; wakeup selector
event loop:
    consume completion
    if connection generation valid: convert reservation to output
    else: discard response and remaining reservation
    release task/global credit exactly once
```

Точно един terminal completion може да се осигури с един task wrapper; не добавяйте отделни competing callback paths за success и timeout. Capacity 68 е достатъчна само ако invariant включва completedNotDrained и admission не връща credit по-рано. Ако offer не успее въпреки reservation, това е invariant violation, а не нормална тихо игнорирана загуба.

## 10. Как да се демонстрират edge cases

За детерминирана saturation блокирайте четирите workers на test latch, enqueue-нете още 64 и подайте следваща заявка; освободете latch в finally. За disconnect задръжте completion, затворете клиента и после освободете. За out-of-order използвайте controlled latches за два ID, вместо да разчитате само на sleep. За slow output fake writer връща 0 до watermark. Проверете credits/reservations след всеки test, не само response bytes. Shutdown тест включва running, queued и completedNotDrained задачи.

## 11. Очаквани резултати

Под capacity queue е кратка; около capacity latency variance расте; над capacity policy води до reject или upstream wait. Throughput може да се стабилизира, докато offered rate расте. Числата не са фиксирани. Fail-fast може да показва нисък p99 за успешните requests и висок rejection rate; и двете трябва да се докладват.

## 12. Критерии за оценяване

| Област | Точки | Доказателство |
|---|---:|---|
| Correctness | 25 | async correlation и terminal completions |
| Protocol/network understanding | 15 | backpressure vs shedding |
| Robustness | 25 | bounded resources, disconnect/cancel/shutdown |
| Code quality | 10 | ownership и reservation bookkeeping |
| Experimental work | 15 | load series, metrics, graph |
| Analysis | 10 | fairness и cost на policy |
| **Общо** | **100** | |

Не оценявайте само completed throughput. Leak при exception трябва да се отрази в robustness независимо от красивата графика.

## 13. Въпроси за устна проверка

1. Каква е разликата между concurrency и capacity? — Едновременни requests срещу устойчиво обработени за време.
2. Защо completion credit е нужен? — Приетата задача неизбежно произвежда terminal result и трябва да има bounded място.
3. Как OP_READ pause помага? — Спира application draining, после TCP receive flow control влияе upstream; ефектът не е мигновен.
4. Защо cancel не освобождава всичко веднага? — Task може физически да продължава; terminal accounting трябва да е точно веднъж.
5. Защо requestId е необходим тук? — Различни service times пренареждат отговорите.
6. Как нисък p99 може да заблуждава? — Изчислен само върху survivors, докато много requests са rejected/timed out.

## 14. Как упражнението се свързва със следващото

Lab 6 добавя TLS на blocking transport и общи defensive deadlines/limits. Students трябва да разберат, че TLS не заменя bounded queues или malformed-frame protection. Lab 7 измерва точно тези admission и latency trade-offs.
