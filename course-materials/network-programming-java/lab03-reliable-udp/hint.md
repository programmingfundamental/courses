# Упражнение 3 — UDP брояч с повторно потвърждение

**След първия разговор.** Добавете отчет колко чат събития е показала Ива. Ако UDP потвърждението се изгуби, клиентът ще изпрати повторно същата операция. Сървърът трябва да върне същата сума. Работете в `labnet.udp.Counter`. Съществуващият чат продължава да пренася текста по TCP.

## 1. Задайте границите на една дейтаграма

Добавете вложения запис `Packet` преди изпращача и приемника. И двата ще го използват. При `delta=+1` дейтаграмата съдържа 19 байта заглавие и осем байта тяло.

[Counter.java · редове 11–29](../chat-solution/src/labnet/udp/Counter.java#L11-L29)

```java
public final class Counter {
    private static final int HEADER = 19, DATA = 1, ACK = 2, ERROR = 3;
    private record Packet(long session, long seq, int type, byte[] body) {
        byte[] encode() {
            return ByteBuffer.allocate(HEADER + body.length).putLong(session).putLong(seq)
                    .put((byte) type).putShort((short) body.length).put(body).array();
        }
        static Packet decode(DatagramPacket datagram) {
            if (datagram.getLength() < HEADER || datagram.getLength() > 1200) return null;
            var buffer = ByteBuffer.wrap(datagram.getData(), datagram.getOffset(), datagram.getLength());
            long session = buffer.getLong(), seq = buffer.getLong();
            int type = buffer.get() & 255, size = Short.toUnsignedInt(buffer.getShort());
            if (session < 1 || seq < 1 || size != buffer.remaining()) return null;
            byte[] body = new byte[size];
            buffer.get(body);
            return new Packet(session, seq, type, body);
        }
    }
    private static byte[] number(long value) { return ByteBuffer.allocate(8).putLong(value).array(); }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 11–12 | Общият клас съдържа числата за DATA, ACK и ERROR. Заглавието е 8 + 8 + 1 + 2 = 19 байта. |
| 13–14 | Запишете идентификатора на сесията, поредния номер, типа и тялото в една стойност. |
| 15–16 | Запишете двата номера като `long`, типа като байт и дължината като `short`. Прикрепете тялото и върнете готовия масив. |
| 18–19 | При получаване проверете границите 19…1200 байта преди разчитане на полетата. |
| 20 | Обвийте точно използвания отрязък от буфера според отместването и размера на дейтаграмата. |
| 21 | Прочетете сесията и номера в реда, използван от кодера. |
| 22 | Възстановете типа и дължината като беззнакови числа. |
| 23 | Изисквайте положителни номера и точно съвпадение между обявения размер и останалите байтове. |
| 24–26 | Копирайте тялото в собствен масив и върнете декодирания пакет. |
| 29 | Използвайте един начин за кодиране на числата в DATA и ACK — осембайтов `long`. |

## 2. Добавете приемника в сървърния процес

Пазете по едно състояние за всяка комбинация от адрес, порт на изпращача и номер на сесията. Всички повторения от един клиент трябва да достигат едно и също състояние.

[Counter.java · редове 31–58](../chat-solution/src/labnet/udp/Counter.java#L31-L58)

```java
    public static final class Receiver implements AutoCloseable {
        private record Key(SocketAddress source, long session) {}
        private record State(long seq, long total, long lastSeen) {}
        private final Map<Key, State> sessions = new HashMap<>();
        private final DatagramSocket socket;
        private final Thread thread;
        private volatile int dropAck;
        public void dropAck(int seq) { dropAck = seq; }
        public Receiver(int port, int dropAck) throws IOException {
            this.dropAck = dropAck;
            socket = new DatagramSocket(new InetSocketAddress(InetAddress.getLoopbackAddress(), port));
            thread = Thread.ofPlatform().name("udp-counter").start(this::run);
        }
        public int port() { return socket.getLocalPort(); }
        private void run() {
            byte[] buffer = new byte[1201];
            try {
                while (!socket.isClosed()) {
                    var datagram = new DatagramPacket(buffer, buffer.length);
                    socket.receive(datagram);
                    var packet = Packet.decode(datagram);
                    if (packet == null || packet.type != DATA || packet.body.length != 8) continue;
                    long delta = ByteBuffer.wrap(packet.body).getLong();
                    if (delta < -100 || delta > 100) continue;
                    long now = System.nanoTime();
                    sessions.values().removeIf(state -> now - state.lastSeen > TimeUnit.SECONDS.toNanos(60));
                    var key = new Key(datagram.getSocketAddress(), packet.session);
                    State previous = sessions.get(key);
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 31–34 | `Key` свързва източника със сесията. `State` пази последния изпълнен номер, сумата и последната активност. Една нишка управлява картата. |
| 35–38 | Сокетът получава пакетите, а `volatile dropAck` позволява на сървърната конзола да зададе кой ACK да се пропусне. |
| 39–42 | Задайте началното правило, отворете UDP порта на loopback и стартирайте нишката `udp-counter`. |
| 44 | Върнете избрания от системата порт, ако при тест е подаден 0. |
| 45–46 | Заделете 1201 байта. Допълнителният байт позволява декодерът да разпознае получено съобщение над лимита 1200. |
| 48–50 | За всеки прием подгответе пакет с пълния капацитет и изчакайте една дейтаграма. |
| 51–52 | Продължете само при валиден DATA пакет с осембайтово тяло. |
| 53–54 | Прочетете прираста и разрешете стойности от −100 до 100. Наблюдателят на чата подава +1. |
| 55–56 | Използвайте монотонно време и отстранете състоянията с повече от 60 секунди неактивност. |
| 57–58 | Намерете последния резултат на конкретния изпращач за тази UDP сесия. |

[Counter.java · редове 59–79](../chat-solution/src/labnet/udp/Counter.java#L59-L79)

```java
                    String error = null;
                    if (previous == null && packet.seq != 1) error = "SESSION_EXPIRED";
                    else if (previous == null && sessions.size() >= 32) error = "SERVER_BUSY";
                    else if (previous != null && packet.seq < previous.seq) continue;
                    else if (previous != null && packet.seq > previous.seq + 1) error = "BAD_SEQUENCE";
                    Packet reply;
                    if (error != null) reply = new Packet(packet.session, packet.seq, ERROR, Frame.bytes(error));
                    else {
                        boolean fresh = previous == null || packet.seq != previous.seq;
                        long total = previous == null ? 0 : previous.total;
                        if (fresh) total = Math.addExact(total, delta);
                        sessions.put(key, new State(packet.seq, total, now));
                        if (fresh && packet.seq == dropAck) continue;
                        reply = new Packet(packet.session, packet.seq, ACK, number(total));
                    }
                    byte[] data = reply.encode();
                    socket.send(new DatagramPacket(data, data.length, key.source));
                }
            } catch (IOException ex) { if (!socket.isClosed()) throw new UncheckedIOException(ex); }
        }
        public void close() throws InterruptedException { socket.close(); thread.join(); }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 59–60 | Нова сесия трябва да започне с номер 1. При забравена сесия върнете `SESSION_EXPIRED`. |
| 61 | Ако вече има 32 сесии, откажете нова със `SERVER_BUSY`. |
| 62 | По-старо копие се пропуска. |
| 63 | Номер, прескачащ следващата операция, получава `BAD_SEQUENCE`. |
| 64–65 | При избрана грешка съставете ERROR със същата сесия и номер. |
| 67 | Операцията е нова при първи пакет или при следващ номер след проверките. |
| 68 | Започнете от нула или от запомнената сума. |
| 69 | Приложете прираста само при нова операция. `Math.addExact` засича числово препълване. |
| 70 | Запишете резултата преди опита за потвърждение. Именно този ред прави възможно повторението при изгубен ACK. |
| 71 | При демонстрационния номер пропуснете първия ACK. При повторение `fresh` вече е `false`. |
| 72 | Съставете ACK със запазената сума. При дублирания пакет операцията от ред 69 е пропусната. |
| 74–75 | Кодирайте отговора и го изпратете обратно към източника. |
| 77–79 | Затварянето на сокета прекъсва `receive`. Изчакайте нишката с `join`. Неочаквана I/O грешка при отворен сокет се предава нагоре. |

## 3. Изпратете една операция и повторете само при липса на ACK

Първо изградете DATA пакета. После го изпращайте в цикъла за опити. Ако увеличите `seq` вътре в този цикъл, сървърът ще възприеме повторението като ново показано съобщение.

[Counter.java · редове 82–111](../chat-solution/src/labnet/udp/Counter.java#L82-L111)

```java
    public record Receipt(long seq, long total, int attempts) {}
    public static final class Sender implements AutoCloseable {
        private final DatagramSocket socket;
        private final long session = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        private long seq;
        public Sender(String host, int port) throws IOException {
            socket = new DatagramSocket();
            socket.connect(InetAddress.getByName(host), port);
        }
        public synchronized Receipt increment() throws IOException {
            byte[] data = new Packet(session, ++seq, DATA, number(1)).encode();
            for (int attempt = 1; attempt <= 5; attempt++) {
                socket.send(new DatagramPacket(data, data.length));
                int delay = Math.min(1600, 200 << (attempt - 1));
                long until = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(delay);
                for (long remaining; (remaining = until - System.nanoTime()) > 0;) {
                    socket.setSoTimeout((int) TimeUnit.NANOSECONDS.toMillis(remaining) + 1);
                    var datagram = new DatagramPacket(new byte[1201], 1201);
                    try { socket.receive(datagram); } catch (SocketTimeoutException timeout) { break; }
                    var packet = Packet.decode(datagram);
                    if (packet == null || packet.session != session || packet.seq != seq) continue;
                    if (packet.type == ERROR) throw new IOException(Frame.text(packet.body));
                    if (packet.type == ACK && packet.body.length == 8)
                        return new Receipt(seq, ByteBuffer.wrap(packet.body).getLong(), attempt);
                }
            }
            close();
            throw new IOException("UNKNOWN_OUTCOME seq=" + seq);
        }
        public void close() { socket.close(); }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 82–86 | `Receipt` пази номера, сумата и броя опити. Всеки `Sender` има собствен сокет, случаен положителен идентификатор и локална номерация. |
| 87–89 | Отворете UDP сокет и задайте адреса на приемника. Това избира партньора за дейтаграмите. |
| 91–92 | Сериализирайте подаването от този изпращач. Увеличете `seq` веднъж и кодирайте DATA с прираст 1. |
| 93–94 | Разрешете до пет изпращания на вече получения масив от байтове. |
| 95 | Изчакайте съответно 200, 400, 800, 1600 и 1600 ms според номера на опита. |
| 96–98 | Задайте краен момент за опита. Преди всеки `receive` пресметнете оставащите милисекунди и добавете 1, за да избегнете нулев таймаут при закръгляне. |
| 99–100 | Приемете възможния ACK. Изтеклото време води до следващ опит със същия DATA пакет. |
| 101–102 | Декодирайте отговора. Чужд номер или друга сесия продължават изчакването в същия срок. |
| 103 | Предайте изричната сървърна грешка към наблюдателя. |
| 104–105 | При ACK с осембайтова сума върнете резултата и действителния брой опити. |
| 108–109 | След пет неуспешни опита затворете сокета и сигнализирайте `UNKNOWN_OUTCOME`. Последният прираст може вече да е приложен. |
| 111 | При край освободете сокета на изпращача. |

## 4. Свържете брояча към показването на събитие

Добавете `Monitor` след работещия обмен DATA → ACK. Четецът на чата трябва да може да покаже следващ ред, докато предишната UDP операция изчаква повторение.

[Counter.java · редове 114–132](../chat-solution/src/labnet/udp/Counter.java#L114-L132)

```java
    // One worker keeps UDP retry delays off the chat reader thread.
    public static final class Monitor implements AutoCloseable {
        private final Sender sender;
        private final Consumer<Receipt> report;
        private final ThreadPoolExecutor worker = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(64));
        public Monitor(String host, int port, Consumer<Receipt> report) throws IOException {
            sender = new Sender(host, port);
            this.report = report;
        }
        public void displayed() {
            try {
                worker.execute(() -> {
                    try { report.accept(sender.increment()); }
                    catch (IOException ex) { System.err.println("UDP: " + ex.getMessage()); }
                });
            } catch (RejectedExecutionException full) { System.err.println("UDP sample rejected"); }
        }
        public void close() { worker.close(); sender.close(); }
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 115–118 | Наблюдателят притежава изпращач, функция за показване на ACK и един работник с опашка от 64 задачи. |
| 119–121 | Създайте изпращача за указаните адрес и порт и запазете функцията за докладване. |
| 123–126 | При показан ред подайте задача към работника. Той изчаква `increment()`, после предава получения `Receipt` за печат. |
| 127 | Грешката на UDP измерването се отпечатва в тази задача. Четецът на TCP чата продължава. |
| 129 | При пълна опашка сигнализирайте отказана проба. В такъв опит броячът може да е по-малък от броя показани редове. |
| 131 | Изчакайте работника чрез `close` и затворете неговия сокет. |

[ChatServer.java · редове 39–45](../chat-solution/src/labnet/ChatServer.java#L39-L45)

```java
        listener = ServerSocketChannel.open();
        try {
            counter = new Counter.Receiver(udpPort, 0);
        } catch (IOException failure) {
            listener.close(); selector.close(); room.close();
            throw failure;
        }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 39–41 | Отварянето на мрежовия канал е част от конструктора на общия сървър. На ред 41 добавете UDP приемника с избрания порт и начална стойност 0 за пропускане на ACK. |
| 42–44 | Ако UDP портът не може да бъде отворен, освободете вече създадените ресурси и предайте грешката. |

[ChatServer.java · редове 134–138](../chat-solution/src/labnet/ChatServer.java#L134-L138)

```java
                case "/drop-ack" -> {
                    int seq = Integer.parseInt(words[1]);
                    if (seq < 0) throw new IllegalArgumentException("SEQ must be >= 0");
                    counter.dropAck(seq); return "Drop first UDP ACK at seq=" + seq;
                }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 134–135 | Прочетете номера за `/drop-ack` от втория аргумент. |
| 136 | Отхвърлете отрицателна стойност. |
| 137 | Задайте тестовото правило към съществуващия приемник. Стойност 0 го изключва, защото реалните номера започват от 1. |

В `ChatClient.open` добавете по един `Monitor` на ред 36. На редове 39–41 първо отпечатайте текста и чак след това извикайте `displayed()`. Така и тримата участници в една клиентска конзола имат собствени числа. [ChatClient.java · редове 35–46](../chat-solution/src/labnet/ChatClient.java#L35-L46)

**Проверка.** На сървъра въведете `/drop-ack 3`. В нови клиентски сесии Ана изпраща три реда след влизането на Ива. При Ива третият текст се показва веднъж и третият UDP резултат е `Receipt[seq=3, total=3, attempts=2]`. Върнете `/drop-ack 0`. Автоматичният опит е `java -cp out labnet.demo.Demo 3`. Броячът се отнася за тази измервателна сесия. След повече от 60 секунди неактивност приемникът може да е забравил предишния номер.

[Съдържание на ръководството](../hint.md) · [Следващо упражнение — Частично четене и запис с Java NIO](../lab04-java-nio/hint.md)
