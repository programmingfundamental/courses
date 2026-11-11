# Упражнение 7 — Измерване на доставката

**След доказана доставка и удостоверяване.** Измервайте същата операция при три начина за обслужване на връзка. Пуснете един сървър, избирайте модела за новите връзки и отваряйте нови измервателни клиенти за всеки опит. При три клиента с по десет публикации целта е 30 успешни заявки и 90 уникални получавания.

## 1. Подайте измерването от съществуващата клиентска конзола

[ChatClient.java · редове 85–90](../chat-solution/src/labnet/ChatClient.java#L85-L90)

```java
            case "/bench" -> {
                int count = args.length > 1 ? Integer.parseInt(name) : 3;
                int messages = args.length > 2 ? Integer.parseInt(args[2]) : 10;
                Benchmark.measure(host, port, tls, keys, count, messages, 30, Path.of("results/chat.csv"));
                return true;
            }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 85–87 | `/bench` приема брой измервателни клиенти и брой публикации от всеки. При липса на аргументи се използват 3 и 10. |
| 88 | Използвайте същия хост, порт и TLS контекст. Началният интервал е 30 ms, а резултатът се добавя в `results/chat.csv`. |
| 89 | Върнете управлението на клиентската конзола след опита. |

[Benchmark.java · редове 118–125](../chat-solution/src/labnet/demo/Benchmark.java#L118-L125)

```java
    public static void measure(String host, int port, SSLContext tls, DemoKeys keys, int clients, int messages, int intervalMs, Path csv) throws Exception {
        if (clients < 1 || clients > 16 || messages < 1 || messages > 100 || intervalMs < 1 || intervalMs > 1000)
            throw new IllegalArgumentException("Benchmark: 1..16 clients, 1..100 messages, 1..1000 ms");
        Files.createDirectories(csv.toAbsolutePath().getParent());
        System.out.println(HEADER);
        run(host, port, tls, keys, clients, messages, intervalMs, csv);
    }
}
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 118 | Публичният метод приема параметрите на вече работещия сървър и място за резултата. |
| 119–120 | Проверете преди старта границите 1…16 клиента, 1…100 текста и интервал 1…1000 ms. |
| 121 | Създайте директорията за CSV, ако липсва. |
| 122–123 | Отпечатайте имената на колоните и изпълнете един опит с тези параметри. |

## 2. Постройте предварително точната множина на очакваните събития

Съобщението `post-00001` от `bench1` е различно от същия текст от `bench2`. В рамките на една входяща кутия ключът е двойката автор и текст. Използвайте различна кутия за всеки получател.

