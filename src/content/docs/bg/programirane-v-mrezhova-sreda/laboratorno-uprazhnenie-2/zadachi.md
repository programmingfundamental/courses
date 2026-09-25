---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 6. Начален експеримент

Отворете client A към baseline от Lab 1 и го оставете idle. Client B изпраща PING. Измерете connect time и response timeout отделно. После добавете thread-per-client и повторете с 10 clients. Сравнете active handler count и thread dump. Запишете защо successful connect не означава available application capacity.

За теста временно задайте read timeout 10 s, за да не изчезне idle A преди наблюдението; после върнете documented настройката.

## 7. Основна лабораторна задача

### Стъпка 1 — lifecycle и ownership

Създайте `ClientSession implements Runnable, AutoCloseable`, `SessionRegistry` и `ServerMetrics`. Един handler чете от session input; всички writers към същия socket използват **един per-session lock върху целия encoded frame и flush**. Никога не дръжте registry lock по време на network I/O.

```java
interface SessionRegistry {
    boolean register(String name, ClientSession session);
    void unregister(String name, ClientSession expected);
    List<String> namesSnapshot();
}
```

Предотвратете двойно освобождаване чрез idempotent `close()` и permit ownership flag/atomic state. Външен close трябва да може да затвори socket без придобиване на write lock, иначе watchdog би блокирал зад stuck write.

### Стъпка 2 — намерете race condition

Следният код е **умишлено грешен**, въпреки ConcurrentHashMap:

```java
if (!users.containsKey(name)) {
    raceGate.await(); // само в тест: двата клиента стигат едновременно тук
    users.put(name, session);
    return true;
}
return false;
```

Напишете test с `CyclicBarrier(2)` и два clients със същото име. Докажете, че двама callers могат да получат success. Поправете registration с atomic map операция. При disconnect отстранете само очакваната session: стар handler не трябва да изтрие нова session със същото име. Повторете 100 пъти с bounded test timeout.

### Стъпка 3 — commands и client receiver

Реализирайте таблицата. Client receiver разпознава unsolicited EVENT; CLI не чете директно от socket. Тествайте A→B SEND, BROADCAST от C и едновременно PING от B. Дръжте ID correlation независимо от event доставката. Изпратете 100 frames от два senders към B и проверете, че decoder никога не вижда interleaved header/payload.

Разширете type/ID validation на общия codec: ID=0 е допустим само за EVENT от server към client. Client не може да подава EVENT; всички негови request IDs остават положителни и строго нарастващи. Отговорите и events не променят server-side last-request-ID state. Добавете regression tests и за двете посоки.

### Стъпка 4 — fixed pool и admission

Добавете `--mode thread-per-client|pool`, `--max-clients`, `--workers`, `--pending-connections`. И двата режима имат admission cap. При waiting session поставете accept timestamp; session, чакала над 2 s, се затваря от periodic sweeper и task-ът пропуска работа, когато бъде изпълнен. Queue capacity остава bounded дори след timeout.

Между `accept`, permit acquisition, task submission и handler start запишете кой притежава socket. При всяка exception branch определете кой го затваря.

### Стъпка 5 — slow peers и shutdown

Blocking write няма SO_TIMEOUT. В тази версия използвайте един periodic watchdog, който проверява `writeStartedAt` и затваря socket при write над 2 s; write owner установява и изчиства timestamp под per-session lock, а watchdog затваря независимо от него. Това е coarse deadline с толеранс на sweep interval, не precise timer.

При shutdown: спрете accept чрез close на listener; спрете admission; `shutdown()` на executor; дайте до 3 s за нормално приключване; затворете всички останали running/queued sockets от tracked set; извикайте `shutdownNow()` при нужда; обработете returned queued tasks и изчакайте bounded termination. Interrupt сам по себе си не е policy за освобождаване на blocking sockets. Затворете watchdog executor.

## 8. Failure scenarios и edge cases

| Случай | Провокация | Проверка |
|---|---|---|
| Duplicate login | barrier + еднакво име | точно един OK |
| Output interleaving | двама senders към един recipient | 200 валидни EVENT frames |
| Idle pool exhaustion | 32 idle running + waiting clients | bounded queue, observable timeout/rejection |
| Client 65 | cap=64 | отказ, без leak на permit/socket |
| Recipient disconnect | затворете B при SEND | локална грешка; A и server продължават |
| Slow reader | B спира да чете, bounded burst SEND | watchdog затваря B, останалите продължават |
| Shutdown с waiting tasks | запълнете pool и queue | bounded termination и active=0 |
| Стар cleanup след reconnect | симулирайте replacement session | новият mapping остава |

## 9. Наблюдение и измерване

Сравнете 10, 32 и 64 connections в двата режима. За всяка конфигурация запишете connected, running, queued, rejected, threads, response latency и резултат от shutdown. За тестовете с idle clients различавайте active connections от active requests.

Логовете съдържат connection ID, request ID, lifecycle transition и error code, без целите payloads. Използвайте atomic counters или `LongAdder` за статистика; приблизителният snapshot при concurrent updates се отбелязва. Admission correctness използва permit/atomic state, а не приблизителен metrics counter.

## 10. Въпроси за анализ

1. Защо ConcurrentHashMap не поправи първоначалния LOGIN?
2. Защо write lock е върху целия frame, а не върху всяко поле?
3. Как една waiting connection може да изглежда успешно свързана?
4. Кое ще се изчерпи при голям thread-per-client server?
5. Какви зависимости създава blocking BROADCAST към slow recipient?
6. Защо remove(name, expected) е по-безопасно от remove(name)?
7. Защо `shutdownNow()` не заменя socket ownership?

## 11. Самостоятелно надграждане

Добавете per-user rate limit за SEND с ограничен burst. State-ът му принадлежи на session, освобождава се с нея и не изисква безкраен global map. Сравнете fairness между активен и тих клиент.

## 12. Очакван резултат

Multi-client LabNet с два concurrency режима, race regression test, ясна доставка на EVENT и bounded resource lifecycle. Предайте таблица, thread dump summary и lifecycle diagram в `results/lab02/`.

## 13. Критерии за приемане

- [ ] Запазени са framing tests от Lab 1.
- [ ] Шестте нови команди имат валидирани payloads и определени errors.
- [ ] Duplicate LOGIN допуска точно един winner.
- [ ] Frames към една session не се interleave-ват.
- [ ] Pool queue, active clients и write duration са ограничени.
- [ ] Shutdown освобождава running и queued sockets.
- [ ] Има измервания за поне три concurrency нива и четири failure tests.

## 14. Допълнителни задачи

1. Сравнете platform и virtual threads при същия admission cap.
2. Добавете bounded outgoing mailbox с един writer owner; измерете ефекта върху slow recipients.
3. Изследвайте snapshot consistency на USERS при масов reconnect.
