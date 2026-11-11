# Упражнение 2 — Една стая и три независими клиента

**След TCP обмена.** В упражнение 1 клиентът изчаква само своя отговор. Сега Ана трябва да вижда съобщения и когато Борис пише, без да натиска Enter. Първо добавете име към кадъра, после регистър на сървъра и независим четец за всяка клиентска връзка.

## 1. Опаковайте името заедно с текста

Добавете `Named` вътре във `Frame`. За събитие от Ана структурата е: един байт със стойност 3, трите ASCII байта на `ana`, после всички UTF-8 байтове на текста. Получателят знае откъде започва текстът, дори той да съдържа интервали.

[Frame.java · редове 45–65](../chat-solution/src/labnet/Frame.java#L45-L65)

```java
    public static boolean validName(String name) { return name.matches("[A-Za-z0-9_-]{1,24}"); }

    // Both EVENT and AUTH use one unsigned name-length byte followed by name and value.
    public record Named(String name, byte[] value) {
        public byte[] pack() {
            if (!validName(name)) throw new IllegalArgumentException("Invalid name");
            byte[] prefix = bytes(name);
            return ByteBuffer.allocate(1 + prefix.length + value.length)
                    .put((byte) prefix.length).put(prefix).put(value).array();
        }

        public static Named unpack(byte[] body) throws IOException {
            if (body.length == 0) throw new IOException("Missing name");
            int size = Byte.toUnsignedInt(body[0]);
            if (size < 1 || size > 24 || size + 1 > body.length) throw new IOException("Invalid name length");
            String name = new String(body, 1, size, StandardCharsets.US_ASCII);
            if (!validName(name)) throw new IOException("Invalid name");
            return new Named(name, java.util.Arrays.copyOfRange(body, size + 1, body.length));
        }
    }
}
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 45 | Допустимото протоколно име има от 1 до 24 ASCII букви, цифри, долни черти или тирета. |
| 47–49 | Двойката име и байтове се използва за събитие и за удостоверяване. При `SEND` същият формат носи име на получател и текст. |
| 50 | Проверете името преди създаването на масива. |
| 51 | Кодирайте името. За разрешените ASCII знаци дължината в байтове съвпада с броя знаци. |
| 52–53 | Заделете място за дължина, име и стойност. Запишете трите части последователно. |
| 56–57 | При разопаковане празно тяло няма дори байт за дължината. |
| 58 | Преобразувайте първия байт в число от 0 до 255. |
| 59 | Проверете диапазона и дали обявеното име се побира в полученото тяло. |
| 60–61 | Прочетете точно тези ASCII байтове след първия и приложете същата проверка за име. |
| 62 | Копирайте остатъка като стойност на новата двойка. |

## 2. Регистрирайте участниците и освободете името при излизане

Създайте `Room` веднъж на сървъра. За всяка приета връзка създавайте отделен `Peer`. Физическата връзка влиза в `connected` при приемане, а в `members` влиза едва след успешен `LOGIN`. Така `/users` показва хората в стаята.

[Room.java · редове 12–26](../chat-solution/src/labnet/Room.java#L12-L26)

```java
public final class Room implements AutoCloseable {
    public abstract static class Peer implements AutoCloseable {
        public volatile String name;
        public String model;
        private String principal;
        private int authFailures;
        private boolean retired;
        public abstract CompletableFuture<Boolean> send(Frame frame);
        public abstract void close();
    }

    private final Map<String, Peer> members = new TreeMap<>();
    private final Map<String, byte[]> tokens;
    private final Set<Peer> connected = new HashSet<>();
    private final LongAdder requests = new LongAdder(), errors = new LongAdder(), rejected = new LongAdder();
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 12–13 | Стаята притежава общото състояние. Вложената абстракция `Peer` дава на стаята интерфейс към една връзка. |
| 14 | Публикуваното име се вижда и от нишките, които доставят съобщения. |
| 15 | Полето пази избрания модел за тази сесия и се връща от `STATS`. |
| 16–17 | При упражнение 6 тук добавете удостовереното име и броя неуспешни опити. |
| 18 | Запомнете, че връзката вече е оттеглена, за да бъде отказан закъснял вход. |
| 19–20 | Конкретният транспорт доставя кадър и завършва бъдещия резултат с успех или отказ. Стаята може също да затвори връзката. |
| 23 | `TreeMap` пази по една сесия за всяко име и връща имената сортирани. |
| 24 | При удостоверяването ще използвате тази таблица за очакваните токени. |
| 25 | `connected` включва и приетите връзки, които още нямат име. |
| 26 | Три брояча събират заявки, грешки и откази при конкуренция между нишки. |

[Room.java · редове 33–58](../chat-solution/src/labnet/Room.java#L33-L58)

```java
    public synchronized void enter(Peer peer) { connected.add(peer); }
    public void rejected() { rejected.increment(); }
    public synchronized String stats() {
        return "active=" + connected.size() + ";loggedIn=" + members.size() + ";requests=" + requests.sum()
                + ";errors=" + errors.sum() + ";rejected=" + rejected.sum() + ";workQueued=" + workers.getQueue().size();
    }
    public void close() { workers.shutdownNow(); }

    public synchronized void leave(Peer peer) {
        connected.remove(peer);
        peer.retired = true;
        if (peer.name != null) members.remove(peer.name, peer);
    }
    private synchronized String login(Peer peer, String name) {
        if (peer.retired) return "SESSION_CLOSED";
        if (!validName(name)) return "BAD_PAYLOAD";
        if (peer.name != null) return "ALREADY_LOGGED_IN";
        if (peer.principal != null && !peer.principal.equals(name)) return "FORBIDDEN";
        if ("server".equals(name) || members.containsKey(name)) return "NAME_IN_USE";
        members.put(name, peer);
        peer.name = name;
        return null;
    }
    private synchronized String users() { return String.join("\n", members.keySet()); }
    private synchronized List<Peer> snapshot() { return List.copyOf(members.values()); }
    private synchronized Peer lookup(String name) { return members.get(name); }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 33–34 | При приемане добавете връзката под заключването на стаята. Отделният брояч за откази може да се увеличи паралелно. |
| 35–37 | Съберете две различни величини — активни връзки и влезли участници. Добавете натрупаните броячи и чакащата работа от упражнение 5. |
| 39 | При спиране на стаята спрете и работниците, добавени в упражнение 5. |
| 41–43 | При излизане премахнете активната връзка и я маркирайте като оттеглена. |
| 44 | Премахнете името само ако все още сочи към тази сесия. Стар обработчик не може да изтрие новопостъпилата Ива. |
| 46 | Входът е синхронизиран. Проверката за заето име и записът стават под едно заключване. |
| 47–49 | Откажете съответно приключила сесия, невалидно име и повторен вход по същата връзка. |
| 50 | При добавяне на AUTH името в LOGIN трябва да съвпадне с вече удостовереното. |
| 51 | Запазете `server` за сървърната конзола и откажете име на друг участник. |
| 52–54 | Запишете връзката под името, поставете името и в сесията и сигнализирайте успех с липса на грешка. |
| 56 | `/users` получава имената, разделени с нов ред. |
| 57 | Направете копие на списъка с получатели в момента на публикацията. След връщане от метода заключването е освободено. |
| 58 | Личното съобщение търси една сесия по име. |

## 3. Разпратете едно събитие и върнете едно потвърждение

Това е мястото, на което чатът става общ. При три участника сървърът създава един `EVENT` и нарежда три записа. След тях връща един `DATA` само на автора. Подателят също трябва да фигурира в снимката на стаята.

[Room.java · редове 60–75](../chat-solution/src/labnet/Room.java#L60-L75)

```java
    public static boolean validMessage(byte[] message) {
        if (message.length == 0 || message.length > 4096) return false;
        try { text(message); return true; }
        catch (IOException invalid) { return false; }
    }

    public CompletableFuture<String> publish(String author, byte[] message) {
        if (!validMessage(message)) return CompletableFuture.completedFuture("BAD_PAYLOAD");
        Frame event = new Frame(EVENT, 0, new Named(author, message).pack());
        List<CompletableFuture<Boolean>> writes = new ArrayList<>();
        for (Peer member : snapshot()) writes.add(member.send(event));
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new)).thenApply(done -> {
            long delivered = writes.stream().filter(write -> write.join()).count();
            return "delivered=" + delivered + ";failed=" + (writes.size() - delivered);
        });
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 60–61 | Проверете текста като байтове. Допустимият диапазон е от 1 до 4096. |
| 62–63 | Декодирайте строго като UTF-8. При невалидно кодиране върнете `false`. |
| 66–67 | Методът за публикация връща бъдещ текстов резултат. Невалидният вход се отхвърля веднага. |
| 68 | Създайте събитие с идентификатор 0 и с име на реалния автор пред текста. |
| 69 | Подгответе списък за резултатите от отделните записи. |
| 70 | Подайте същото събитие към всеки от снимката, включително автора. Всеки транспорт кодира в собствен буфер. |
| 71 | `allOf` изчаква приключването на всички записи. Продължението изчислява броячите, след като резултатите са готови. |
| 72 | Пребройте записите с резултат `true`. Тук `join()` чете вече завършени резултати. |
| 73 | Върнете броя на завършилите локални записи и броя на останалите. Четенето на съответния `EVENT` при всеки клиент се проверява отделно. |

[Room.java · редове 77–103](../chat-solution/src/labnet/Room.java#L77-L103)

```java
    // Each transport dispatches requests serially per peer. NIO completes writes on its selector thread.
    public CompletableFuture<Boolean> handle(Peer peer, Frame request) {
        requests.increment();
        long id = request.id();
        int type = request.type();
        byte[] body = request.body();
        try {
            if (type == QUIT && body.length == 0) {
                leave(peer);
                return peer.send(new Frame(OK, id, EMPTY)).whenComplete((ok, ex) -> peer.close());
            }
            if (type == PING && body.length == 0) return peer.send(new Frame(OK, id, EMPTY));
            if (type == QUIT || type == PING) return error(peer, id, "BAD_PAYLOAD");
            if (type == AUTH) return authenticate(peer, id, body);
            if (peer.principal == null) return error(peer, id, "AUTH_REQUIRED");
            if (type == ECHO) return peer.send(new Frame(DATA, id, body));
            if (type == LOGIN) {
                String problem = login(peer, text(body));
                return problem == null ? peer.send(new Frame(OK, id, EMPTY)) : error(peer, id, problem);
            }
            if (type != USERS && type != BROADCAST && type != SEND && type != STATS && type != WORK)
                return error(peer, id, "BAD_TYPE");
            if (peer.name == null) return error(peer, id, "NOT_LOGGED_IN");
            if (type == USERS)
                return body.length == 0 ? peer.send(new Frame(DATA, id, bytes(users()))) : error(peer, id, "BAD_PAYLOAD");
            if (type == STATS)
                return body.length == 0 ? peer.send(new Frame(DATA, id, bytes(stats() + ";model=" + peer.model))) : error(peer, id, "BAD_PAYLOAD");
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 77–78 | Един метод приема разчетения кадър и сесията, от която идва. |
| 79–82 | Увеличете брояча на заявките. Извадете идентификатор, тип и тяло само веднъж за последващите разклонения. |
| 84–86 | При празен `QUIT` изведете участника от стаята, изпратете `OK` и тогава затворете връзката. |
| 88 | При празен `PING` върнете `OK` със същия идентификатор. |
| 89 | Останалите форми на `PING` и `QUIT` имат недопустимо тяло. |
| 90–91 | На етап 6 включете `authenticate` и проверката за удостоверен участник точно преди обработката на приложните команди. |
| 92 | При `ECHO` върнете същите байтове като `DATA`. |
| 93–95 | Декодирайте името за `LOGIN`. Върнете `OK` или точната причина от `login`. |
| 97–98 | Разрешете познатите типове. Добавете `WORK` при упражнение 5. Върнете `BAD_TYPE` за непознат тип. |
| 99 | Операциите на стаята изискват име от успешен вход. |
| 100–101 | За празен `USERS` върнете сортирания списък. Тяло към тази команда е грешка. |
| 102–103 | За `STATS` върнете броячите и модела на подателя. И тук се допуска само празно тяло. |

[Room.java · редове 104–118](../chat-solution/src/labnet/Room.java#L104-L118)

```java
            if (type == WORK) return work(peer, id, body);
            if (type == SEND) {
                Named to = Named.unpack(body);
                if (!validMessage(to.value())) return error(peer, id, "BAD_PAYLOAD");
                Peer target = lookup(to.name());
                if (target == null) return error(peer, id, "NO_SUCH_USER");
                return target.send(new Frame(EVENT, 0, new Named(peer.name, to.value()).pack())).thenCompose(ok ->
                        ok ? peer.send(new Frame(OK, id, EMPTY)) : error(peer, id, "SLOW_RECIPIENT"));
            }
            if (!validMessage(body)) return error(peer, id, "BAD_PAYLOAD");
            return publish(peer.name, body).thenCompose(counts -> peer.send(new Frame(DATA, id, bytes(counts))));
        } catch (IOException invalid) {
            return error(peer, id, "BAD_PAYLOAD");
        }
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 104 | Добавете това разклонение при въвеждане на пула за `WORK` в упражнение 5. |
| 105–107 | При лично съобщение разопаковайте получател и текст и проверете текста. |
| 108–109 | Намерете активната сесия на получателя. При липса отговорете `NO_SUCH_USER`. |
| 110–111 | Адресатът получава `EVENT` с името на подателя. Едва след записа подателят получава `OK` или `SLOW_RECIPIENT`. |
| 113 | Проверете текста на останалата команда, `BROADCAST`. |
| 114 | Предайте на `publish` името от сесията на автора. Когато записите приключат, върнете броячите в `DATA` с идентификатора на заявката. |
| 115–116 | Грешка при разчитане на тялото става протоколен отговор `BAD_PAYLOAD`. |

[Room.java · редове 149–153](../chat-solution/src/labnet/Room.java#L149-L153)

```java
    private CompletableFuture<Boolean> error(Peer peer, long id, String code) {
        errors.increment();
        return peer.send(new Frame(ERROR, id, bytes(code)));
    }
}
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 149–151 | Съберете отговорите за грешка на едно място. Увеличете брояча и изпратете `ERROR` с номера на първоначалната заявка и текстов код. |

## 4. Дайте на всяка връзка четец и собствена номерация

Създайте `ChatConnection`. Главната конзола подава команди, докато една отделна нишка чете целия вход от сокета. Така присъстващият клиент може да види публикация по време на въвеждане.

[ChatConnection.java · редове 12–30](../chat-solution/src/labnet/blocking/ChatConnection.java#L12-L30)

```java
public final class ChatConnection implements AutoCloseable {
    public record Message(String author, String text) {
        public String toString() { return "[" + author + "] " + text; }
    }
    private final Socket socket;
    private final DataInputStream input;
    private final OutputStream output;
    private final Consumer<Message> events;
    private final ConcurrentMap<Long, CompletableFuture<Frame>> pending = new ConcurrentHashMap<>();
    private final CompletableFuture<Void> ended = new CompletableFuture<>();
    private long lastId;

    public ChatConnection(String host, int port, SSLContext tls, Consumer<Message> events) throws IOException {
        this.events = events;
        Socket plain = new Socket();
        Socket connected = plain;
        try {
            plain.connect(new InetSocketAddress(host, port), 3000);
            plain.setTcpNoDelay(true);
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 12–14 | `Message` представя вече разчетеното събитие. Преобразуването в текст дава формата `[автор] текст`. |
| 16–18 | Запазете сокета и двата му потока за целия живот на една връзка. |
| 19 | При събитие извикайте функцията на този клиент за показване. |
| 20 | Картата свързва номер на заявка с нейния незавършен отговор. Четецът и подателят я използват едновременно. |
| 21 | Отделен бъдещ резултат съобщава, че връзката е приключила. |
| 22 | Номерацията е локална за връзката. Първата заявка ще вземе 1. |
| 24–25 | Подайте адрес, порт, евентуален TLS контекст и обработчик за събития. Запазете обработчика. |
| 26–27 | Създайте TCP сокет. `connected` първоначално сочи към него и по-късно може да поеме TLS обвивката. |
| 29–30 | Свържете се до три секунди и включете `TCP_NODELAY` за малките кадри. |

[ChatConnection.java · редове 41–72](../chat-solution/src/labnet/blocking/ChatConnection.java#L41-L72)

```java
            socket = connected;
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            output = socket.getOutputStream();
        } catch (IOException | RuntimeException failure) {
            connected.close();
            throw failure;
        }
        Thread.ofVirtual().name("chat-receiver").start(this::receive);
    }
    private void receive() {
        try {
            while (true) {
                Frame frame = Frame.read(input);
                if (frame.type() == EVENT && frame.id() == 0) {
                    Named named = Named.unpack(frame.body());
                    events.accept(new Message(named.name(), text(named.value())));
                } else {
                    if (frame.type() != DATA && frame.type() != OK && frame.type() != ERROR)
                        throw new IOException("Unexpected response type");
                    var request = pending.remove(frame.id());
                    if (request == null) throw new IOException("Unexpected response ID");
                    request.complete(frame);
                }
            }
        } catch (Exception end) {
            synchronized (this) {
                close();
                pending.values().forEach(future -> future.completeExceptionally(end));
                pending.clear();
            }
        } finally { ended.complete(null); }
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 41–43 | Запазете избрания сокет. Обвийте неговия вход в буфериран поток за двоични данни и вземете изхода за запис. |
| 44–46 | При неуспешно свързване затворете достигнатия до момента сокет и предайте причината нагоре. |
| 48 | Стартирайте виртуалната нишка `chat-receiver` след инициализирането на потоците. |
| 50–53 | Четете последователно цели кадри, докато връзката е отворена. |
| 54 | Само `EVENT` с номер 0 минава по пътя за публикации. |
| 55–56 | Декодирайте автора и текста. Подайте готовото съобщение на обработчика за тази връзка. |
| 58–59 | В другото разклонение очаквайте един от трите типа отговор. Непознатият тип прекратява връзката. |
| 60–61 | Извадете от картата чакащата заявка по номер. Липсващ номер означава несъответствие в протокола. |
| 62 | Подайте отговора на точно този, който чака заявката. |
| 65–67 | При край на потока или друга грешка синхронизирайте почистването с `submit` и затворете сокета. |
| 68–69 | Завършете чакащите заявки с изключението и изпразнете картата. |
| 71 | Обявете края на връзката, независимо по какъв път е настъпил. |

[ChatConnection.java · редове 73–91](../chat-solution/src/labnet/blocking/ChatConnection.java#L73-L91)

```java
    public synchronized CompletableFuture<Frame> submit(int type, byte[] body) throws IOException {
        if (socket.isClosed()) throw new EOFException("Connection closed");
        if (pending.size() >= 4) throw new IOException("Four requests already pending");
        long id = ++lastId;
        byte[] bytes = new Frame(type, id, body).encode().array();
        var reply = new CompletableFuture<Frame>();
        pending.put(id, reply);
        try { output.write(bytes); }
        catch (IOException failure) { close(); throw failure; }
        return reply;
    }
    public Frame request(int type, byte[] body) throws Exception {
        return submit(type, body).get(8, TimeUnit.SECONDS);
    }
    public CompletableFuture<Void> ended() { return ended; }
    public void close() {
        try { socket.close(); } catch (IOException ignored) {}
    }
}
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 73 | Едно заключване пази реда между избиране на номер и действително подаване на кадъра. |
| 74–75 | Приключила връзка отказва нова заявка. Ограничението от четири чакащи заявки ще се използва за `/burst` при упражнение 5. |
| 76 | Увеличете номера преди употреба, за да започне от 1. |
| 77 | Кодирайте цялата заявка в масив. |
| 78–79 | Създайте бъдещия отговор и го регистрирайте преди записа, защото сървърът може да отговори веднага. |
| 80–81 | Запишете байтовете. При грешка затворете сокета и предайте изключението. |
| 82 | Върнете обекта, който четецът по-късно ще завърши. |
| 84–85 | Синхронната форма използва същия механизъм и чака отговора до осем секунди. |
| 87 | Дайте на конзолата възможност да следи приключването на връзката. |
| 88–89 | Затварянето на сокета събужда и блокиращото четене. |

## 5. Свържете три такива връзки с една конзола

Първо добавете картата и избора на подател, после командите. В обединения проект `open` е и мястото за две по-късни добавки. В упражнение 3 показаният ред задейства UDP брояча. В упражнение 6 преди `LOGIN` се изпраща `AUTH`.

[ChatClient.java · редове 16–56](../chat-solution/src/labnet/ChatClient.java#L16-L56)

```java
/** Each local client owns its TLS socket, UDP session, reader and request IDs. */
public final class ChatClient implements AutoCloseable {
    private record Connection(ChatConnection chat, Counter.Monitor monitor) implements AutoCloseable {
        public void close() { chat.close(); monitor.close(); }
    }
    private final Map<String, Connection> clients = new LinkedHashMap<>();
    private final DemoKeys keys = new DemoKeys();
    private final SSLContext tls;
    private final String host;
    private final int port, udpPort;
    private String current;

    public ChatClient(String host, int port, int udpPort) throws Exception {
        this.host = host; this.port = port; this.udpPort = udpPort;
        tls = keys.clientContext(true);
    }
    private void open(String name) throws Exception {
        prune();
        if (clients.containsKey(name)) { ConsoleIO.line("Already open: " + name); return; }
        byte[] token = keys.token(name);
        var monitor = new Counter.Monitor(host, udpPort, ack -> ConsoleIO.line(name + " UDP " + ack));
        ChatConnection chat = null;
        try {
            chat = new ChatConnection(host, port, tls, event -> {
                ConsoleIO.line(name + " < " + event);
                monitor.displayed();
            });
            requireOk(chat.request(AUTH, new Named(name, token).pack()));
            requireOk(chat.request(LOGIN, bytes(name)));
            clients.put(name, new Connection(chat, monitor));
            chat.ended().thenRun(() -> { ConsoleIO.line("Disconnected " + name); monitor.close(); });
            current = name;
            ConsoleIO.line("Connected " + name);
        } catch (Exception failure) {
            if (chat != null) chat.close();
            monitor.close();
            throw failure;
        }
    }
    private static void requireOk(Frame reply) throws IOException {
        if (reply.type() != OK) throw new IOException(text(reply.body()));
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 16–19 | `Connection` държи чата и бъдещия UDP наблюдател на един човек. При излизане затворете и двата ресурса. |
| 21 | Картата държи локалните връзки в реда на отваряне. Тя позволява няколко клиента в един процес. |
| 22–23 | Учебните ключове и общият клиентски TLS контекст се включват в упражнение 6. |
| 24–26 | Съхранете адрес, портове и текущо избрано име. |
| 28–30 | Конструкторът запомня адресите. В защитената версия зарежда и доверения сертификат. |
| 32–34 | Почистете приключилите връзки и проверете за вече отворено име в същата конзола. |
| 35–36 | В окончателната версия вземете токена на този човек и създайте негов отделен UDP наблюдател. |
| 37–39 | Пазете референция за почистване и отворете нова `ChatConnection` с обработчик за нейните събития. |
| 40 | Изведете името на получателя пред съобщението. Така в една конзола личи кой е получил реда. |
| 41 | В упражнение 3 добавете тук броенето, веднага след показването на реда. |
| 43–44 | На защитения етап изпратете AUTH преди LOGIN. Изчакайте успеха на всяка заявка. |
| 45 | Едва след успешния вход запишете клиентската двойка по име. |
| 46 | При прекъсване покажете кое име е отпаднало и спрете неговия наблюдател. |
| 47–48 | Новата връзка става текущ подател. Сега може да се покаже `Connected`. |
| 49–52 | При неуспех затворете вече създадените ресурси и върнете причината към командната конзола. |
| 55–56 | `requireOk` превръща отказа на сървъра в изключение с получения код. |

[ChatClient.java · редове 58–84](../chat-solution/src/labnet/ChatClient.java#L58-L84)

```java
    private void prune() {
        var iterator = clients.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().chat.ended().isDone()) {
                entry.getValue().close(); iterator.remove();
            }
        }
        if (!clients.containsKey(current)) current = clients.keySet().stream().findFirst().orElse(null);
    }
    private void show(Frame reply) throws IOException {
        String type = switch (reply.type()) { case OK -> "OK"; case DATA -> "DATA"; default -> "ERROR"; };
        ConsoleIO.line(current + " " + type + " #" + reply.id() + " " + text(reply.body()));
    }
    private boolean command(String line) throws Exception {
        prune();
        String[] args = line.split(" ", 3);
        String name = args.length > 1 ? args[1] : "";
        switch (args[0]) {
            case "/help" -> { ConsoleIO.line("/new name, /use name, /clients, /login name, /users, /ping, /echo text, /send name text, /stats, /work ms text, /burst, /bench [clients] [messages], /quit, /exit"); return true; }
            case "/new", "/login" -> { open(name); return true; }
            case "/use" -> {
                if (!clients.containsKey(name)) throw new IOException("No local client " + name);
                current = name; ConsoleIO.line("Active " + name); return true;
            }
            case "/clients" -> { ConsoleIO.line("Local: " + clients.keySet() + " | active=" + current); return true; }
            case "/exit" -> { return false; }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 58–61 | Обходете локалните връзки чрез итератор, който допуска изтриване на текущия елемент. |
| 62–63 | При приключил четец затворете ресурсите и премахнете връзката. |
| 66 | Ако избраното име е отпаднало, вземете първото останало или `null`. |
| 68–70 | Отпечатайте вида, номера и тялото на отговора до името на текущия подател. |
| 72–75 | Преди всяка команда почистете старите връзки. Разделете реда на най-много три части, така че текстът на лично съобщение да може да съдържа интервали. |
| 76–78 | Покажете командите при `/help`. `/new` и `/login` отварят независима връзка. |
| 79–81 | `/use` проверява локалната карта и сменя само подателя. Четците на другите връзки продължават да получават. |
| 83–84 | `/clients` показва локалните сесии, а `/exit` връща `false` към основния цикъл. |

[ChatClient.java · редове 92–113](../chat-solution/src/labnet/ChatClient.java#L92-L113)

```java
        if (current == null) {
            if (line.equals("/quit")) return false;
            ConsoleIO.line("Open a client with /new ana, /new boris or /new iva");
            return true;
        }
        var chat = clients.get(current).chat;
        switch (args[0]) {
            case "/quit" -> {
                show(chat.request(QUIT, EMPTY));
                clients.remove(current).close();
                current = clients.keySet().stream().findFirst().orElse(null);
                if (current != null) ConsoleIO.line("Active " + current);
                return current != null;
            }
            case "/ping" -> show(chat.request(PING, EMPTY));
            case "/users" -> show(chat.request(USERS, EMPTY));
            case "/stats" -> show(chat.request(STATS, EMPTY));
            case "/echo" -> show(chat.request(ECHO, bytes(line.length() > 5 ? line.substring(6) : "")));
            case "/send" -> {
                if (args.length < 3) throw new IllegalArgumentException("/send name text");
                show(chat.request(SEND, new Named(name, bytes(args[2])).pack()));
            }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 92–95 | Ако няма текущ клиент, `/quit` приключва конзолата. За мрежова команда първо поискайте отваряне на връзка. |
| 97–98 | Изберете сокета по текущото име и обработете мрежовата команда. |
| 99–100 | При `/quit` изчакайте сървърното потвърждение за тази връзка. |
| 101–104 | Премахнете точно този клиент, изберете следващия и оставете конзолата да работи, ако има останали. |
| 106 | Изпратете празен `PING` за проверка на връзката. |
| 107–108 | Поискайте от сървъра всички имена или общата статистика. |
| 109 | Изпратете като `ECHO` байтовете на текста след командата. |
| 110–112 | За `/send` проверете дали има адресат и текст. Опаковайте ги заедно и покажете потвърждението на подателя. |

[ChatClient.java · редове 124–130](../chat-solution/src/labnet/ChatClient.java#L124-L130)

```java
            default -> {
                if (line.startsWith("/")) throw new IllegalArgumentException("Unknown command. Use /help");
                show(chat.request(BROADCAST, bytes(line)));
            }
        }
        return true;
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 124–126 | Обикновен текст става `BROADCAST`. Ако редът започва с наклонена черта и командата е непозната, покажете грешка преди изпращане. |
| 129 | Сигнализирайте на `main`, че може да прочете следващ ред. |

**Проверка с три клиента.** Стартирайте `ChatServer` и `ChatClient`. В клиентската конзола изпълнете последователно:

```text
/new ana
/new boris
/new iva
/use ana
Здравейте!
/send iva Само за теб
```

При първия текст потърсете `ana < [ana] Здравейте!`, `boris < [ana] Здравейте!` и `iva < [ana] Здравейте!` по веднъж. Вторият текст се появява като събитие само при Ива. Поставете breakpoint в `Room.publish` на ред 70 и проследете трите `member.send`. За проверки без спиране в дебъгера изпълнете `java -cp out labnet.demo.Demo 2` след компилация. Очаквайте `PASS lab02`.

[Съдържание на ръководството](../hint.md) · [Следващо упражнение — UDP брояч с повторно потвърждение](../lab03-reliable-udp/hint.md)
