# Упражнение 4 — Частично четене и запис с Java NIO

**След работещата стая.** Запазете `Room.handle` като обработчик на вече сглобен кадър. Сега заменете изчакването в `readFully` за NIO връзките с буфер за всяка сесия. Една нишка трябва да може да премине към Борис, докато липсват последните два байта от кадъра на Ана.

## 1. Дайте на сървъра един слушащ канал и един селектор

В крайния код и трите модела приемат връзките през същия порт. Моделът се избира за новата връзка при `accept()`. По-долу полетата за TLS и работниците са показани на окончателните им места, за да можете да свържете надгражданията от упражнения 5 и 6.

[ChatServer.java · редове 13–31](../chat-solution/src/labnet/ChatServer.java#L13-L31)

```java
public final class ChatServer implements AutoCloseable {
    public enum Model { NIO, THREAD, POOL }

    private final DemoKeys keys = new DemoKeys();
    private final Room room;
    private final SSLContext tls;
    private final Selector selector;
    private final ServerSocketChannel listener;
    private final Counter.Receiver counter;
    private final Set<Session> sessions = ConcurrentHashMap.newKeySet();
    private final ExecutorService threads = Executors.newThreadPerTaskExecutor(Thread.ofPlatform().factory());
    private final Semaphore handshakes = new Semaphore(4);
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(16, 16, 0, TimeUnit.SECONDS, new SynchronousQueue<>());
    private final ThreadPoolExecutor handshakeTasks = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(16));
    private final ScheduledThreadPoolExecutor timers = new ScheduledThreadPoolExecutor(1);
    private final Thread loop;
    private volatile Model model = Model.NIO;
    private volatile int chunk = 65536;
    private volatile boolean running = true;
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 13–14 | Сървърът може да се затваря като ресурс. Трите стойности на `Model` избират обработката на всяка следваща връзка. |
| 16–18 | Един набор учебни ключове, една обща стая и един TLS контекст обслужват всички връзки. |
| 19–21 | Пазете селектора и TCP слушателя на сървъра. UDP приемникът от упражнение 3 живее в същия процес. |
| 22 | Наборът държи всички приети сесии. Той допуска премахване и от нишките на блокиращите връзки. |
| 23 | Режимът THREAD създава платформена нишка за всяка подадена сесия. |
| 24 | До четири сесии могат да договарят TLS едновременно. Това ограничение се добавя на защитения етап. |
| 25 | Режимът POOL разполага с 16 работници. `SynchronousQueue` предава на свободен работник без натрупване на чакащи сесии. |
| 26–27 | Отделен пул обслужва TLS задачи, а планираща нишка задейства сроковете на блокиращия транспорт. |
| 28–29 | Запазете нишката на цикъла и началния модел NIO. `volatile` прави смяната от конзолата видима при ново приемане. |
| 30–31 | `chunk` ограничава единичния запис в установен NIO канал. `running` позволява на друга нишка да поиска приключване. |

[ChatServer.java · редове 46–58](../chat-solution/src/labnet/ChatServer.java#L46-L58)

```java
        try {
            listener.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), port));
            listener.configureBlocking(false);
            listener.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception failure) {
            counter.close(); listener.close(); selector.close(); room.close();
            throw failure;
        }
        timers.setRemoveOnCancelPolicy(true);
        loop = Thread.ofPlatform().name("chat-selector").start(this::run);
    }
    public int port() throws IOException { return ((InetSocketAddress) listener.getLocalAddress()).getPort(); }
    public int udpPort() { return counter.port(); }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 47–49 | Свържете слушателя с loopback и зададения порт. Преминете към неблокиращ режим и регистрирайте интерес към приемане на нови връзки. |
| 50–52 | При частично неуспешен старт затворете UDP приемника, канала, селектора и стаята и предайте причината. |
| 54 | Настройте планиращия изпълнител да отстранява отменените таймери от опашката. |
| 55 | Пуснете цикъла само след успешно отваряне на услугите. |
| 57–58 | Прочетете действителния TCP и UDP порт. Тестът използва тези стойности при избор на свободни портове. |

