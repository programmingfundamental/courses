---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 6. Начален експеримент — 15 минути

На един server изпълнете два 10 s closed-loop runs със същия payload и продължителност: първо един клиент, после 10 clients. Запишете throughput и средна/median/p95/p99 latency. Проверете дали по-висок throughput съществува с по-висока latency; не предполагайте предварително резултата.

После активирайте кратък controlled 200 ms service stall в отделен диагностичен run. Сравнете closed-loop и open-loop samples, including scheduling lag. Не включвайте fault-injection run в основната performance таблица. Предскажете кой начин на подаване „скрива“ повече от периода на недостъпност.

## 7. Основна лабораторна задача — 45 минути

### Стъпка 1 — хипотеза и controls

Преди изпълнение запишете поне две хипотези, например за idle resource cost и tail latency при наситен pool. Изберете ECHO с 256 body bytes, persistent connections, едно outstanding request на connection, еднакъв TCP_NODELAY, timeouts и payload validation.

Документирайте JDK/OS/CPU/RAM, JVM heap flags, commits, connection cap, pool workers/queue, idle/write deadlines, logging level и client host. За първата серия с 10 clients осигурете поне 10 pool workers. За scaling серия фиксирайте workers (например 32) и отчетете waiting/rejected connections — изводите са за **модел + конфигурация**. Допълнителна серия с workers=C може да изолира ефекта на pool cap, ако ресурсите позволяват.

### Стъпка 2 — configurable load generator

Следващият CLI е договор за програмата, която реализирате; не е готов executable в материалите:

```sh
java -cp out labnet.load.LoadGenerator --host localhost --port 9000 --clients 100 --mode closed --payload-bytes 256 --warmup-seconds 10 --duration-seconds 20 --timeout-ms 2000 --max-inflight 100 --seed 42 --csv results/run.csv
java -cp out labnet.load.LoadGenerator --host localhost --port 9002 --clients 100 --mode open --rate 500 --payload-bytes 256 --warmup-seconds 10 --duration-seconds 20 --timeout-ms 2000 --max-inflight 100 --seed 42 --csv results/open.csv
```

Валидирайте аргументите: clients>0, payloadBytes<=65528, positive rate/durations, maxInflight<=documented hard cap. Server address и port са explicit, за да не натоварите друга услуга по грешка.

```java
record LoadConfig(int clients, int payloadBytes, int maxInflight,
                  Duration warmup, Duration measurement,
                  Duration timeout, long seed) {}
record Sample(long requestId, long scheduledNanos, long sentNanos,
              long completedNanos, String outcome, int responseBytes) {}
interface Recorder {
    void record(Sample sample);
    void snapshot(long nowNanos);
}
```

Използвайте общия encoder/decoder. Virtual threads могат да се използват за **клиентския driver** във всички серии еднакво; server моделите остават тези от курса. Един reader owner на socket проверява type, ID и exact ECHO bytes. Response mismatch е error, не success.

В closed mode един request/response loop управлява всяка persistent connection. В open mode един scheduler подава arrivals през свободни connection slots; всяка има bounded mailbox от една задача и един outstanding request. Ако няма slot, отчита LOCAL_REJECT. Не създавайте нова нишка/Future на всеки arrival без cap.

Deadline е от scheduled time. При timeout connection се затваря, за да не се смеси късен response със следваща заявка; reconnect attempts са ограничени и се броят отделно. Driver спира submission след measurement window, drain-ва до timeout и завършва всички requests с terminal status. Executors, sockets и CSV writer се затварят.

### Стъпка 3 — scheduler, samples и counters

За open loop изчислявайте `scheduled = start + index * interval`, вместо `sleep(interval)` след приключила заявка. Записвайте `sendLag=sent-scheduled`. Ако scheduler изостане над request deadline, отбележете LOCAL_LATE и прескочете arrival; не изпращайте безкраен catch-up burst. Лимитът на outstanding е част от резултата.

Recorder не трябва да блокира network reader за disk I/O. Използвайте bounded sample queue към един CSV writer; queue overflow прави run **невалиден за percentiles**, а не тихо загубени данни. За малък run е допустим preallocated bounded sample array; ако cap се достигне, приключете run с explicit reason. Планирайте storage capacity от rate×duration и worst-case memory.

Разделете две гледни точки: throughput брои completions, настъпили в measurement window; latency/outcomes използват cohort-а от заявки, scheduled в този window, включително responses в bounded drain. Late drain completions не увеличават window throughput. Warm-up samples са отделени и не участват в report statistics.

### Стъпка 4 — experiment matrix

Първо correctness smoke test. После за всяка от трите архитектури изпълнете 10, 100, 500 и 1000 clients, **ако средата позволява**, с 10 s warm-up, 20 s measurement, до 2 s drain и 3 повторения. Разбъркайте реда на архитектурите с фиксиран seed. Не пускайте servers едновременно на една machine за сравнение.

Ако thread/file-descriptor/memory limits не позволяват ниво, запишете `not run: конкретна причина`; не измисляйте числа. Минималната приемлива серия при ограничена среда е три модела × две изпълними concurrency нива × три повторения, плюс documented attempt/limit за по-високите нива.

Добавете open-loop rates под, около и над наблюдавания устойчив капацитет за една фиксирана concurrency конфигурация. За Lab 5 WORK използвайте cost=20 ms като отделна серия; не смесвайте нейните latencies с ECHO. За TLS по избор отделете connect+handshake+AUTH от steady-state exchanges.

