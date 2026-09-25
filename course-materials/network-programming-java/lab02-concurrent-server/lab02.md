# Лабораторно упражнение 2 — Concurrent TCP Server

## 1. Контекст и инженерен проблем

Първият клиент на LabNet остава свързан, без да изпраща команда. Втори клиент успява да установи TCP connection, но не получава application response. След въвеждане на threads проблемът се променя: двама потребители могат да получат едно име, а едновременни отговори повреждат frame stream.

Трябва да обслужим много connections, без да загубим protocol correctness и без неограничено потребление на ресурси.

**Какво използваме от предходното упражнение:** Message, codec, dispatcher skeleton, PING/ECHO/QUIT и raw-frame tests. **Какво ще се използва по-късно:** session lifecycle, командите, atomic registration и thread-pool вариантът са основа за NIO, TLS и сравнителните измервания.

## 2. Учебни цели

- Реализира thread-per-client и ограничен thread-pool вариант.
- Определя ownership на session, input stream и output stream.
- Открива и поправя check-then-act race condition.
- Реализира LOGIN/SEND/BROADCAST/USERS/STATS с точна семантика.
- Измерва поведение при много connections и изчерпан pool.
- Изпълнява graceful shutdown с bounded drain и cleanup.
- Аргументира limits и trade-offs при slow clients.

## 3. Необходими предварителни знания

Работещ Lab 1, Thread/Runnable, synchronized, happens-before, atomic операции, exceptions и executor lifecycle.

## 4. Необходими инструменти

JDK, IDE, Git и няколко terminal процеса. `jcmd <pid> Thread.print` служи за thread dump. Клиентският test driver създава най-много 100 connections в началните експерименти.

## 5. Теоретична подготовка

TCP connection може да стои в OS accept backlog, без application handler да е започнал. При thread-per-client acceptor стартира `new Thread(new ClientSession(socket)).start()`. Това изолира blocking reads, но броят platform threads нараства с connections. Pool ограничава threads, но дълго живеещи sessions могат да заемат всички workers.

```text
acceptor -> admission limit -> client-session executor
                               |    |    |
                            session A/B/C -> shared registry
```

`ExecutorService` е lifecycle abstraction; конкретната queue policy е отделен избор. `Executors.newFixedThreadPool(n)` използва неограничена work queue. Тук създаваме fixed-size `ThreadPoolExecutor` с bounded queue и explicit rejection, за да не складираме произволен брой accepted sockets. Вижте [ThreadPoolExecutor](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html).

```java
ExecutorService sessions = new ThreadPoolExecutor(
    32, 32, 0L, TimeUnit.MILLISECONDS,
    new ArrayBlockingQueue<>(32),
    new ThreadPoolExecutor.AbortPolicy());
```

32 workers + 32 waiting sessions е **учебна конфигурация**, не универсален sizing. Admission cap=64 включва accepted running и queued sockets; sockets в backlog не са application active sessions. При rejection acceptor затваря socket и връща permit. Не използвайте CallerRunsPolicy: blocking handler в acceptor спира новото приемане.

`ConcurrentHashMap` защитава отделни операции, но не прави `containsKey` + `put` атомарна последователност. Също така thread-safe map не прави mutable `ClientSession` thread-safe.

### Разширение на протокола

Header, ID и отговорите са от Lab 1. PING/ECHO/QUIT остават достъпни преди login за diagnostics. `LOGIN` е именуване, не authentication.

| Request | Body | Успех / error |
|---|---|---|
| LOGIN `0x10` | UTF-8 име, regex `[A-Za-z0-9_-]{1,24}` | OK / NAME_IN_USE, ALREADY_LOGGED_IN |
| SEND `0x11` | 1-byte name length + recipient name + UTF-8 text, text 1..4096 bytes | OK / NO_SUCH_USER, SLOW_RECIPIENT |
| BROADCAST `0x12` | UTF-8 text 1..4096 bytes | DATA с ASCII `delivered=N;failed=M` |
| USERS `0x13` | празно | DATA с имена, разделени с LF, sorted snapshot |
| STATS `0x14` | празно | DATA с `active`, `loggedIn`, `requests`, `errors`, `rejected` |

SEND/BROADCAST/USERS/STATS изискват login, иначе `ERROR/NOT_LOGGED_IN`. Невалиден UTF-8 се отхвърля с decoder, настроен на `CodingErrorAction.REPORT`. Response ID съвпада с request ID. `EVENT=0x82` има ID=0 и body: 1-byte sender-name length + sender name + text. Client има един reader, който маршрутизира EVENT отделно от отговорите.

SEND OK означава, че server е приключил локалния write към recipient, не че потребителят го е прочел. BROADCAST брои такива локално успешни writes; не е атомарна доставка до всички. Глобален ред между различни изпращачи не се обещава. При промени на registry USERS е ограничен snapshot, не транзакционен read на целия свят. При твърде много имена върнете `ERROR/RESULT_TOO_LARGE`, вместо да нарушавате MAX_PAYLOAD.

## 6. Начален експеримент — 15 минути

Отворете client A към baseline от Lab 1 и го оставете idle. Client B изпраща PING. Измерете connect time и response timeout отделно. После добавете thread-per-client и повторете с 10 clients. Сравнете active handler count и thread dump. Запишете защо successful connect не означава available application capacity.

За теста временно задайте read timeout 10 s, за да не изчезне idle A преди наблюдението; после върнете documented настройката.

## 7. Основна лабораторна задача — 50 минути

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

## 8. Failure scenarios и edge cases — 20 минути

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

## 9. Наблюдение и измерване — 15 минути

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