[ChatServer.java · редове 60–85](../chat-solution/src/labnet/ChatServer.java#L60-L85)

```java
    private void accept() throws IOException {
        SocketChannel channel = listener.accept();
        if (channel == null) return;
        if (sessions.size() >= 64 || !handshakes.tryAcquire()) { room.rejected(); channel.close(); return; }
        Session session = null;
        try {
            channel.socket().setTcpNoDelay(true);
            Model selected = model;
            if (selected == Model.NIO) {
                channel.configureBlocking(false);
                session = new SelectorSession(channel, selector, tls, room, sessions::remove, handshakeTasks, handshakes);
            } else {
                channel.configureBlocking(true);
                session = new BlockingSession(channel.socket(), tls, room, sessions::remove, timers, handshakes);
            }
            session.model = selected.name();
            room.enter(session);
            sessions.add(session);
            if (session instanceof BlockingSession blocking)
                (selected == Model.POOL ? pool : threads).execute(blocking);
        } catch (IOException | RuntimeException failure) {
            room.rejected();
            if (session != null) session.close(); else handshakes.release();
            channel.close();
        }
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 60–62 | Поискайте чакаща връзка. При неблокиращ `accept()` може вече да няма такава. |
| 63 | При 64 активни сесии или липса на свободно TLS разрешение отчетете отказ и затворете новия канал. |
| 64 | Запомнете дали вече има обект за сесията. Това определя кой ще почиства при грешка. |
| 66–67 | Включете `TCP_NODELAY` и запазете модела към момента на приемане. |
| 68–70 | При NIO оставете канала неблокиращ. Предайте на сесията същите селектор, стая и TLS контекст. |
| 71–73 | При другите модели изберете блокиращ канал и обвийте приетия сокет в `BlockingSession`. |
| 75–77 | Запазете модела за `STATS`, регистрирайте активната връзка в стаята и в набора на сървъра. |
| 78–79 | Блокиращата сесия се изпълнява от собствена нишка или от ограничения пул. NIO сесията се обслужва в цикъла на селектора. |
| 80–83 | При неуспешно създаване или подаване отчетете отказ. Създадена сесия освобождава своето разрешение при затваряне. При липса на сесия го върнете тук и затворете канала. |

[ChatServer.java · редове 86–110](../chat-solution/src/labnet/ChatServer.java#L86-L110)

```java
    private void run() {
        try {
            while (running) {
                selector.select(150);
                var ready = selector.selectedKeys().iterator();
                while (ready.hasNext()) {
                    var key = ready.next();
                    ready.remove();
                    if (!key.isValid()) continue;
                    if (key.isAcceptable()) accept();
                    else if (key.isReadable()) {
                        var session = (SelectorSession) key.attachment();
                        try { session.read(); } catch (IOException end) { session.close(); }
                    }
                }
                for (Session peer : sessions) {
                    if (peer instanceof SelectorSession session) {
                        try { session.pump(chunk); } catch (IOException | CancelledKeyException end) { session.close(); }
                    }
                }
            }
        } catch (IOException failure) {
            if (running) throw new UncheckedIOException(failure);
        } finally { sessions.forEach(Session::close); }
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 88–89 | Чакайте готовност до 150 ms. Периодичното връщане позволява проверка на сроковете и без ново събитие. |
| 90–93 | Вземете итератор на готовите ключове и отстранете обработвания ключ от множеството. |
| 94 | Пропуснете ключ за вече отменена регистрация. |
| 95 | Готовият слушател означава нова връзка. |
| 96–98 | При данни за четене вземете `SelectorSession` от прикрепения обект и повикайте нейния `read`. Грешката затваря засегнатата връзка. |
| 101–103 | След обработка на готовите ключове задвижете всяка NIO сесия с `pump(chunk)`. Там се обработват и чакащите записи. |
| 107–109 | Неочаквана I/O грешка на целия цикъл се предава нагоре. При излизане се затварят всички останали сесии. |

## 2. Дайте на всяка NIO връзка собствени буфери

Първо упражнете сглобяването на приложен кадър с `plain`. В завършения защитен транспорт има още два буфера. `netIn` и `netOut` държат TLS данните върху канала, а `plain` получава дешифрираните байтове за LabNet.

[SelectorSession.java · редове 11–43](../chat-solution/src/labnet/transport/SelectorSession.java#L11-L43)

```java
/** All engine and buffer operations belong to the selector thread, except delegated handshake tasks. */
public final class SelectorSession extends Session {
    private static final ByteBuffer EMPTY = ByteBuffer.allocate(0);
    private final SocketChannel channel;
    private final SelectionKey key;
    private final SSLEngine engine;
    private final Executor handshakeTasks;
    private final ByteBuffer netIn, netOut, plain;
    private volatile boolean doingTask;
    private boolean established;
    private final long handshakeDeadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
    private long frameStarted;
    private Write flushed;
    private boolean readPaused;

    public SelectorSession(SocketChannel channel, Selector selector, SSLContext tls, Room room,
                           Consumer<Session> removed, Executor handshakeTasks, Semaphore handshakes) throws IOException {
        super(room, removed, handshakes);
        this.channel = channel;
        this.handshakeTasks = handshakeTasks;
        engine = tls.createSSLEngine();
        engine.setUseClientMode(false);
        engine.setEnabledProtocols(new String[]{"TLSv1.3"});
        engine.beginHandshake();
        int packet = engine.getSession().getPacketBufferSize();
        netIn = ByteBuffer.allocate(2 * packet);
        netOut = ByteBuffer.allocate(2 * packet);
        netOut.flip();
        plain = ByteBuffer.allocate(Frame.HEADER + Frame.MAX_PAYLOAD + engine.getSession().getApplicationBufferSize());
        key = channel.register(selector, SelectionKey.OP_READ, this);
    }
    public void read() throws IOException {
        if (!doingTask && !quitting && !readPaused && channel.read(netIn) < 0) close();
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 12–14 | Наследете общата сесия. Празен буфер ще служи при TLS договаряне. Каналът принадлежи на този клиент. |
| 15–18 | Ключът е регистрацията в селектора. TLS машината, изпълнителят за делегирани задачи и трите буфера са за тази сесия. |
| 19–21 | Отбелязвайте изпълняваща се TLS задача, приключило договаряне и краен момент три секунди след създаването. |
| 22–24 | `frameStarted` измерва чакането за незавършен кадър. `flushed` помни чий шифрован запис остава за довършване. `readPaused` ще спира входа при пълнеща се опашка. |
| 26–30 | Получете канала и ресурсите на общия сървър. Предайте общата част към `Session` и запазете канала и изпълнителя. |
| 31–34 | На етап 6 създайте по една TLS машина за връзка, задайте сървърен режим, TLS 1.3 и начало на договарянето. |
| 35–37 | Попитайте TLS машината за размера на пакет. Осигурете входен и изходен буфер с място за двоен такъв размер. |
| 38 | В началото няма шифровани байтове за писане. `flip()` при позиция 0 прави изходния буфер празен за четене. |
| 39 | Осигурете място за максимален LabNet кадър и за още една порция от TLS машината. |
| 40 | Регистрирайте интерес към четене и прикрепете `this`, за да намери цикълът точната сесия. |
| 42–43 | При готовност прочетете наличното в `netIn`, ако състоянието разрешава това. Отрицателен резултат означава край на потока. |

## 3. Сглобете кадъра, без да загубите недочетения остатък

Нека първо дойдат шестте байта на заглавието и само част от полезния товар. Ако консумирате заглавието окончателно, при следващото четене ще вземете байтове от тялото за версия и тип. Затова поставете отметка преди заглавието.

[SelectorSession.java · редове 45–60](../chat-solution/src/labnet/transport/SelectorSession.java#L45-L60)

```java
    private void decode() throws IOException {
        plain.flip();
        while (!closed && !quitting && plain.remaining() >= Frame.HEADER) {
            plain.mark();
            int version = plain.get() & 255, type = plain.get() & 255;
            int size = Frame.length(version, plain.getInt());
            if (plain.remaining() < size) { plain.reset(); break; }
            long id = plain.getLong();
            byte[] body = new byte[size - Long.BYTES];
            plain.get(body);
            dispatch(new Frame(type, id, body));
            frameStarted = 0;
        }
        plain.compact();
        if (plain.position() > 0 && frameStarted == 0) frameStarted = System.nanoTime();
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 46 | `flip()` подготвя събраните приложни байтове за четене. |
| 47 | Проверете дали сесията работи и има поне шест байта за заглавие. |
| 48 | `mark()` запомня точно началото на кандидат-кадъра. |
| 49 | Прочетете версия и тип като беззнакови стойности. |
| 50 | Приложете същата проверка за дължина като при блокиращото четене от упражнение 1. |
| 51 | Ако тялото е непълно, `reset()` връща позицията пред заглавието. Излезте от цикъла и изчакайте още данни. |
| 52–54 | За цял кадър прочетете идентификатора, заделете масив за останалите байтове и попълнете тялото. |
| 55–56 | Предайте целия кадър за обработка. Нулирайте времето за частичен кадър. |
| 58 | `compact()` премества неизползвания остатък в началото и оставя място за следващото дописване. |
| 59 | Ако има остатък, отбележете кога е започнало чакането за него. При следващо непълно четене запазете първоначалното време. |

## 4. Довършете записа преди да го отчетете

След като шифрованите байтове са в `netOut`, каналът може да приеме само част от тях. Този код пази позицията между две извиквания на `pump`. В упражнение 5 ще добавите контрол на опашката в началото на същия метод.

[SelectorSession.java · редове 87–100](../chat-solution/src/labnet/transport/SelectorSession.java#L87-L100)

```java
        for (int turn = 0; turn < 64 && !closed && !doingTask; turn++) {
            if (netOut.hasRemaining()) {
                int limit = netOut.limit();
                if (established) netOut.limit(netOut.position() + Math.min(chunk, netOut.remaining()));
                channel.write(netOut);
                netOut.limit(limit);
                if (netOut.hasRemaining()) break;
            }
            if (flushed != null) {
                Write complete = flushed;
                flushed = null;
                written(complete);
                if (closed) break;
            }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 87 | Дайте до 64 стъпки на едно задвижване. Спрете при затваряне или докато друг работник изпълнява TLS задача. |
| 88 | Проверете дали от предходната стъпка има шифровани байтове за запис. |
| 89 | Запомнете пълната граница на наличните данни. |
| 90 | За установена връзка временно ограничете видимия отрязък според `chunk`. |
| 91 | Каналът премества позицията с действително приетия брой байтове. Нулев запис също е възможен. |
| 92 | Възстановете пълната граница на буфера. Позицията продължава от действително записаното. |
| 93 | Ако има остатък, върнете управлението на цикъла. Следващото задвижване започва от същия буфер. |
| 95–97 | След пълно изпразване вземете запомнения запис и изчистете временната референция. |
| 98–99 | С `written` отчетете завършения запис. Това може да приключи сесията, например при `QUIT`. |

[SelectorSession.java · редове 124–134](../chat-solution/src/labnet/transport/SelectorSession.java#L124-L134)

```java
        interests();
    }
    private void interests() {
        if (closed || !key.isValid()) return;
        int ops = !doingTask && !quitting && !readPaused ? SelectionKey.OP_READ : 0;
        if (!doingTask && (netOut.hasRemaining() || (established && head() != null))) ops |= SelectionKey.OP_WRITE;
        key.interestOps(ops);
    }
    protected void wakeWriter() { key.selector().wakeup(); }
    protected void closeTransport() throws IOException { key.cancel(); channel.close(); }
}
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 124 | След обработката съгласувайте регистрирания интерес с текущото състояние. |
| 126–128 | При валиден ключ поискайте четене, ако няма TLS задача, излизане или пауза. |
| 129 | Поискайте писане, когато остават шифровани байтове или установената сесия има достъпен изходящ кадър. |
| 130 | Запишете получената комбинация от флагове в ключа. |
| 132 | От друга нишка може да се извика `wakeup()`, за да се върне селекторът от изчакване. |
| 133 | При затваряне отменете ключа и освободете канала. |

[ChatServer.java · редове 119–124](../chat-solution/src/labnet/ChatServer.java#L119-L124)

```java
                case "/io" -> { model = Model.valueOf(words[1].toUpperCase(Locale.ROOT)); return "New connections: " + model; }
                case "/chunk" -> {
                    int value = Integer.parseInt(words[1]);
                    if (value < 1 || value > 65536) throw new IllegalArgumentException("Chunk must be 1..65536");
                    chunk = value; return "NIO socket write limit=" + chunk;
                }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 119 | `/io` сменя избора за следващи връзки. Вече приетата сесия продължава със своя модел. |
| 120–123 | `/chunk` чете число, проверява диапазона 1…65536 и сменя максималния размер на единичния запис за установени NIO връзки. |

**Проверка.** Изберете `/io nio` на сървъра преди да отворите `/new ana`, `/new boris` и `/new iva`. На сървъра въведете `/chunk 3`, а от Ана изпратете `Чат`. Трите клиента трябва да видят целия текст по веднъж, а подателят получава `delivered=3;failed=0`. Върнете `/chunk 65536`. За автоматичната проверка изпълнете `java -cp out labnet.demo.Demo 4`.

[Съдържание на ръководството](../hint.md) · [Следващо упражнение — Бавен получател и ограничена работа](../lab05-backpressure/hint.md)
