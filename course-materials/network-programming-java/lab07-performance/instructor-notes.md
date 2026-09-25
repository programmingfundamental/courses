# Lab 7 — Бележки за преподавателя

Само за преподавателя; не се включва в студентския сайт.

## 1. Цел на упражнението

Да се защити техническо решение с измервания и ограничения. Концептуалните цели са measurement validity, workload control, tail latency и reproducibility. „Най-бързият server“ не е учебен резултат сам по себе си.

## 2. Очаквано предварително ниво

Работещи TCP variants и regression suites, metrics от Lab 5, познаване на percentile. Преди часа подгответе празни LoadConfig/Sample/Recorder и CLI skeleton; студентите реализират scheduling, lifecycle и reporting. Осигурете еднаква JDK версия и baseline configuration лист.

## 3. Препоръчително разпределение на времето

| Дейност | Минути |
|---|---:|
| Research questions и хипотези | 10 |
| Метрики и experimental controls | 15 |
| Начален latency/throughput опит | 15 |
| Generator, controls и matrix setup | 45 |
| Failure/accounting tests | 15 |
| Серии, таблици и графики | 25 |
| Анализ и report outline | 10 |
| **Общо** | **135** |

Пълната матрица 3 модела ×4 нива ×3 повторения от 10+20+до2 s е около 19–20 минути без setup. Автоматизирайте run order; при по-бавна машина използвайте минималната documented matrix. Не намалявайте warm-up тайно, за да получите желан резултат. Окончателното оформление на report може да се довърши след часа.

## 4. Как да бъде въведен проблемът

Покажете два примерни, ясно обозначени като хипотетични резултата: повече req/s с повече rejects и по-ниска average latency с по-лош p99. Поискайте criteria за избор според workload. Не обявявайте предварително NIO или threads за победител.

## 5. Основни точки за обяснение

Offered/admitted/completed са различни; requests/s и B/s зависят от payload; concurrency не е rate; averages скриват tail; timeouts не са successful samples; closed loop се самозабавя; generator/recorder са част от measurement system; controls и repeats са задължителни; mean of percentiles не е aggregate percentile.

## 6. Чести грешки на студентите

Един run без warm-up; различен payload между servers; сравнение с различни security settings; console logging на всяка заявка; generator и server споделят CPU без отчитане; отчитат requested вместо established connections; reconnect time се смесва с RTT; timeouts се изтриват; local rejects изчезват от denominator; recorder губи samples; pool cap се интерпретира като присъщо качество на Java I/O API.

## 7. Насочващи въпроси

Какво е denominator-ът на error rate? Кои requests участват в p99? Какво става със заявка, започнала преди края и завършила в drain? Има ли CPU или scheduler lag при generator? Кой фактор се промени между двата runs? Как бихте опровергали собствената хипотеза?

## 8. Очаквана архитектура на решението

```text
Config + seeded run matrix
          |
arrival scheduler -> bounded connection slots -> LabNet clients
          |                                      |
   local rejects/late                     verified responses
          +---------------> bounded recorder <---+
                                  |
                              CSV -> analysis/report
```

Client sockets имат един reader owner и един request в полет за основната ECHO серия. Open-loop scheduler не чака response. Samples запазват scheduled/send/completion timestamps в един clock domain. Server и generator имат отделни process metrics. Coordinator управлява startup, warm-up, measurement и bounded drain.

## 9. Ключови части от примерно решение

**Не показвайте преди обсъждане на percentile дефиницията.**

```java
static long nearestRank(long[] sorted, double p) {
    if (sorted.length == 0 || p <= 0 || p > 1) {
        throw new IllegalArgumentException();
    }
    int index = (int) Math.ceil(p * sorted.length) - 1;
    return sorted[index];
}
```

Validate-нете с known samples 1..100: p50=50, p95=95, p99=99. Test-нете n=1 и empty case. Sorting е извън hot network loop. Не създавайте огромен sample array без предварителен cap.

За terminal accounting използвайте request state, което се приключва атомарно само веднъж: response/timeout/disconnect могат да се състезават. Записът в recorder и освобождаването на slot принадлежат на победилия terminal transition. След drain проверявайте conservation equation от студентската задача.

## 10. Как да се демонстрират edge cases

За wrong response използвайте controlled test dispatcher, който променя един ID. За timeout задръжте response над deadline, после го освободете и проверете липса на втори terminal result. За generator overload задайте rate много над maxInflight capacity, но ограничете run до 5 s. За recorder overflow задайте capacity=2 и забавете consumer; run трябва да се маркира invalid. За all-errors run затворете server port или използвайте deterministic refusal и очаквайте N/A percentiles. За coordinated omission използвайте един контролиран 200 ms stall и сравнете scheduled-arrival latency.

## 11. Очаквани резултати

Резултатите зависят от machine, JDK, workload и policy. Възможно е NIO да използва по-малко threads, но да не печели при ниска concurrency. Pool cap може да създава timeouts при idle persistent connections. Overload може да ограничи completed throughput и да увеличи rejects. Групите могат да стигнат до различни защитими препоръки; изисквайте данни и controls.

## 12. Критерии за оценяване

| Област | Точки | Доказателство |
|---|---:|---|
| Correctness | 20 | verified replies, generator accounting |
| Protocol/network understanding | 15 | модели и metrics semantics |
| Robustness | 15 | bounded driver, errors, cleanup |
| Code quality | 10 | scheduler/recorder separation |
| Experimental work | 25 | matrix, repeats, CSV, controls, graphs |
| Analysis | 15 | report, limitations, falsifiable recommendation |
| **Общо** | **100** | |

Performance стойностите не носят точки сами по себе си. Отбелязан hardware ceiling с коректно измерени по-ниски нива е приемлив; измислени стойности не са. Доклад без raw evidence не получава пълните experimental точки.

## 13. Въпроси за устна проверка

1. Защо throughput и latency могат да нарастват едновременно? — Повече concurrency увеличава completed work, но и contention/queueing.
2. Какво е coordinated omission? — Closed-loop подаването пропуска потенциални arrivals по време на забавяне и може да подцени waiting experience.
3. Какво е честно сравнение на pool с NIO? — Общ workload/security, explicit pool/cap configuration и отчетени waiting/rejected connections.
4. Как обработвате timeouts в p99? — Не ги превръщаме в successes; докладваме отделно censored outcomes и rate.
5. Защо warm-up не е фиксирана магическа стойност? — JIT/GC/workload варират; проверяваме стабилност и описваме избрания период.
6. Как откривате generator bottleneck? — CPU, send lag, local rejects, recorder overflow и отделна машина/повторение.
7. Коя промяна опровергава препоръката? — Конкретна контролирана промяна на workload или resource constraints и предварителна прогноза.

## 14. Как упражнението се свързва със следващото

Това е финалът на курса. Следващата работа е една аргументирана оптимизация на същия LabNet проект, последвана от повторение на засегнатите tests/измервания. Връзката обратно към Lab 1 е важна: висока производителност без framing correctness не е успешен резултат.