## 8. Failure scenarios и edge cases — 15 минути

| Случай | Провокация | Отчитане |
|---|---|---|
| Server rejection | pool/admission cap | SERVER_REJECT/connection refusal, не success |
| Mid-run disconnect | controlled close | DISCONNECT, terminal request accounting |
| Response timeout | controlled delay | TIMEOUT, долна latency граница |
| Wrong response | test handler връща друг ID/bytes | PROTOCOL_ERROR |
| Generator saturation | нисък maxInflight/бавен scheduler | LOCAL_REJECT/LOCAL_LATE, send lag |
| Recorder overflow | малък sample cap | run invalid за percentiles |
| No successful samples | 100% errors | N/A mean/percentiles, explicit error rate |
| Resource ceiling | твърде много connections | actual established count и причина |

Един scheduled request има точно един terminal outcome. Проверете conservation след drain: `scheduled = success + serverRejected + timeout + disconnect + protocolError + localRejected + localLate + otherFailure`. Connection setup failures са отделна статистика, когато още няма scheduled request.

## 9. Наблюдение и измерване — 25 минути

На 1 s събирайте actual active connections, outstanding requests, CPU и memory на **server и generator отделно**. `jcmd <pid> GC.heap_info`, JFR и OS process metrics са възможни източници; JVM heap used не е process RSS. За standard-library sampling използвайте ManagementFactory/[MemoryMXBean](https://docs.oracle.com/en/java/javase/25/docs/api/java.management/java/lang/management/MemoryMXBean.html) във всеки измерван JVM и [ProcessHandle.Info.totalCpuDuration](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/ProcessHandle.Info.html), когато е налично. Липсваща метрика се записва като N/A, не 0. CPU percent = CPU-time delta / wall-time delta ×100; при тази дефиниция многопроцесорен процес може да надвиши 100%. Ако нормализирате по cores, отбележете го.

| Модел/config | requested/active clients | repeats | completed req/s | goodput B/s | avg ms | median ms | p95 ms | p99 ms | errors % | rejects % | CPU % | heap/RSS |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| попълнете от CSV | | | | | | | | | | | | |

Пазете per-run values. За обобщение покажете median throughput и range между повторенията; latency percentiles или са per-run, или са изчислени върху ясно описан merged raw sample set. Не представяйте mean of p99 като общ p99.

Изгответе две графики: throughput/concurrency и p95+p99/concurrency, със същите конфигурации и error-rate annotations. Допълнете open-loop графика offered/completed/rejected rate. Ако generator CPU/lag показва bottleneck, run не измерва server capacity; намалете rate или отделете машините и повторете засегнатия run.

### Финален технически доклад

Създайте `results/lab07/report.md` със заглавие **Comparative Analysis of Java Network I/O Models** и следните части:

1. Research questions и предварителни хипотези.
2. System under test: модели, commits и configuration matrix.
3. Method: hardware/software, workload, warm-up, windows, repeats и metrics definitions.
4. Results: таблици, графики, sample counts, errors и raw CSV paths.
5. Interpretation: коя хипотеза се подкрепя и какви алтернативни обяснения има.
6. Limitations: loopback/shared CPU, caps, JVM warm-up/GC, sample size, scheduler/clock effects.
7. Recommendation за конкретен workload и следващ експеримент, който би могъл да я опровергае.

Доклад от приблизително 3–5 страници е достатъчен, ако има реални данни и ясни аргументи. В часа подгответе таблици и основни изводи; окончателното редактиране може да е част от предаването.

## 10. Въпроси за анализ

1. Защо requests/s не е latency и не е брой clients?
2. Кога closed loop прикрива претоварването?
3. Как pool size обърква сравнение на I/O architectures?
4. Как ще разберете, че generator е bottleneck?
5. Какво пропуска p99, изчислен само върху успешните requests?
6. Защо не осредняваме p99 от отделните clients?
7. Какво може да се заключи от loopback и какво остава неизвестно?
8. Какъв нов експеримент би опровергал предпочитаната ви архитектура?

## 11. Самостоятелно надграждане

Изберете една конкретна хипотеза от доклада, променете само един фактор — например pool size или output watermark — и повторете засегнатата серия. Добавете before/after данни и проверете дали подобрението не е прехвърлило цената към rejection rate или ресурси.

## 12. Очакван резултат

Configurable Java load generator, raw CSV и възпроизводим comparative report за поне три server модели. Изводът е условен спрямо workload и configuration, а не класация на API класове.

## 13. Критерии за приемане

- [ ] Генераторът проверява protocol correctness и има bounded resources.
- [ ] Има closed-loop и open-loop режими с explicit schedule accounting.
- [ ] Сравнени са трите модела при повторяеми configurations.
- [ ] 10/100/500/1000 clients са измерени или ограниченията са документирани.
- [ ] Има req/s, byte throughput, average/median/p95/p99 latency.
- [ ] Active connections, CPU, memory, errors и rejects са отчетени.
- [ ] Warm-up, measurement и drain имат ясни граници.
- [ ] Има поне три repeats и raw data, не само screenshot.
- [ ] Докладът съдържа таблици, графики, ограничения и проверими изводи.

## 14. Допълнителни задачи

1. Измерете TLS handshake/session reuse отделно от persistent request latency.
2. Сравнете platform и virtual thread server със същите budgets и workload.
3. Добавете allocation/JFR анализ и проверете дали bottleneck е codec, GC, scheduler или network.
4. Повторете избрана серия на отделни machines и анализирайте разликата спрямо loopback.