[Benchmark.java · редове 17–41](../chat-solution/src/labnet/demo/Benchmark.java#L17-L41)

```java
public final class Benchmark {
    private static final String HEADER = "mode,clients,messages_each,published,expected_events,received,missing,duplicates,unexpected,rejected,timeouts,errors,publications_s,events_s,own_samples,p95_own_ms";

    private static void run(String host, int port, SSLContext tls, DemoKeys keys, int count, int messages, int intervalMs, Path csv) throws Exception {
        List<String> names = new ArrayList<>();
        for (int n = 1; n <= count; n++) names.add("bench" + n);
        int publications = count * messages, expected = count * publications;
        var seen = new ArrayList<Set<ChatConnection.Message>>();
        var corpus = new HashSet<ChatConnection.Message>();
        List<String> texts = new ArrayList<>();
        for (int n = 1; n <= messages; n++) {
            String content = String.format(Locale.ROOT, "post-%05d", n);
            texts.add(content);
            for (int user = 0; user < count; user++) corpus.add(new ChatConnection.Message(names.get(user), content));
        }
        var sentAt = new ConcurrentHashMap<ChatConnection.Message, Long>();
        var latencies = new ConcurrentLinkedQueue<Long>();
        var received = new CountDownLatch(expected);
        var duplicates = new AtomicInteger();
        var unexpected = new AtomicInteger();
        var rejected = new AtomicInteger();
        var timeouts = new AtomicInteger();
        var errors = new AtomicInteger();
        var completed = new AtomicInteger();
        var clients = new ArrayList<ChatConnection>();
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 18 | Заглавието на CSV определя фиксиран ред за броячите, скоростите и времето за собственото събитие. |
| 20–22 | Методът получава параметрите на опита и изгражда списък от отделни имена `bench1` до избрания брой. |
| 23 | Изчислете логическите публикации като `count × messages`. Всяка е предназначена и за всеки измервателен клиент, затова умножете още веднъж по `count`. |
| 24 | Подгответе списък с по едно множество на видяното за всеки получател. |
| 25–26 | Общата множина държи допустимите двойки, а списъкът `texts` държи само текстовете за изпращане. |
| 27–30 | Номерирайте текстовете с пет цифри и добавете по една очаквана двойка за всеки автор и всеки номер. |
| 32–33 | Конкурентната карта запомня началния момент за всяка публикация. Опашката получава времената до собствените събития. |
| 34 | `CountDownLatch` започва на 90 в основния опит. Всяко ново валидно получаване го намалява с едно. |
| 35–36 | Два отделни брояча разграничават повторена двойка от двойка извън очакваната множина. |
| 37–40 | Отчитайте отхвърлени заявки, изтекли срокове, други грешки и успешни заявки в отделни атомарни броячи. |
| 41 | Пазете всички отворени връзки за последващите бариери и за затваряне. |

## 3. Отворете клиентите и проверете всяко получаване

[Benchmark.java · редове 42–64](../chat-solution/src/labnet/demo/Benchmark.java#L42-L64)

```java
        try (var writers = Executors.newFixedThreadPool(count)) {
            for (int i = 0; i < count; i++) {
                String name = names.get(i);
                Set<ChatConnection.Message> inbox = ConcurrentHashMap.newKeySet();
                seen.add(inbox);
                var client = new ChatConnection(host, port, tls, message -> {
                    if (!names.contains(message.author())) return;
                    if (!corpus.contains(message)) { unexpected.incrementAndGet(); return; }
                    if (!inbox.add(message)) { duplicates.incrementAndGet(); return; }
                    if (message.author().equals(name)) latencies.add(System.nanoTime() - sentAt.get(message));
                    received.countDown();
                });
                clients.add(client);
                if (client.request(AUTH, new Named(name, keys.token(name)).pack()).type() != OK)
                    throw new IllegalStateException("AUTH " + name);
                if (client.request(LOGIN, bytes(name)).type() != OK) throw new IllegalStateException("LOGIN " + name);
            }
            String stats = text(clients.getFirst().request(STATS, EMPTY).body());
            String mode = stats.substring(stats.lastIndexOf("model=") + 6);
            int audience = text(clients.getFirst().request(USERS, EMPTY).body()).split("\n").length;
            var jobs = new ArrayList<Future<?>>();
            long start = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(100);
            for (int i = 0; i < count; i++) {
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 42 | Създайте по един подаващ работник за всеки клиент и осигурете затваряне на пула при изход. |
| 43–46 | За всяко име създайте отделна конкурентна входяща кутия и я добавете към списъка за крайния отчет. |
| 47 | Регистрирайте обработчик за събития на нова реална мрежова връзка. |
| 48 | Игнорирайте авторите извън измервателната група. Обикновените участници могат да бъдат в същата стая. |
| 49 | Събитие от измерван автор с неочакван текст увеличава `unexpected`. |
| 50 | Ако добавянето към собствената кутия върне `false`, този клиент вече е получил същата двойка. Увеличете `duplicates`. |
| 51 | Само за собствения автор вземете разликата между текущия монотонен момент и момента на изпращане. |
| 52 | След всички проверки отчетете едно валидно уникално получаване. |
| 54–56 | Запазете връзката и поискайте AUTH с индивидуалния ѝ токен. Прекратете опита при отказ. |
| 57 | Влезте в стаята със същото име и изисквайте `OK`. |
| 59–60 | Вземете модела от статистиката на първата измервателна връзка. Задайте `/io` преди опита и оставете този избор стабилен до края. |
| 61 | Пребройте всички имена в стаята. Този брой включва и присъстващите извън измервателната група и определя очакваното `delivered`. |
| 62–63 | Подгответе списък със задачите и общо начало на измерването на 100 ms напред. |
| 64 | Започнете да създавате по една подаваща задача на клиент. |

## 4. Подайте публикациите и отделете отговор от събитие

Отговорът `DATA` се проверява при подателя, а събитията се проверяват в обработчиците на получателите. Един успешен `DATA` при три измервателни клиента изисква още три отделни наблюдения в техните входящи кутии.

[Benchmark.java · редове 65–85](../chat-solution/src/labnet/demo/Benchmark.java#L65-L85)

```java
                ChatConnection client = clients.get(i);
                String name = names.get(i);
                jobs.add(writers.submit(() -> {
                    for (int n = 0; n < messages; n++) {
                        long scheduled = start + TimeUnit.MILLISECONDS.toNanos((long) n * intervalMs);
                        while (scheduled > System.nanoTime()) LockSupport.parkNanos(scheduled - System.nanoTime());
                        String content = texts.get(n);
                        sentAt.put(new ChatConnection.Message(name, content), System.nanoTime());
                        try {
                            var reply = client.request(BROADCAST, bytes(content));
                            if (reply.type() == DATA && text(reply.body()).equals("delivered=" + audience + ";failed=0"))
                                completed.incrementAndGet();
                            else rejected.incrementAndGet();
                        } catch (TimeoutException timeout) { timeouts.incrementAndGet(); }
                        catch (Exception failure) { errors.incrementAndGet(); }
                    }
                }));
            }
            for (var job : jobs) job.get();
            received.await(3, TimeUnit.SECONDS);
            double seconds = (System.nanoTime() - start) / 1e9;
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 65–67 | Изберете клиента и името му и подайте задача към пула. |
| 68–70 | За всеки пореден текст изчислете планирания момент. При по-ранно събуждане отново проверете колко време остава. |
| 71–72 | Вземете текста и запишете началния момент преди мрежовото изпращане. Обработчикът на собственото събитие може да се изпълни почти веднага. |
| 74 | Изпратете една публикация и изчакайте нейния отговор. Следващата заявка на този клиент идва след отговора. |
| 75–77 | Успех означава `DATA` с всички текущи получатели и нула откази. Друг резултат се отчита отделно като `rejected`. |
| 78–79 | Разделете изтекъл срок от всички останали изключения. |
| 81 | Приключването на задачата става след изпращането на всички текстове от този автор. |
| 83 | Изчакайте всички подаващи задачи. |
| 84 | Оставете до три секунди за липсващите към този момент събития. Окончателната проверка ще ги преброи. |
| 85 | Спрете часовника за скоростите преди проверочните PING заявки и преобразувайте интервала в секунди. |

## 5. Докажете доставката и запишете CSV

[Benchmark.java · редове 86–106](../chat-solution/src/labnet/demo/Benchmark.java#L86-L106)

```java
            // PING is a per-connection barrier after all publication writes and replies.
            for (var client : clients) {
                try { if (client.request(PING, EMPTY).type() != OK) errors.incrementAndGet(); }
                catch (TimeoutException timeout) { timeouts.incrementAndGet(); }
                catch (Exception failure) { errors.incrementAndGet(); }
            }
            int unique = seen.stream().mapToInt(Set::size).sum(), missing = expected - unique;
            int actual = unique + duplicates.get() + unexpected.get();
            var sorted = latencies.stream().sorted().toList();
            String p95 = sorted.isEmpty() ? "NA" : String.format(Locale.ROOT, "%.3f",
                    sorted.get((int) Math.ceil(0.95 * sorted.size()) - 1) / 1e6);
            String row = String.format(Locale.ROOT, "%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%.2f,%.2f,%d,%s",
                    mode, count, messages, completed.get(), expected, actual, missing, duplicates.get(), unexpected.get(),
                    rejected.get(), timeouts.get(), errors.get(), completed.get() / seconds, actual / seconds, sorted.size(), p95);
            System.out.println(row);
            append(csv, row);
            if (completed.get() != publications || missing != 0 || actual != unique || sorted.size() != publications
                    || errors.get() != 0 || timeouts.get() != 0)
                throw new IllegalStateException("Delivery check failed: " + row);
        } finally { clients.forEach(ChatConnection::close); }
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 86–88 | След публикациите направете по един PING на същите връзки. Четецът на всеки клиент обработва по-ранните кадри на своя поток преди този отговор. |
| 89–90 | Запишете таймаут или грешка и за проверочната заявка. |
| 92 | Сумирайте размерите на отделните кутии. Разликата спрямо очакваното дава липсите. |
| 93 | Прибавете дубликатите и неочакваните събития, за да получите общия брой отчетени събития. |
| 94 | Подредете пробите за време до собственото събитие във възходящ ред. |
| 95–96 | При празна извадка запишете `NA`. Иначе вземете индекс `ceil(0.95 × n) − 1` и разделете наносекундите на милион за ms. При 30 проби това е 29-ата подредена стойност. |
| 97–99 | Попълнете колоните в същия ред като заглавието. Скоростите са успешни публикации или отчетени събития, разделени на продължителността. |
| 100–101 | Изведете и запишете реда преди крайната проверка, за да останат данни и при неуспешен опит. |
| 102–104 | Проверете публикациите, липсите, извънредните събития, броя времеви проби и грешките. Нарушението прекратява опита с конкретния ред от отчета. |
| 105 | Затворете всяка създадена измервателна връзка, включително при изключение. |

[Benchmark.java · редове 107–116](../chat-solution/src/labnet/demo/Benchmark.java#L107-L116)

```java
    private static synchronized void append(Path csv, String row) throws Exception {
        try (var file = FileChannel.open(csv, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             var lock = file.lock()) {
            long size = file.size();
            file.position(size);
            String content = (size == 0 ? HEADER + "\n" : "") + row + "\n";
            ByteBuffer data = StandardCharsets.UTF_8.encode(content);
            while (data.hasRemaining()) file.write(data);
        }
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 107–109 | Синхронизирайте записите в един процес и вземете файлово заключване за стартирани отделно процеси. |
| 110–111 | Прочетете размера под заключването и преместете позицията след последния записан байт. |
| 112 | Добавете заглавие само при празен файл, след което добавете един CSV ред. |
| 113–114 | Кодирайте съдържанието в UTF-8 и повтаряйте записа, докато в буфера има байтове. |

## 6. Приключете общия сървър

След последния опит затворете клиентите и въведете `/quit` в сървърната конзола. При `try` с ресурс в `main` това стига до `ChatServer.close`.

[ChatServer.java · редове 141–148](../chat-solution/src/labnet/ChatServer.java#L141-L148)

```java
        if (line.startsWith("/")) return "Unknown command. Use /help";
        return room.publish("server", Frame.bytes(line)).get(8, TimeUnit.SECONDS);
    }
    public void close() throws Exception {
        running = false; selector.wakeup(); loop.join();
        listener.close(); selector.close(); counter.close();
        threads.close(); pool.close(); handshakeTasks.close(); timers.shutdownNow(); room.close();
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 141 | Неизвестна сървърна команда получава съобщение за помощ. |
| 142 | Обикновен текст от сървърната конзола се публикува със запазен автор `server`. Изчакайте общия отчет до осем секунди. |
| 144–145 | Обявете край на цикъла, събудете селектора и изчакайте неговата нишка. Блокът `finally` в `run` приключва приетите сесии. |
| 146 | Затворете слушащия канал, селектора и UDP приемника. |
| 147 | Приключете изпълнителите за сесии и TLS, спрете таймерите и освободете работниците на стаята. |

**Проверка в час.** На сървъра подайте `/io thread`, а на клиента `/bench 3 10`. Повторете със `/io pool` и `/io nio`, като изчаквате края на всеки опит. Дръжте броя на останалите участници постоянен. В трите реда на `results/chat.csv` очаквайте `published=30`, `expected_events=90`, `received=90`, `missing=0`, `duplicates=0` и `own_samples=30`. Сравнявайте измерените стойности за скорост и p95 от собственото изпълнение. Автоматичният сценарий `java -cp out labnet.demo.Demo 7` изпълнява трите модела срещу един сървър и приключва с `PASS lab07`.

[Съдържание на ръководството](../hint.md)
