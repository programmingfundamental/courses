---
title: "Лабораторно упражнение 5 — Async processing, queues и backpressure"
sidebar:
  order: 5
  label: Упражнение 5
---

# Лабораторно упражнение 5 — Async processing, queues и backpressure

## 1. Контекст и инженерен проблем

LabNet вече приема много connections, но нова команда обработва всяка заявка за около 20 ms. Мрежовият слой може да приема по-бързо, отколкото workers обслужват. Ако сложим queue без limit, системата първо изглежда отзивчива, после latency и паметта растат, а старите requests губят полезност.

**Какво използваме от предходното упражнение:** NIO event loop, decoder, output queues и metrics. **Какво ще се използва по-късно:** admission budgets, deadlines, resource ownership и latency breakdown са част от defensive policies в Lab 6 и от експериментите в Lab 7.

## 2. Учебни цели

- Отделя network I/O от application work чрез producer/consumer pipeline.
- Реализира bounded worker и completion queues.
- Определя ownership при asynchronous handoff и disconnect.
- Измерва queue wait, service time и end-to-end latency.
- Реализира поне една overload/backpressure стратегия.
- Ограничава slow-client output и per-client resource usage.
- Аргументира fairness, rejection и out-of-order response semantics.

## 3. Необходими предварителни знания

Работещ Lab 4, ExecutorService, concurrency primitives, ByteBuffer ownership и request IDs. Прочетете difference между blocking wait и asynchronous completion.

## 4. Необходими инструменти

JDK, IDE, terminal, Git, `jcmd`/JFR по избор. Java test driver подава configurable request rate; пълният генератор ще бъде систематизиран в Lab 7.

## 5. Теоретична подготовка

```text
Clients -> NIO Event Loop -> bounded work queue -> workers
              ^                                  |
              +---- bounded completion queue <---+
              |
         bounded per-client output
```

Producer произвежда задачи, consumer ги обработва. При входящ rate λ над устойчивия service rate μ backlog расте, докато не се достигне limit или се промени rate. За 4 workers и симулирани 20 ms чакане грубата идеализирана оценка е 4/0.020=200 requests/s. Това е хипотеза, не измерен капацитет: scheduler, encoding и deadlines добавят разход.

Backpressure ограничава подаването upstream; load shedding отказва част от товара. Queue капацитетът купува кратък burst tolerance за сметка на memory и waiting time, не добавя устойчив капацитет. Little's law `L=λW` е полезна за приблизително стабилни средни величини, но не оправдава оценка от нестационарен overload run.

### WORK и response correlation

`WORK=0x20`; payload след общия ID е `costMillis` като big-endian int `0..50`, следван от 0..4096 data bytes. Worker симулира service delay и връща `DATA` със същия ID и **само оригиналните data bytes**. Реализацията е учебен service stub; sleep/park е допустим в worker, никога в selector. Не приемайте неограничен cost от client.

До 4 outstanding requests на connection са разрешени. Отговорите могат да пристигат извън request реда; client ги съпоставя по уникален requestId. ID се използва веднъж за целия connection. `QUIT` се подава след всички outstanding responses; при нарушение връщайте `ERROR/IN_FLIGHT` и продължете с pending work. Response timeout не доказва, че работата не е изпълнена.

### Resource budget

| Ресурс | Начален limit | Owner |
|---|---:|---|
| Workers | 4 | server |
| Work queue | 64 tasks | executor |
| Global accepted work credits | 68 | event loop / terminal completion |
| Completion queue | 68 entries | workers produce, event loop consumes |
| Outstanding work на client | 4 | event loop |
| Output + reserved response bytes | 256 KiB/client | event loop |
| Active connections | 64 | event loop |

Всеки accepted task резервира **един completion slot** чрез global credit и byte budget за бъдещия encoded response, преди да влезе в work queue. Credit се държи и докато резултатът чака в completion queue; така нова работа не измества неприбрани резултати. `queued + running + completedNotDrained <= 68`. Byte reservation се превръща в queued output и се освобождава с действително изпратените bytes. Memory budget трябва да включва и input snapshots, decoder buffers и object overhead.
