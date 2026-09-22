---
title: "Лабораторно упражнение 7 — Performance engineering и сравнителен анализ"
sidebar:
  order: 7
  label: Упражнение 7
---

# Лабораторно упражнение 7 — Performance engineering и сравнителен анализ

## 1. Контекст и инженерен проблем

Екипът има три работещи реализации на LabNet. Едната използва повече threads, друга държи waiting connections, а трета има event loop. Трябва да изберем подходяща конфигурация за конкретен workload. Единично число „requests/s“ не показва нито tail latency, нито отказите, нито цената в ресурси.

**Какво използваме от предходното упражнение:** defensive tests и TLS adapter; всички TCP codecs, servers и metrics идват от Lab 1–6. **Какво ще се използва по-късно:** резултатите оформят финалния технически доклад и дават аргументи за следваща архитектурна промяна на същия проект.

## 2. Учебни цели

- Формулира проверима хипотеза за workload и server configuration.
- Реализира configurable Java load generator с bounded resources.
- Измерва requests/s, byte throughput, latency percentiles и error rates.
- Разграничава offered load, admitted work, completions и concurrency.
- Контролира warm-up, repeats, client bottleneck и experiment conditions.
- Интерпретира latency/throughput/resource trade-offs без предварителен победител.
- Представя таблици, графики и ограничения в технически доклад.

## 3. Необходими предварителни знания

Работещи и запазени baseline versions, passing failure suites, CSV обработка, средна стойност/median/percentile, timestamps и JVM process metrics. Полезно е предварително да се подготви skeleton на генератора и празен report template.

## 4. Необходими инструменти

JDK, IDE, terminal, Git, `jcmd`, JFR по избор. Използвайте Java за генератора и обработката на samples. За графиките е достатъчен spreadsheet или Java-generated SVG; инструментът за графика не трябва да променя начина на измерване.

## 5. Теоретична подготовка

| Метрика | Дефиниция за експеримента |
|---|---|
| Offered requests/s | планирани arrivals / measurement seconds |
| Completed requests/s | валидни успешни responses, получени в measurement window / seconds |
| Goodput B/s | успешни response application body bytes / seconds |
| Protocol throughput B/s | реално изпратени/получени LabNet bytes, включително headers, отделно по посока |
| End-to-end latency | response completion - планиран arrival, на client monotonic clock |
| Service-observed RTT | response completion - действителен send timestamp |
| Concurrency | установени connections и outstanding requests, отделни counts |
| Error/rejection rate | съответните terminal outcomes / всички scheduled requests |

Не наричайте protocol throughput „wire bytes“, ако не измервате TCP/IP/TLS overhead. Не смесвайте setup/handshake time с persistent-connection RTT. Използвайте `System.nanoTime()` само за интервали в един JVM процес.

**Closed loop:** всяка connection изпраща следваща заявка след отговора. Така бавният server автоматично намалява offered rate. **Open loop:** arrivals се планират независимо от responses; ако генераторът няма свободен slot, отчита local rejection, вместо да трупа безкрайна queue. Closed-loop latency може да пропусне чакането, което реални независимо пристигащи заявки биха имали — проблемът на coordinated omission. Затова се изисква и open-loop проверка около saturation.

За sorted samples `x[0..n-1]` използвайте nearest-rank percentile: `x[ceil(p*n)-1]` за p=0.50, 0.95, 0.99. При n=0 записвайте N/A. Малък брой samples дава нестабилна p99; посочете n. Не осреднявайте per-client p99 като „общ p99“ и не сумирайте percentiles. Timeouts са отделна категория с долна граница на waiting time, а не успешно завършени samples.

### Архитектури за задължителното сравнение

1. **Blocking / thread-per-connection** от Lab 2: platform thread за connection, admission cap.
2. **Thread-pool server** от Lab 2: fixed platform workers и bounded pending sessions.
3. **NIO server** от Lab 4: един event loop, bounded buffers и кратък ECHO dispatcher.

Последователният baseline от Lab 1 може да бъде допълнителна четвърта колона. NIO+workers от Lab 5 се измерва отделно при WORK workload. TLS от Lab 6 е отделна серия върху blocking implementation, защото иначе едновременно променяме security и I/O model.
