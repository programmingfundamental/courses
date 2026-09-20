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

## 6. Начален експеримент — 15 минути

Добавете временно 20 ms delay директно в NIO dispatcher. Пускайте WORK от client A и измерете PING latency от B: independent connection чака заради event-loop blocking. После преместете delay в 4-worker pool с **bounded** queue от 64 и подайте 400 requests/s за 10 s. Предскажете кога queue ще достигне cap и сравнете с наблюдението.

Не е нужна реална unbounded queue, за да видите растежа; при cap запишете saturation и refusals. Клиентският driver също има max outstanding и отчита local rejects, вместо да складира безкрайно requests.

## 7. Основна лабораторна задача — 50 минути

### Стъпка 1 — отделете application work

```java
record WorkItem(long connectionId, long generation, long requestId,
                long acceptedAtNanos, int costMillis, byte[] data) {}
record Completion(long connectionId, long generation, long requestId,
                  Message response, long startedAtNanos, long endedAtNanos) {}
interface WorkService {
    Message execute(WorkItem item) throws Exception;
}
```

Records с array не са автоматично дълбоко immutable. Копирайте data при handoff или прехвърлете exclusive ownership; decoder не може да презаписва същата памет. Worker няма достъп до SocketChannel, SelectionKey или session output queue.

### Стъпка 2 — bounded executor и приемане

Конструирайте fixed ThreadPoolExecutor с ArrayBlockingQueue(64) и AbortPolicy. Преди submit проверете client/global credits и response byte budget. При RejectedExecutionException отменете reservation точно веднъж и върнете `ERROR/BUSY`, ако bounded output позволява; иначе затворете connection. Не изпълнявайте rejected task inline в event loop.

Не използвайте `Executors.newFixedThreadPool` без анализ на queue. Ако използвате `CompletableFuture.supplyAsync`, подайте този explicit executor и обработете submission rejection. `join()`/`get()` в event loop е забранено. Стандартните async overloads без executor използват default execution facility; това не е вашият capacity limit. Вижте [CompletableFuture](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/CompletableFuture.html).

### Стъпка 3 — completion handoff

Всеки accepted task публикува точно един terminal completion, включително при service exception. След enqueue извиква `selector.wakeup()`. Event loop обработва completions преди/след select, валидира connectionId+generation, превръща reservation в output buffer и добавя OP_WRITE.

Disconnected client не трябва да бъде „съживен“ от късен completion. Drop-нете резултата, но освободете credit/reservation. Rejected/expired work и worker exceptions се броят отделно. `future.cancel()` или timeout не означава, че physical task е спряла; не връщайте execution credit преждевременно и не double-release-вайте при късен completion.

### Стъпка 4 — overload policy

Реализирайте поне една от двете стратегии и документирайте другата:

- **Fail fast:** при липса на work capacity изпратете `ERROR/BUSY`, ако output budget позволява. Ограничете read/frame budget; ако client не чете errors и запълни output, close. Client backoff е негово explicit поведение, не автоматично следствие от BUSY.
- **Pause reads:** при 4 in-flight заявки изключете OP_READ за този client; включете отново при <=2. При глобална saturation спрете reads на засегнатите connections и ги възстановявайте round-robin при освободен credit. Само OP_WRITE/close/timers продължават. Не губете вече buffered complete frames; не decode-вайте безкрайно напред.

High/low thresholds създават hysteresis и намаляват често превключване. Pause reads постепенно използва TCP receive-window flow control, но OS buffers могат временно да приемат още bytes.

### Стъпка 5 — slow clients, fairness и shutdown

Добавете output high watermark=128 KiB, low=64 KiB и hard cap=256 KiB. Above high спрете новото приемане от client; below low възстановете само ако останалите work limits позволяват. Не спирайте OP_WRITE. Един client има max 4 accepted tasks, което ограничава monopolization, но не доказва идеална fairness между всички.

Заявка, чакала в queue над 500 ms, приключва с `ERROR/EXPIRED`, без service execution. Използвайте enqueue/start timestamps. Shutdown спира admission, изчаква работата/completions до 3 s, отчита незавършените, затваря connections и workers с bounded await. Изчистете cancellation paths и reservations; test-нете ги.

## 8. Failure scenarios и edge cases — 20 минути

| Случай | Стимул | Очакване |
|---|---|---|
| Queue saturation | λ над измерения μ | bounded queue и reject/pause |
| Slow receiver | client изпраща, не чете | bounded output, pause/close |
| Disconnect with running work | close след submit | completion dropped, credits released |
| Worker exception | test hook за един ID | един ERROR/FAILED, event loop жив |
| Out-of-order completion | cost 50 ms и 0 ms | правилна ID correlation |
| Queue expiry | намалете workers и deadline | EXPIRED без service effect |
| Client flooding | един агресивен + няколко тихи | per-client caps, measured fairness |
| Shutdown under load | stop при nonempty queues | няма lost permits/reservations |

## 9. Наблюдение и измерване — 15 минути

Изпълнете λ=50, 150, 300, 500 requests/s по 10 s с cost=20 ms, 4 workers и еднакъв брой clients. За всяка серия запишете offered, admitted, completed, BUSY, EXPIRED, client-local rejects и timeouts. Ресурсите и latencies се измерват, не се предполагат.

CSV на 1 s: queue depth/max, active workers, completion queue depth, reserved/output bytes, throughput, CPU, heap и per-client completions. Request samples съдържат queue wait, service duration и end-to-end latency; изчислете median, p95, p99. Не сумирайте percentiles на компонентите, за да получите end-to-end percentile.

Сравнете baseline с реализираната policy и представете графика queue length/time или latency/offered rate. За „по-стабилно“ решение покажете и цената: повече rejections, по-малък accepted throughput или по-дълго client wait.

## 10. Въпроси за анализ

1. Защо по-голяма queue не увеличава устойчивия service rate?
2. Защо bounded work queue без bounded completion/output queue не е достатъчна?
3. Какво поврежда CallerRunsPolicy в selector thread?
4. Кога pause reads започва да влияе на sender-а?
5. Как се освобождава ресурс при късен completion за затворена connection?
6. Защо timeout не означава cancellation на physical work?
7. Как fairness cap може да намали общия throughput, но да подобри обслужването?

## 11. Самостоятелно надграждане

Реализирайте втората overload стратегия и сравнете fail-fast с pause-reads при един и същ offered rate. Запазете отделно latency на completed requests, rejected rate и време за recovery след спиране на товара.

## 12. Очакван резултат

LabNet с bounded asynchronous pipeline и поне една работеща backpressure/overload policy. Предайте budget diagram, CSV, графика и анализ в `results/lab05/`.

## 13. Критерии за приемане

- [ ] Event loop не изпълнява work и не чака Future.
- [ ] Work, completion, output и per-client usage имат limits.
- [ ] Reservation се освобождава точно веднъж във всички paths.
- [ ] Responses се съпоставят по ID, включително out-of-order.
- [ ] BUSY/pause/close policy е описана и тествана.
- [ ] Slow client и disconnect не задържат памет безкрайно.
- [ ] Измерени са queue, latency percentiles, throughput, rejects и resources.
- [ ] Има експеримент над capacity и анализ на fairness.

## 14. Допълнителни задачи

1. Добавете weighted per-client scheduling с bounded pending state.
2. Сравнете deadline-aware admission с rejection само при пълна queue.
3. Изследвайте отделни worker pools за CPU work и симулиран blocking I/O при общ memory budget.
