# Упражнение 5 — Бавен получател и ограничена работа

**След частичния запис.** Нека Ива временно спре да чете. Ана и Борис трябва да продължат разговора, а сървърът да знае за кои публикации е приключил записът. Добавете обща изходяща опашка в `Session`, преди да въвеждате демонстрационното задържане и асинхронния `WORK`.

## 1. Опишете какво чака в опашката на един получател

[Session.java · редове 12–41](../chat-solution/src/labnet/transport/Session.java#L12-L41)

```java
/** Each connection has one writer. Completion means the encrypted record was written to the socket. */
public abstract class Session extends Room.Peer {
    public record Write(ByteBuffer bytes, int size, boolean event, CompletableFuture<Boolean> done) {}
    private final ArrayDeque<Write> queue = new ArrayDeque<>();
    private final Room room;
    private final Consumer<Session> removed;
    private final AtomicInteger inFlight = new AtomicInteger();
    private final AtomicBoolean handshaking = new AtomicBoolean(true);
    private final Semaphore handshakes;
    private int queuedBytes;
    private long lastId;
    private boolean slow;
    protected volatile boolean closed, quitting;

    protected Session(Room room, Consumer<Session> removed, Semaphore handshakes) {
        this.room = room; this.removed = removed; this.handshakes = handshakes;
    }
    protected void handshakeDone() { if (handshaking.compareAndSet(true, false)) handshakes.release(); }
    protected abstract void wakeWriter();
    protected abstract void closeTransport() throws IOException;
    public synchronized void slow(boolean hold) { slow = hold; notifyAll(); wakeWriter(); }
    protected synchronized Write head() {
        Write first = queue.peek();
        return slow && first != null && first.event ? null : first;
    }
    protected synchronized int queuedBytes() { return queuedBytes; }
    protected synchronized Write awaitHead() throws InterruptedException {
        while (!closed && head() == null) wait();
        return head();
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 13–14 | Всеки транспорт наследява `Room.Peer`. Един `Write` държи буфера, първоначалния му размер, дали е събитие и бъдещия резултат на този запис. |
| 15 | Пазете записите по ред на постъпване в двойно завършена опашка. |
| 16–17 | Сесията познава стаята и действието, което я премахва от набора на сървъра при край. |
| 18 | Бройте заявките, за които още няма завършен отговор. Различни нишки могат да приключат техните записи. |
| 19–20 | При TLS добавете флаг и общ семафор. Едно успешно или прекъснато договаряне трябва да освободи едно разрешение. |
| 21–24 | Отделно пазете байтовия бюджет, последния номер на заявка, флага за бавен получател и състоянието на приключване. |
| 26–27 | Конструкторът получава стаята и общите ресурси от сървъра. |
| 29 | `compareAndSet` гарантира, че само първото приключване на договарянето връща разрешение. |
| 30–31 | Транспортът определя как се събужда писателят и как се затваря физическата връзка. |
| 32 | При промяна на демонстрационния режим събудете чакащите за тази сесия. |
| 33–35 | Вземете първия запис без изваждане. Ако е задържано събитие, писателят временно не получава елемент. |
| 37 | Върнете отчетения размер под същото заключване като опашката. |
| 38–40 | Блокиращият писател изчаква в цикъл за достъпен запис или затваряне. След събуждане условието се проверява отново. |

## 2. Добавете две различни операции — приемане за запис и завършен запис

В `send` върнете незавършен резултат веднага след поставянето в опашката. В `written` го завършете едва след изписване на кадъра. Така броячът в `Room.publish` може да отчете какво действително е станало при всеки получател.

[Session.java · редове 42–66](../chat-solution/src/labnet/transport/Session.java#L42-L66)

```java
    public CompletableFuture<Boolean> send(Frame frame) {
        ByteBuffer encoded = frame.encode();
        synchronized (this) {
            if (closed) return CompletableFuture.completedFuture(false);
            if (queuedBytes + encoded.remaining() <= 256 * 1024 && queue.size() < (slow ? 2 : 64)) {
                var item = new Write(encoded, encoded.remaining(), frame.type() == Frame.EVENT, new CompletableFuture<>());
                queue.addLast(item);
                queuedBytes += item.size;
                notifyAll();
                wakeWriter();
                return item.done;
            }
        }
        room.rejected();
        close();
        return CompletableFuture.completedFuture(false);
    }
    protected void written(Write item) {
        synchronized (this) {
            if (closed || queue.peek() != item) return;
            queue.removeFirst();
            queuedBytes -= item.size;
        }
        item.done.complete(true);
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 42–43 | Кодирайте кадъра в собствен буфер на получателя. Частичният запис при Ива не променя позицията при Борис. |
| 44–45 | Заключете опашката. Ако сесията е затворена, върнете завършен отказ. |
| 46 | Преди добавяне проверете и двата лимита — 256 KiB и 64 кадъра. В режима за бавен получател лимитът е два кадъра. |
| 47 | Запомнете пълния размер и създайте отделен обект за потвърждението. |
| 48–49 | Добавете в края и увеличете отчетените байтове със същия размер. |
| 50–51 | Събудете чакащия блокиращ писател или селектора според транспорта. |
| 52 | Върнете обещания резултат, чрез който стаята ще получи изхода от записа. |
| 55–57 | Ако лимитът е достигнат, отчетете отказ, затворете тази сесия и върнете `false` за новия кадър. |
| 59–61 | Писателят завършва само точно този елемент, който все още е начело на отворена сесия. |
| 62–63 | Отстранете елемента и освободете целия отчетен размер. Бюджетът тук се освобождава на цели кадри. |
| 65 | Завършете резултата с `true` след излизане от заключването. Изчакващите продължения могат да подадат следващи кадри. |

[Session.java · редове 67–98](../chat-solution/src/labnet/transport/Session.java#L67-L98)

```java
    protected void dispatch(Frame frame) {
        if (frame.id() <= lastId || frame.type() == Frame.EVENT) { close(); return; }
        lastId = frame.id();
        int outstanding = inFlight.incrementAndGet();
        if (outstanding > 4) { close(); return; }
        if (frame.type() == Frame.QUIT && frame.body().length == 0) {
            if (outstanding > 1) {
                send(new Frame(Frame.ERROR, frame.id(), Frame.bytes("IN_FLIGHT"))).thenRun(inFlight::decrementAndGet);
                return;
            }
            quitting = true;
        }
        room.handle(this, frame).whenComplete((ok, ex) -> inFlight.decrementAndGet());
    }
    public void close() {
        List<Write> abandoned;
        synchronized (this) {
            if (closed) return;
            closed = true;
            abandoned = List.copyOf(queue);
            queue.clear();
            queuedBytes = 0;
            notifyAll();
        }
        room.leave(this);
        handshakeDone();
        try { closeTransport(); } catch (IOException ignored) {}
        abandoned.forEach(item -> item.done.complete(false));
        removed.accept(this);
        wakeWriter();
    }
}
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 67–69 | Заявката трябва да има строго нарастващ положителен номер и допустима посока. Клиентски `EVENT` или повторен номер затварят сесията. |
| 70–71 | Увеличете броя на текущите заявки. Петата едновременна заявка надвишава лимита. |
| 72–75 | Ако `QUIT` идва преди по-ранни отговори, върнете `IN_FLIGHT` и отчетете приключването на отказаната заявка. |
| 77 | При допустим `QUIT` спрете приемането на следващи кадри. |
| 79 | Нормалната заявка отива в стаята. Намалете брояча при завършване на обработката и записа на отговора. |
| 81–85 | Второ затваряне приключва веднага. Първото маркира сесията като затворена под заключване. |
| 86–89 | Направете снимка на незавършените записи, изпразнете опашката, нулирайте бюджета и събудете чакащите. |
| 91–93 | Изведете участника от стаята, освободете евентуалното TLS разрешение и затворете транспорта. |
| 94 | Завършете всеки изоставен запис с `false`. Иначе изпращачите биха останали да чакат потвърждения. |
| 95–96 | Премахнете сесията от глобалния набор и събудете писателя за приключване. |

[SelectorSession.java · редове 80–87](../chat-solution/src/labnet/transport/SelectorSession.java#L80-L87)

```java
    public void pump(int chunk) throws IOException {
        if (closed) return;
        if (queuedBytes() >= 128 * 1024) readPaused = true;
        else if (queuedBytes() <= 64 * 1024) readPaused = false;
        long now = System.nanoTime();
        if ((!established && now > handshakeDeadline) ||
                (frameStarted != 0 && now - frameStarted > TimeUnit.SECONDS.toNanos(2))) { close(); return; }
        for (int turn = 0; turn < 64 && !closed && !doingTask; turn++) {
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 80–81 | При всяко задвижване първо проверете дали сесията е отворена. |
| 82 | При 128 KiB чакащ изход поставете входното четене на пауза. |
| 83 | Възстановете четенето едва при 64 KiB или по-малко. Двата прага избягват непрекъснато превключване около една граница. |
| 84–86 | Използвайте текущото монотонно време и прекратете просрочено TLS договаряне или частичен приложен кадър на повече от две секунди. |
| 87 | Ограничете стъпките в едно задвижване, за да получат време и другите връзки. |

## 3. Направете бавния получател възпроизводим

Свържете `/slow iva` в конзолата на сървъра с `Session.slow`. След това добавете в клиента три изпращания без междинно чакане. Ако Ана чака първия отговор преди втората публикация, задържаната опашка на Ива няма да достигне третия кадър.

[ChatServer.java · редове 125–133](../chat-solution/src/labnet/ChatServer.java#L125-L133)

```java
                case "/slow" -> {
                    boolean found = false;
                    for (Session peer : sessions) {
                        boolean match = words[1].equals(peer.name);
                        peer.slow(match);
                        found |= match;
                    }
                    return found || words[1].equals("off") ? "Slow: " + words[1] : "NO_SUCH_USER";
                }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 125–127 | Обходете приетите сесии и пазете дали е намерено исканото име. |
| 128–129 | Сравнете името на конкретния участник и задайте задържане само за съвпадащата сесия. |
| 130 | Натрупайте резултата от търсенето. Положително съвпадение трябва да остане запомнено и при следващи несъвпадения. |
| 132 | Върнете състоянието или `NO_SUCH_USER`. Командата `/slow off` освобождава задържането при обичайните демонстрационни имена. |

[ChatClient.java · редове 119–123](../chat-solution/src/labnet/ChatClient.java#L119-L123)

```java
            case "/burst" -> {
                var replies = new ArrayList<CompletableFuture<Frame>>();
                for (int i = 1; i <= 3; i++) replies.add(chat.submit(BROADCAST, bytes("burst-" + i)));
                for (var reply : replies) show(reply.get(8, TimeUnit.SECONDS));
            }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 119–120 | `/burst` събира три бъдещи отговора. |
| 121 | С `submit` подайте `burst-1`, `burst-2` и `burst-3` в една и съща връзка. |
| 122 | Едва сега изчакайте всеки отговор до осем секунди и го покажете. |

**Проследете един отказ.** При трима влезли участници задръжте Ива. Първата и втората публикация остават в нейната опашка. Третата не се побира, затова `close()` отбелязва и първите две като неуспешни за Ива. Ана и Борис получават всички три. Очаквайте три отговора `delivered=2;failed=1`. При следващата публикация снимката на стаята вече включва само двама и резултатът е `delivered=2;failed=0`.

## 4. Изнесете бавната приложна работа в отделен пул

Добавете `WORK` към типа кадър и в `Room.handle`. Клиентът задава цена 0…50 ms и текст. Селекторът трябва да може да обслужи `PING` от друг клиент, докато работникът изчаква.

[Room.java · редове 27–27](../chat-solution/src/labnet/Room.java#L27-L27)

```java
    private final ThreadPoolExecutor workers = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(64));
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 27 | Четири работника ползват опашка за 64 чакащи задачи. Ограничението е общо за стаята. |

[ChatClient.java · редове 114–118](../chat-solution/src/labnet/ChatClient.java#L114-L118)

```java
            case "/work" -> {
                int ms = Integer.parseInt(name);
                byte[] data = args.length > 2 ? bytes(args[2]) : EMPTY;
                show(chat.request(WORK, ByteBuffer.allocate(4 + data.length).putInt(ms).put(data).array()));
            }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 114–115 | След `/work` първият аргумент е целочислена продължителност в милисекунди. |
| 116 | Останалата част на реда става данни. При липса на текст тялото след числото е празно. |
| 117 | Заделете четири байта за `int` и място за текста. Изпратете първо числото, после текстовите байтове. |

[Room.java · редове 135–148](../chat-solution/src/labnet/Room.java#L135-L148)

```java
    private CompletableFuture<Boolean> work(Peer peer, long id, byte[] body) {
        if (body.length < 4 || body.length > 4100) return error(peer, id, "BAD_PAYLOAD");
        int millis = ByteBuffer.wrap(body).getInt();
        if (millis < 0 || millis > 50) return error(peer, id, "BAD_PAYLOAD");
        try {
            return CompletableFuture.supplyAsync(() -> {
                try { Thread.sleep(millis); } catch (InterruptedException end) { Thread.currentThread().interrupt(); }
                return new Frame(DATA, id, Arrays.copyOfRange(body, 4, body.length));
            }, workers).thenCompose(peer::send);
        } catch (RejectedExecutionException full) {
            rejected();
            return error(peer, id, "BUSY");
        }
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 135–136 | Изисквайте поне четири байта за числото и до 4096 байта след него. |
| 137–138 | Разчетете продължителността като big-endian `int`. Число извън 0…50 получава `BAD_PAYLOAD`. |
| 140 | Подайте операцията асинхронно към изпълнителя. |
| 141 | Работникът симулира закъснението. При прекъсване възстановява флага на нишката. |
| 142 | Върнете `DATA` със същия идентификатор и само оригиналните байтове след първите четири. |
| 143 | Изберете пула `workers`. Подайте получения кадър към изходящата опашка на същия участник. |
| 144–146 | При отказ от пълен или спрян пул увеличете брояча и изпратете `BUSY`. |

**Проверка.** На сървъра въведете `/slow iva`, а от Ана `/burst` при трима влезли участници. След отпадането на Ива изпратете `/work 30 Чат`. Отговорът съдържа `DATA` и „Чат“. При `/work 51 Чат` очаквайте `BAD_PAYLOAD`. Автоматичната проверка на бавния получател е `java -cp out labnet.demo.Demo 5`.

[Съдържание на ръководството](../hint.md) · [Следващо упражнение — TLS, доверие и вход по име](../lab06-tls-security/hint.md)
