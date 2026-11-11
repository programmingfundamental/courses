---
title: "Общ проект — многопотребителски чат с Java"
sidebar:
  label: Общ проект — чат
---

# Общ проект — многопотребителски чат с Java

Изградете конзолен клиент и сървър за една обща стая. Когато Ана напише „Здравейте!“, текстът трябва да се появи точно веднъж при Ана и при всеки друг участник в стаята. Сървърът също може да публикува съобщение, например „Занятието започва“. Клиент, който само чете, вижда разговора веднага, без да натиска Enter. Предайте две приложения — един `ChatServer` за всички упражнения и един `ChatClient`, който може да отваря няколко независими връзки.

За да изпълните решението, започнете от [създаването на проекта в IntelliJ](#chat-setup), добавете [класовете в посочения ред](#chat-code) и направете [демонстрацията с три клиента](#chat-demo). За да проследите какво добавя всяко от седемте упражнения, започнете с [първия TCP кадър](#lab-1).

## Договор на стаята

- **Влизане.** Клиентът отваря TCP връзка и подава `LOGIN` с уникално име по правилата на LabNet (`[A-Za-z0-9_-]{1,24}`). След успешен вход участва във всички следващи публикации. Заето име получава `NAME_IN_USE`. Запазете името `server` за конзолата на сървъра.
- **Публикуване.** Всеки обикновен ред от клиентската конзола става заявка `BROADCAST` с текст в UTF-8, от 1 до 4096 **байта**. Сървърът взема името на автора от сесията. Ред от сървърната конзола следва същите правила за текст и се разпраща с автор `server`.
- **Получатели.** При приемане на публикацията сървърът определя участниците в този момент, включително подателя. Към всеки изпраща по един `EVENT` с `requestId=0`, име на автора и текст. Клиентът показва реда като `[автор] текст` само при получен `EVENT`. Така и подателят вижда едно копие. Нов участник започва с първата публикация след своя вход.
- **Потвърждение.** Подателят получава отделен `DATA` с идентификатора на заявката и тяло `delivered=N;failed=M`. Броячите отчитат за колко от избраните сесии записът на целия кадър е завършил локално и за колко е отказан или прекъснат. За да докажете получаване, проверете и какво е прочел всеки клиент. При публикация от сървърната конзола покажете броячите там.
- **Излизане и грешки.** Командата `/quit` изпраща `QUIT`. При нормално излизане или прекъсната връзка сървърът освобождава сесията и името. Останалите продължават разговора. В TCP версията `BROADCAST` преди вход получава `NOT_LOGGED_IN`. Празен, прекалено дълъг или невалидно кодиран текст в иначе валиден кадър получава `BAD_PAYLOAD`. При тези откази никой не получава събитие.

<a id="chat-setup"></a>

## Решен пример — създайте проекта в IntelliJ IDEA

Ще направите един сървър, който пази участниците в обща стая, и една клиентска програма, която може да отвори три отделни връзки. Когато Ана изпрати текст, Борис и Ива трябва да го видят, дори докато техните клиенти чакат въвеждане от клавиатурата.

1. Изберете **File → New → Project**, **Java**, име `chat-solution`, система за изграждане **IntelliJ** и **JDK 21**. Ако е включена опцията **Add sample code**, изключете я. Натиснете **Create**.
2. В прозореца **Project** намерете папката `src`. Ако липсва, създайте я чрез **New → Directory** в корена на проекта. С десен бутон върху `src` изберете **Mark Directory as → Sources Root**, ако още не е маркирана. До нея създайте `test` и я маркирайте като **Test Sources Root**.
3. В `src` използвайте **New → Package**, за да създадете `labnet`. След това създайте пакетите `labnet.blocking`, `labnet.udp`, `labnet.tls`, `labnet.transport` и `labnet.demo`. В `test` създайте отделен пакет `labnet.demo` за автоматичната проверка.
4. За всеки файл по-долу изберете **New → Java Class** в посочения пакет и въведете името без `.java`. Разгънете **Пълен код** и заменете цялото автоматично създадено съдържание с показания код, включително първия ред `package`. Така можете да създадете и `Frame`, чиято декларация в примера е `record`. Оставете вътрешните класове и записи във файла, в който ги виждате.
5. След последния файл изберете **Build → Build Project**. За вече изтеглено копие на курса можете да започнете направо от тази стъпка. В **File → Open** изберете папката `course-materials/network-programming-java/chat-solution` и задайте JDK 21 като **Project SDK**.

<a id="chat-code"></a>

## Създайте класовете стъпка по стъпка

Следвайте реда от 1 до 14, за да можете да компилирате след всяка стъпка. Името на пакета в началото на файла определя мястото му под `src` или `test`. Първите три файла са общи за клиента и сървъра.

| Упражнение | Къде да проследите промяната в кода |
|---|---|
| [1. TCP кадър](#lab-1) | В стъпка 1 сравнете реда на полетата във `Frame.encode()` и `Frame.read()`. |
| [2. Обща стая](#lab-2) | В стъпки 4, 5 и 13 проследете един ред от клавиатурата до `Room.publish()` и обратно до трите получателя. |
| [3. Надежден UDP брояч](#lab-3) | В стъпки 6 и 13 свържете извеждането на един `EVENT` с `Monitor.displayed()` и номера на UDP операцията. |
| [4. Java NIO](#lab-4) | В стъпки 10 и 11 вижте как селекторът приема нова връзка и как буферът пази непълния кадър. |
| [5. Ограничени опашки](#lab-5) | В стъпка 8 проследете какво става с третия чакащ кадър след сървърната команда `/slow iva` и клиентската `/burst`. |
| [6. TLS и вход](#lab-6) | В стъпки 5, 7, 9 и 10 проследете проверката на сертификата, а в стъпки 13 и 4 — изпращането и обработката на `AUTH` и `LOGIN`. |
| [7. Производителност](#lab-7) | В стъпки 11, 12 и 14 сменете модела за нови връзки и сравнете броя публикации с броя действително прочетени събития. |

### 1. Frame — представете едно съобщение като байтове

В `src`, пакет `labnet`, създайте `Frame`. Записът съдържа типа на командата, идентификатора на заявката и тялото. Проследете реда на операциите в `encode()`. `putInt(length)` записва размера на идентификатора и тялото, `putLong(id)` записва номера на заявката, а `flip()` подготвя буфера за четене. В обратната посока `readFully(body)` изчаква всички байтове на тялото. Вътрешният запис `Named` добавя име към текста на събитието. За „Чат“ трябва да получите 6 байта текст и 20 байта за целия кадър.

<details>
<summary>Пълен код на <code>src/labnet/Frame.java</code></summary>

```java title="src/labnet/Frame.java"
package labnet;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

public record Frame(int type, long id, byte[] body) {
    public static final int PING = 1, ECHO = 2, QUIT = 3, LOGIN = 0x10, BROADCAST = 0x12,
            SEND = 0x11, USERS = 0x13, STATS = 0x14, AUTH = 0x15, WORK = 0x20,
            OK = 0x80, DATA = 0x81, EVENT = 0x82, ERROR = 0xff;
    public static final int HEADER = 6, MAX_PAYLOAD = 65536;
    public static final byte[] EMPTY = {};

    public static byte[] bytes(String text) { return text.getBytes(StandardCharsets.UTF_8); }

    public static String text(byte[] data) throws CharacterCodingException {
        return StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(data)).toString();
    }

    public ByteBuffer encode() {
        int length = Long.BYTES + body.length;
        if (length > MAX_PAYLOAD) throw new IllegalArgumentException("Frame too large");
        return ByteBuffer.allocate(HEADER + length).put((byte) 1).put((byte) type)
                .putInt(length).putLong(id).put(body).flip();
    }

    public static int length(int version, int length) throws IOException {
        if (version != 1 || length < Long.BYTES || length > MAX_PAYLOAD)
            throw new IOException("Invalid LabNet header");
        return length;
    }

    public static Frame read(DataInputStream in) throws IOException {
        return read(in.readUnsignedByte(), in);
    }
    public static Frame read(int version, DataInputStream in) throws IOException {
        int type = in.readUnsignedByte();
        int size = length(version, in.readInt());
        long id = in.readLong();
        byte[] body = new byte[size - Long.BYTES];
        in.readFully(body);
        return new Frame(type, id, body);
    }

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

</details>

### 2. Options — прочетете адреса и портовете

В `src`, пакет `labnet`, създайте `Options`. Конструкторът обхожда аргументите по двойки. При `--port 9000` той запомня ключа `port` и стойността `9000`. Методът `number()` преобразува текста в число и проверява диапазона. По-късно и двата метода `main()` ще използват този клас.

<details>
<summary>Пълен код на <code>src/labnet/Options.java</code></summary>

```java title="src/labnet/Options.java"
package labnet;

import java.util.*;

public final class Options {
    private final Map<String, String> values = new HashMap<>();
    public Options(String[] args) {
        if (args.length % 2 != 0) throw new IllegalArgumentException("Expected --option value pairs");
        for (int i = 0; i < args.length; i += 2) {
            if (!args[i].startsWith("--")) throw new IllegalArgumentException(args[i]);
            values.put(args[i].substring(2), args[i + 1]);
        }
    }
    public String get(String name, String fallback) { return values.getOrDefault(name, fallback); }
    public int number(String name, int fallback, int min, int max) {
        int value = Integer.parseInt(get(name, Integer.toString(fallback)));
        if (value < min || value > max) throw new IllegalArgumentException("--" + name + " outside " + min + ".." + max);
        return value;
    }
}
```

</details>

### 3. ConsoleIO — въведете и покажете ред на кирилица

В `src`, пакет `labnet`, създайте `ConsoleIO`. Методът `input()` предоставя четец за цели редове. В конзолата на IntelliJ, когато `System.console()` е `null`, той чете от `System.in` с UTF-8. Съответният клон на `line()` отпечатва в UTF-8. По-късно същите два метода ще показват „Здравейте!“ и в двете приложения.

<details>
<summary>Пълен код на <code>src/labnet/ConsoleIO.java</code></summary>

```java title="src/labnet/ConsoleIO.java"
package labnet;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class ConsoleIO {
    private static final PrintWriter UTF8_OUTPUT = new PrintWriter(System.out, true, StandardCharsets.UTF_8);
    private ConsoleIO() {}
    public static BufferedReader input() {
        return new BufferedReader(System.console() == null
                ? new InputStreamReader(System.in, StandardCharsets.UTF_8) : System.console().reader());
    }
    public static void line(String value) {
        if (System.console() != null) System.console().writer().println(value);
        else UTF8_OUTPUT.println(value);
    }
}
```

</details>

### 4. Room — определете кой получава публикацията

В `src`, пакет `labnet`, създайте `Room`. Вътрешният тип `Peer` дава на стаята операция `send()` за връзка с участник. При `login()` името се записва в `members`. При `publish()` проследете три поредни действия: създаване на `EVENT` с идентификатор 0, вземане на текущия списък чрез `snapshot()` и извикване на `send(event)` за всеки участник. Ана е в същия списък като Борис и Ива. Когато всички записи приключат, стаята изчислява `delivered` и `failed`. Методът `handle()` връща този резултат на подателя с номера на неговата заявка.

<details>
<summary>Пълен код на <code>src/labnet/Room.java</code></summary>

```java title="src/labnet/Room.java"
package labnet;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;
import java.nio.ByteBuffer;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

import static labnet.Frame.*;

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
    private final ThreadPoolExecutor workers = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(64));

    public Room(Map<String, byte[]> tokens) {
        this.tokens = new ConcurrentHashMap<>(tokens);
    }
    public void provision(String name, byte[] token) { tokens.put(name, token); }
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

    private CompletableFuture<Boolean> authenticate(Peer peer, long id, byte[] body) throws IOException {
        if (peer.principal != null) return error(peer, id, "ALREADY_AUTHENTICATED");
        Named credential = Named.unpack(body);
        byte[] token = credential.value();
        if (token.length < 16 || token.length > 128) return error(peer, id, "BAD_PAYLOAD");
        text(token);
        byte[] expected = tokens.get(credential.name());
        if (expected == null || !MessageDigest.isEqual(expected, token)) {
            var result = error(peer, id, "AUTH_FAILED");
            if (++peer.authFailures >= 3) result.thenRun(peer::close);
            return result;
        }
        peer.principal = credential.name();
        return peer.send(new Frame(OK, id, EMPTY));
    }
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
    private CompletableFuture<Boolean> error(Peer peer, long id, String code) {
        errors.increment();
        return peer.send(new Frame(ERROR, id, bytes(code)));
    }
}
```

</details>

### 5. ChatConnection — изпращайте и слушайте едновременно

В `src`, пакет `labnet.blocking`, създайте `ChatConnection`. Конструкторът отваря сокета и стартира нишка за `receive()`. Методът `submit()` избира следващия номер, записва очаквания отговор в `pending` и изпраща кадъра. В `receive()` проследете двете възможности. При `EVENT` с номер 0 клиентът извежда получената публикация чрез `events.accept(...)`. При `DATA`, `OK` или `ERROR` той намира чакащата заявка по номера ѝ. Това позволява Борис да чете съобщенията на Ана, без да натиска Enter.

<details>
<summary>Пълен код на <code>src/labnet/blocking/ChatConnection.java</code></summary>

```java title="src/labnet/blocking/ChatConnection.java"
package labnet.blocking;

import labnet.Frame;
import static labnet.Frame.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

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
            if (tls != null) {
                SSLSocket secure = (SSLSocket) tls.getSocketFactory().createSocket(plain, host, port, true);
                connected = secure;
                SSLParameters policy = secure.getSSLParameters();
                policy.setEndpointIdentificationAlgorithm("HTTPS");
                secure.setSSLParameters(policy);
                secure.setSoTimeout(3000);
                secure.startHandshake();
                secure.setSoTimeout(0);
            }
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

</details>

### 6. Counter — отчетете показаното съобщение по UDP

В `src`, пакет `labnet.udp`, създайте `Counter`. Вложените `Sender` и `Receiver` са двете страни на UDP обмена. В `Sender.increment()` вижте къде се увеличава `seq`: пакетът се подготвя преди цикъла за повторение. При изгубен отговор подателят изпраща пак същия номер. `Receiver` пази последния номер и общия брой за сесията, затова второто изпращане връща същия брой. `Monitor.displayed()` подава работа към отделна нишка, така че повторението по UDP да не забавя получаването на следващия ред по TCP.

<details>
<summary>Пълен код на <code>src/labnet/udp/Counter.java</code></summary>

```java title="src/labnet/udp/Counter.java"
package labnet.udp;

import labnet.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

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
    }

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
    }

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

}
```

</details>

### 7. DemoKeys — подгответе учебните TLS данни

В `src`, пакет `labnet.tls`, създайте `DemoKeys`. При първо стартиране `prepare()` използва програмата `keytool` от JDK и създава сертификат за `localhost`, хранилище за доверие и отделни случайни токени за `ana`, `boris` и `iva`. Методът `serverContext()` зарежда ключа на сървъра, а `clientContext(true)` зарежда доверието на клиента. Генерираните файлове остават локално в `.local/tls` под работната директория. Затова при стартиране на двете програми използвайте една и съща директория.

<details>
<summary>Пълен код на <code>src/labnet/tls/DemoKeys.java</code></summary>

```java title="src/labnet/tls/DemoKeys.java"
package labnet.tls;

import labnet.Frame;
import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public final class DemoKeys {
    public static final List<String> NAMES = List.of("ana", "boris", "iva");
    private final Path directory = Path.of(".local", "tls");

    private static String secret() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    // keytool receives the password through its environment, never through its argument list.
    private static void keytool(Path cwd, String password, String... arguments) throws Exception {
        String executable = File.separatorChar == '\\' ? "keytool.exe" : "keytool";
        var command = new ArrayList<String>();
        command.add(Path.of(System.getProperty("java.home"), "bin", executable).toString());
        command.addAll(List.of(arguments));
        command.addAll(List.of("-storepass:env", "LABNET_STORE_PASSWORD", "-storetype", "PKCS12"));
        var process = new ProcessBuilder(command).directory(cwd.toFile()).inheritIO();
        process.environment().put("LABNET_STORE_PASSWORD", password);
        if (process.start().waitFor() != 0) throw new IOException("keytool failed");
    }

    public synchronized void prepare() throws Exception {
        if (Files.isDirectory(directory)) { tokens(); serverContext(); clientContext(true); return; }
        Files.createDirectories(directory.getParent());
        Path staging = Files.createTempDirectory(directory.getParent(), "keys-");
        try {
            String password = secret();
            keytool(staging, password, "-genkeypair", "-alias", "chat", "-keyalg", "RSA", "-keysize", "2048",
                    "-validity", "365", "-keystore", "server.p12", "-dname", "CN=localhost", "-ext", "SAN=dns:localhost,ip:127.0.0.1");
            keytool(staging, password, "-exportcert", "-alias", "chat", "-keystore", "server.p12", "-file", "server.cer");
            keytool(staging, password, "-importcert", "-noprompt", "-alias", "chat", "-file", "server.cer", "-keystore", "trust.p12");
            Files.writeString(staging.resolve("store.pass"), password);
            for (String name : NAMES) Files.writeString(staging.resolve(name + ".token"), secret());
            Files.move(staging, directory);
        } finally {
            if (Files.exists(staging)) {
                try (var files = Files.list(staging)) {
                    for (Path file : files.toList()) Files.delete(file);
                }
                Files.delete(staging);
            }
        }
    }

    public byte[] token(String name) throws IOException {
        checkName(name);
        Path token = directory.resolve(name + ".token");
        if (!Files.isRegularFile(token)) throw new IOException("No demo credential for " + name);
        return Files.readAllBytes(token);
    }
    private void checkName(String name) throws IOException {
        if (!name.matches("[a-z][a-z0-9_-]{0,23}") || name.equals("server")) throw new IOException("Invalid demo name");
    }
    public byte[] provision(String name) throws IOException {
        checkName(name);
        try { Files.writeString(directory.resolve(name + ".token"), secret(), StandardOpenOption.CREATE_NEW); }
        catch (FileAlreadyExistsException existing) { /* Keep the same local identity after restart. */ }
        return token(name);
    }
    public Map<String, byte[]> tokens() throws IOException {
        var result = new HashMap<String, byte[]>();
        try (var files = Files.newDirectoryStream(directory, "*.token")) {
            for (Path file : files) {
                String filename = file.getFileName().toString();
                String name = filename.substring(0, filename.length() - ".token".length());
                result.put(name, token(name));
            }
        }
        return result;
    }
    private KeyStore store(String file, char[] password) throws Exception {
        var store = KeyStore.getInstance("PKCS12");
        try (var in = Files.newInputStream(directory.resolve(file))) { store.load(in, password); }
        return store;
    }
    public SSLContext serverContext() throws Exception {
        char[] password = Files.readString(directory.resolve("store.pass")).toCharArray();
        var managers = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        managers.init(store("server.p12", password), password);
        var context = SSLContext.getInstance("TLSv1.3");
        context.init(managers.getKeyManagers(), null, null);
        return context;
    }
    public SSLContext clientContext(boolean trusted) throws Exception {
        var managers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        if (trusted) managers.init(store("trust.p12", Files.readString(directory.resolve("store.pass")).toCharArray()));
        else {
            var empty = KeyStore.getInstance("PKCS12");
            empty.load(null, null);
            managers.init(empty);
        }
        var context = SSLContext.getInstance("TLSv1.3");
        context.init(null, managers.getTrustManagers(), null);
        return context;
    }
}
```

</details>

### 8. Session — дайте на всеки получател собствена опашка

В `src`, пакет `labnet.transport`, създайте `Session`. Той наследява `Room.Peer`. В `send()` всеки кадър получава място в опашката на конкретната връзка и обект, който по-късно ще отчете успех или отказ. Проследете как `written()` премахва първия елемент и го отбелязва като записан. При препълване `close()` освобождава сесията и отчита незавършените записи като неуспешни. Двата транспорта в следващите стъпки споделят тази логика.

<details>
<summary>Пълен код на <code>src/labnet/transport/Session.java</code></summary>

```java title="src/labnet/transport/Session.java"
package labnet.transport;

import labnet.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

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

</details>

### 9. BlockingSession — обслужете връзка с блокиращи операции

В `src`, пакет `labnet.transport`, създайте `BlockingSession`. В `run()` сървърът договаря TLS, стартира отделна нишка за писане и чете цели кадри чрез `Frame.read()`. Прочетеният кадър отива в `dispatch()`. В `write()` другата нишка чака следващия елемент от опашката, записва го в потока и извиква `written()`. Така съобщение може да стигне до Ива, докато сървърът още чака нейния следващ вход.

<details>
<summary>Пълен код на <code>src/labnet/transport/BlockingSession.java</code></summary>

```java title="src/labnet/transport/BlockingSession.java"
package labnet.transport;

import labnet.*;
import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public final class BlockingSession extends Session implements Runnable {
    private final SSLSocket socket;
    private final ScheduledExecutorService timers;
    public BlockingSession(Socket accepted, SSLContext context, Room room, Consumer<Session> removed,
                           ScheduledExecutorService timers, Semaphore handshakes) throws IOException {
        super(room, removed, handshakes);
        this.timers = timers;
        socket = (SSLSocket) context.getSocketFactory().createSocket(accepted, "localhost", accepted.getPort(), true);
        socket.setUseClientMode(false);
        socket.setEnabledProtocols(new String[]{"TLSv1.3"});
    }
    public void run() {
        try {
            var deadline = timers.schedule(this::close, 3, TimeUnit.SECONDS);
            try { socket.startHandshake(); } finally { deadline.cancel(false); }
            handshakeDone();
            Thread.ofVirtual().name("tls-writer").start(this::write);
            var input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            while (!closed && !quitting) {
                int version = input.readUnsignedByte();
                var frameDeadline = timers.schedule(this::close, 2, TimeUnit.SECONDS);
                Frame frame;
                try { frame = Frame.read(version, input); } finally { frameDeadline.cancel(false); }
                dispatch(frame);
            }
            // The writer closes the connection after the final QUIT response.
        } catch (IOException | RuntimeException failure) { close(); }
    }
    private void write() {
        try {
            var output = socket.getOutputStream();
            for (Write item; (item = awaitHead()) != null;) {
                var deadline = timers.schedule(this::close, 2, TimeUnit.SECONDS);
                try { output.write(item.bytes().array()); }
                finally { deadline.cancel(false); }
                written(item);
            }
        } catch (IOException | InterruptedException | RuntimeException failure) { close(); }
    }
    protected void wakeWriter() { /* awaitHead uses the session monitor */ }
    protected void closeTransport() throws IOException { socket.close(); }
}
```

</details>

### 10. SelectorSession — обработете частично четене и запис

В `src`, пакет `labnet.transport`, създайте `SelectorSession`. `read()` приема наличните мрежови байтове, `unwrap()` ги дешифрира, а `decode()` предава към стаята само цял кадър. Ако тялото още не е пристигнало, `reset()` и `compact()` запазват началото му за следващото четене. В обратната посока `pump()` извиква `wrap()` и записва шифрованите байтове в канала. При частичен запис запазва позицията в буфера. Завършването на последния запис позволява на `Room` да отчете доставката.

<details>
<summary>Пълен код на <code>src/labnet/transport/SelectorSession.java</code></summary>

```java title="src/labnet/transport/SelectorSession.java"
package labnet.transport;

import labnet.*;
import javax.net.ssl.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

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
    }
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
    private boolean unwrap() throws IOException {
        netIn.flip();
        var result = engine.unwrap(netIn, plain);
        netIn.compact();
        switch (result.getStatus()) {
            case CLOSED -> close();
            case BUFFER_OVERFLOW -> throw new IOException("TLS application buffer full");
            case BUFFER_UNDERFLOW -> { if (!netIn.hasRemaining()) throw new IOException("TLS record too large"); return false; }
            case OK -> { if (result.bytesProduced() > 0) decode(); }
        }
        return result.bytesConsumed() > 0 || result.bytesProduced() > 0
                || result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED;
    }
    private void wrap(ByteBuffer source) throws IOException {
        netOut.clear();
        var result = engine.wrap(source, netOut);
        netOut.flip();
        if (result.getStatus() != SSLEngineResult.Status.OK) throw new IOException("TLS wrap: " + result.getStatus());
    }
    public void pump(int chunk) throws IOException {
        if (closed) return;
        if (queuedBytes() >= 128 * 1024) readPaused = true;
        else if (queuedBytes() <= 64 * 1024) readPaused = false;
        long now = System.nanoTime();
        if ((!established && now > handshakeDeadline) ||
                (frameStarted != 0 && now - frameStarted > TimeUnit.SECONDS.toNanos(2))) { close(); return; }
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
            switch (engine.getHandshakeStatus()) {
                case NEED_TASK -> {
                    Runnable task = engine.getDelegatedTask();
                    if (task == null) throw new IOException("Missing TLS task");
                    doingTask = true;
                    try {
                        handshakeTasks.execute(() -> {
                            try { task.run(); } finally { doingTask = false; wakeWriter(); }
                        });
                    } catch (RejectedExecutionException busy) { close(); }
                }
                case NEED_WRAP -> wrap(EMPTY.duplicate());
                case NEED_UNWRAP, NEED_UNWRAP_AGAIN -> { if (!unwrap()) { interests(); return; } }
                case FINISHED, NOT_HANDSHAKING -> {
                    if (!established) { established = true; handshakeDone(); }
                    if (!quitting && !readPaused && netIn.position() > 0 && unwrap()) continue;
                    Write item = head();
                    if (item == null) { interests(); return; }
                    wrap(item.bytes());
                    if (!item.bytes().hasRemaining()) flushed = item;
                }
            }
        }
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

</details>

### 11. ChatServer — стартирайте общата стая и приемайте връзки

В `src`, пакет `labnet`, създайте `ChatServer`. Това е първата входна точка на приложението. `main()` взема портовете от `Options`. Конструкторът създава една стая, TCP слушател и UDP приемник. `accept()` избира транспорт за новата връзка и предава същия обект `room` на всяка сесия. Обикновен ред от сървърната конзола извиква `room.publish("server", ...)`. Вече можете да пуснете `ChatServer.main()`, да изчакате `ChatServer ready` и да въведете `/stats`. Без свързани клиенти резултатът започва с `active=0;loggedIn=0`. Завършете тази проверка с `/quit`.

<details>
<summary>Пълен код на <code>src/labnet/ChatServer.java</code></summary>

```java title="src/labnet/ChatServer.java"
package labnet;

import labnet.tls.DemoKeys;
import labnet.transport.*;
import labnet.udp.Counter;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

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

    public ChatServer(int port, int udpPort) throws Exception {
        keys.prepare();
        for (int n = 1; n <= 16; n++) keys.provision("bench" + n);
        tls = keys.serverContext();
        room = new Room(keys.tokens());
        selector = Selector.open();
        listener = ServerSocketChannel.open();
        try {
            counter = new Counter.Receiver(udpPort, 0);
        } catch (IOException failure) {
            listener.close(); selector.close(); room.close();
            throw failure;
        }
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

    public String console(String line) throws Exception {
        String[] words = line.split(" ", 2);
        if (line.equals("/stats")) return room.stats() + ";newConnections=" + model;
        if (line.equals("/help")) return "/stats, /user name, /io nio|thread|pool, /chunk 1..65536, /slow name|off, /drop-ack seq, /quit";
        if (words.length == 2) {
            switch (words[0]) {
                case "/user" -> { room.provision(words[1], keys.provision(words[1])); return "User ready: " + words[1]; }
                case "/io" -> { model = Model.valueOf(words[1].toUpperCase(Locale.ROOT)); return "New connections: " + model; }
                case "/chunk" -> {
                    int value = Integer.parseInt(words[1]);
                    if (value < 1 || value > 65536) throw new IllegalArgumentException("Chunk must be 1..65536");
                    chunk = value; return "NIO socket write limit=" + chunk;
                }
                case "/slow" -> {
                    boolean found = false;
                    for (Session peer : sessions) {
                        boolean match = words[1].equals(peer.name);
                        peer.slow(match);
                        found |= match;
                    }
                    return found || words[1].equals("off") ? "Slow: " + words[1] : "NO_SUCH_USER";
                }
                case "/drop-ack" -> {
                    int seq = Integer.parseInt(words[1]);
                    if (seq < 0) throw new IllegalArgumentException("SEQ must be >= 0");
                    counter.dropAck(seq); return "Drop first UDP ACK at seq=" + seq;
                }
            }
        }
        if (line.startsWith("/")) return "Unknown command. Use /help";
        return room.publish("server", Frame.bytes(line)).get(8, TimeUnit.SECONDS);
    }
    public void close() throws Exception {
        running = false; selector.wakeup(); loop.join();
        listener.close(); selector.close(); counter.close();
        threads.close(); pool.close(); handshakeTasks.close(); timers.shutdownNow(); room.close();
    }
    public static void main(String[] args) throws Exception {
        var opts = new Options(args);
        try (var server = new ChatServer(opts.number("port", 9000, 0, 65535), opts.number("udp-port", 9001, 0, 65535))) {
            ConsoleIO.line("ChatServer ready | TLS localhost:" + server.port() + " | UDP " + server.udpPort() + " | /help");
            var in = ConsoleIO.input();
            for (String line; (line = in.readLine()) != null && !line.equals("/quit");) {
                try { ConsoleIO.line(server.console(line)); }
                catch (Exception failure) { ConsoleIO.line("Error: " + failure.getMessage()); }
            }
        }
    }
}
```

</details>

### 12. Benchmark — измерете доставките с реални клиенти

В `src`, пакет `labnet.demo`, създайте `Benchmark`. Този клас ще бъде извикан от клиентската команда `/bench`. В `run()` всяка измервателна връзка има собствено множество от получени двойки (автор, текст). При три клиента с по десет публикации трябва да има 30 различни двойки при всеки получател и 90 събития общо. Сравнете `missing` и `duplicates` с нула. Накрая `append()` добавя ред в CSV файла.

<details>
<summary>Пълен код на <code>src/labnet/demo/Benchmark.java</code></summary>

```java title="src/labnet/demo/Benchmark.java"
package labnet.demo;

import labnet.*;
import labnet.blocking.*;
import javax.net.ssl.SSLContext;
import labnet.tls.DemoKeys;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.LockSupport;
import static labnet.Frame.*;

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

    public static void measure(String host, int port, SSLContext tls, DemoKeys keys, int clients, int messages, int intervalMs, Path csv) throws Exception {
        if (clients < 1 || clients > 16 || messages < 1 || messages > 100 || intervalMs < 1 || intervalMs > 1000)
            throw new IllegalArgumentException("Benchmark: 1..16 clients, 1..100 messages, 1..1000 ms");
        Files.createDirectories(csv.toAbsolutePath().getParent());
        System.out.println(HEADER);
        run(host, port, tls, keys, clients, messages, intervalMs, csv);
    }
}
```

</details>

### 13. ChatClient — управлявайте трима участници от една конзола

В `src`, пакет `labnet`, създайте `ChatClient`. Това е втората входна точка. В `open()` всяко име получава собствена `ChatConnection` и UDP монитор. След успешни `AUTH` и `LOGIN` връзката влиза в `clients`. В `command()` командата `/use ana` избира коя връзка ще изпрати следващия ред. Обикновен текст става `BROADCAST`. Когато сървърът го върне като `EVENT`, обработчикът в `open()` отпечатва името на получателя и текста с автора. Затова `boris < [ana] Здравейте!` означава, че Борис е получил съобщението на Ана.

<details>
<summary>Пълен код на <code>src/labnet/ChatClient.java</code></summary>

```java title="src/labnet/ChatClient.java"
package labnet;

import labnet.blocking.ChatConnection;
import labnet.demo.Benchmark;
import labnet.tls.DemoKeys;
import labnet.udp.Counter;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import static labnet.Frame.*;

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
    }
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
            case "/bench" -> {
                int count = args.length > 1 ? Integer.parseInt(name) : 3;
                int messages = args.length > 2 ? Integer.parseInt(args[2]) : 10;
                Benchmark.measure(host, port, tls, keys, count, messages, 30, Path.of("results/chat.csv"));
                return true;
            }
        }
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
            case "/work" -> {
                int ms = Integer.parseInt(name);
                byte[] data = args.length > 2 ? bytes(args[2]) : EMPTY;
                show(chat.request(WORK, ByteBuffer.allocate(4 + data.length).putInt(ms).put(data).array()));
            }
            case "/burst" -> {
                var replies = new ArrayList<CompletableFuture<Frame>>();
                for (int i = 1; i <= 3; i++) replies.add(chat.submit(BROADCAST, bytes("burst-" + i)));
                for (var reply : replies) show(reply.get(8, TimeUnit.SECONDS));
            }
            default -> {
                if (line.startsWith("/")) throw new IllegalArgumentException("Unknown command. Use /help");
                show(chat.request(BROADCAST, bytes(line)));
            }
        }
        return true;
    }
    public void close() { clients.values().forEach(Connection::close); clients.clear(); }
    public static void main(String[] args) throws Exception {
        var opts = new Options(args);
        try (var client = new ChatClient(opts.get("host", "localhost"), opts.number("port", 9000, 1, 65535),
                opts.number("udp-port", 9001, 1, 65535))) {
            ConsoleIO.line("ChatClient | /new ana, /new boris, /new iva | /use name | /help");
            String name = opts.get("name", "");
            if (!name.isEmpty()) client.open(name);
            var input = ConsoleIO.input();
            for (String line; (line = input.readLine()) != null;) {
                try { if (!client.command(line)) return; }
                catch (Exception failure) { ConsoleIO.line("Error: " + failure.getMessage()); }
            }
        }
    }
}
```

</details>

### 14. Demo — проверете седемте упражнения

В папката **test**, пакет `labnet.demo`, създайте `Demo`. Неговият `main()` стартира сървър на свободни локални портове. Вътрешният `Participant` чете от реална клиентска връзка и сравнява събитията с очакванията. Без аргументи тестът преминава през седемте упражнения. За да повторите само разговора с трима участници, задайте `2` в **Program arguments** на конфигурацията за `Demo`. За всички упражнения очаквайте по един ред от `PASS lab01` до `PASS lab07`.

<details>
<summary>Пълен код на <code>test/labnet/demo/Demo.java</code></summary>

```java title="test/labnet/demo/Demo.java"
package labnet.demo;

import labnet.*;
import labnet.blocking.*;
import labnet.tls.DemoKeys;
import labnet.udp.Counter;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import static labnet.Frame.*;

public final class Demo {
    private static final DemoKeys keys = new DemoKeys();
    private static SSLContext tls;
    private static void check(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
    }
    private static void reply(Frame frame, int type, String body) throws IOException {
        check(frame.type() == type && Arrays.equals(frame.body(), bytes(body)), "Unexpected response #" + frame.id() + ": " + text(frame.body()));
    }
    private static final class Participant implements AutoCloseable {
        final String name;
        final ChatConnection client;
        final BlockingQueue<ChatConnection.Message> inbox = new LinkedBlockingQueue<>();
        Participant(String name, int port, SSLContext tls, Counter.Monitor monitor) throws IOException {
            this.name = name;
            client = new ChatConnection("localhost", port, tls, message -> {
                inbox.add(message);
                if (monitor != null) monitor.displayed();
            });
        }
        void login() throws Exception {
            reply(client.request(AUTH, new Named(name, keys.token(name)).pack()), OK, "");
            reply(client.request(LOGIN, bytes(name)), OK, "");
        }
        void expect(String author, String content) throws Exception {
            var wanted = new ChatConnection.Message(author, content);
            check(wanted.equals(inbox.poll(3, TimeUnit.SECONDS)), name + " missing " + author + ": " + content);
        }
        void barrier() throws Exception {
            reply(client.request(PING, EMPTY), OK, "");
            check(inbox.isEmpty(), name + ": unexpected event " + inbox.peek());
        }
        public void close() { client.close(); }
    }

    private static void stage1(ChatServer server) throws Exception {
        try (var socket = (SSLSocket) tls.getSocketFactory().createSocket("localhost", server.port())) {
            socket.setSoTimeout(3000);
            var out = socket.getOutputStream();
            var in = new DataInputStream(socket.getInputStream());
            out.write(new Frame(AUTH, 1, new Named("ana", keys.token("ana")).pack()).encode().array());
            reply(Frame.read(in), OK, "");
            byte[] fragmented = new Frame(ECHO, 2, bytes("Чат")).encode().array();
            // Split after two header bytes. Also test the decoder with forced one-byte reads.
            out.write(fragmented, 0, 2);
            out.write(fragmented, 2, fragmented.length - 2);
            var shortReads = new ByteArrayInputStream(fragmented) {
                public synchronized int read(byte[] b, int off, int len) { return super.read(b, off, Math.min(1, len)); }
            };
            Frame decoded = Frame.read(new DataInputStream(shortReads));
            check(decoded.id() == 2 && text(decoded.body()).equals("Чат"), "One-byte reads");
            var pair = new ByteArrayOutputStream();
            pair.write(new Frame(ECHO, 3, bytes("Б")).encode().array());
            pair.write(new Frame(ECHO, 4, bytes("В")).encode().array());
            out.write(pair.toByteArray());
            int id = 1;
            for (String expected : List.of("Чат", "Б", "В")) {
                var frame = Frame.read(in);
                check(frame.id() == ++id, "Wrong echo ID");
                reply(frame, DATA, expected);
                ConsoleIO.line("ECHO #" + id + " -> " + expected);
            }
        }
    }

    private static void chat(ChatServer server) throws Exception {
        int port = server.port();
        try (var ana = new Participant("ana", port, tls, null);
             var boris = new Participant("boris", port, tls, null);
             var iva = new Participant("iva", port, tls, null);
             var visitor = new Participant("ana", port, tls, null)) {
            var members = List.of(ana, boris, iva);
            for (var participant : members) participant.login();
            reply(visitor.client.request(AUTH, new Named("ana", keys.token("ana")).pack()), OK, "");
            reply(visitor.client.request(LOGIN, bytes("ana")), ERROR, "NAME_IN_USE");
            reply(visitor.client.request(BROADCAST, bytes("Hi")), ERROR, "NOT_LOGGED_IN");
            reply(ana.client.request(BROADCAST, bytes("Здравейте!")), DATA, "delivered=3;failed=0");
            for (var member : members) {
                member.expect("ana", "Здравейте!");
                ConsoleIO.line(member.name + " получи [ana] Здравейте!");
            }
            check("delivered=3;failed=0".equals(server.console("Занятието започва.")), "Console receipt");
            for (var member : members) {
                member.expect("server", "Занятието започва.");
                ConsoleIO.line(member.name + " получи [server] Занятието започва.");
            }
            String boundary = "я".repeat(2048);
            reply(boris.client.request(BROADCAST, bytes(boundary)), DATA, "delivered=3;failed=0");
            for (var member : members) member.expect("boris", boundary);
            for (byte[] invalid : List.of(EMPTY, bytes(boundary + "!"), new byte[]{(byte) 0xff}))
                reply(boris.client.request(BROADCAST, invalid), ERROR, "BAD_PAYLOAD");
            try (var writers = Executors.newFixedThreadPool(2)) {
                var jobs = new ArrayList<Future<Void>>();
                var start = new CyclicBarrier(2);
                for (var sender : List.of(ana, boris)) jobs.add(writers.submit(() -> {
                    start.await(3, TimeUnit.SECONDS);
                    for (int n = 1; n <= 10; n++)
                        reply(sender.client.request(BROADCAST, bytes("ред " + n)), DATA, "delivered=3;failed=0");
                    return null;
                }));
                for (var job : jobs) job.get(15, TimeUnit.SECONDS);
            }
            for (var member : members) {
                Set<ChatConnection.Message> expected = new HashSet<>();
                for (String author : List.of("ana", "boris"))
                    for (int n = 1; n <= 10; n++) expected.add(new ChatConnection.Message(author, "ред " + n));
                for (int n = 0; n < 20; n++) check(expected.remove(member.inbox.poll(3, TimeUnit.SECONDS)), "Duplicate or missing event");
                member.barrier();
            }
            visitor.barrier();
            reply(iva.client.request(QUIT, EMPTY), OK, "");
            reply(ana.client.request(USERS, EMPTY), DATA, "ana\nboris");
            reply(ana.client.request(BROADCAST, bytes("До утре!")), DATA, "delivered=2;failed=0");
            ana.expect("ana", "До утре!"); boris.expect("ana", "До утре!");
            try (var rejoined = new Participant("iva", port, tls, null)) {
                rejoined.login();
                reply(boris.client.request(BROADCAST, bytes("Добре дошла!")), DATA, "delivered=3;failed=0");
                rejoined.expect("boris", "Добре дошла!");
                rejoined.barrier();
            }
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
            while (!text(ana.client.request(USERS, EMPTY).body()).equals("ana\nboris")) {
                check(System.nanoTime() < deadline, "Abrupt disconnect cleanup");
                Thread.sleep(10);
            }
            try (var after = new Participant("iva", port, tls, null)) { after.login(); }
        }
    }

    private static void stage3(ChatServer server) throws Exception {
        var receipts = new LinkedBlockingQueue<Counter.Receipt>();
        server.console("/drop-ack 3");
        try (var monitor = new Counter.Monitor("localhost", server.udpPort(), receipts::add);
             var ana = new Participant("ana", server.port(), tls, null);
             var boris = new Participant("boris", server.port(), tls, null);
             var iva = new Participant("iva", server.port(), tls, monitor)) {
            ana.login(); boris.login(); iva.login();
            for (int n = 1; n <= 3; n++) {
                String line = "съобщение " + n;
                reply(ana.client.request(BROADCAST, bytes(line)), DATA, "delivered=3;failed=0");
                for (var participant : List.of(ana, boris, iva)) participant.expect("ana", line);
                var ack = receipts.poll(3, TimeUnit.SECONDS);
                check(ack != null && ack.seq() == n && ack.total() == n && ack.attempts() == (n == 3 ? 2 : 1), "UDP ACK " + ack);
                System.out.println("iva UDP " + ack);
            }
        }
    }

    private static void mixedRoom(ChatServer server) throws Exception {
        List<Participant> people = new ArrayList<>();
        try {
            List<String> models = List.of("nio", "thread", "pool");
            for (int i = 0; i < models.size(); i++) {
                server.console("/io " + models.get(i));
                var person = new Participant(DemoKeys.NAMES.get(i), server.port(), tls, null);
                people.add(person);
                person.login();
            }
            var ana = people.getFirst();
            reply(ana.client.request(BROADCAST, bytes("Обща стая")), DATA, "delivered=3;failed=0");
            for (var person : people) person.expect("ana", "Обща стая");
            reply(ana.client.request(SEND, new Named("iva", bytes("Лично")).pack()), OK, "");
            people.getLast().expect("ana", "Лично");
            reply(ana.client.request(SEND, new Named("missing", bytes("Hi")).pack()), ERROR, "NO_SUCH_USER");
            for (int i = 0; i < models.size(); i++) {
                var person = people.get(i);
                var work = person.client.submit(WORK, new byte[]{0, 0, 0, 30, 65});
                reply(person.client.request(PING, EMPTY), OK, "");
                reply(work.get(3, TimeUnit.SECONDS), DATA, "A");
                reply(person.client.request(WORK, new byte[]{0, 0, 0, 51}), ERROR, "BAD_PAYLOAD");
                String stats = text(person.client.request(STATS, EMPTY).body());
                check(stats.contains("loggedIn=3;") && stats.endsWith("model=" + models.get(i).toUpperCase(Locale.ROOT)), stats);
                person.barrier();
            }
        } finally { people.forEach(Participant::close); }
    }

    private static void stage5(ChatServer server) throws Exception {
        try (var ana = new Participant("ana", server.port(), tls, null);
             var boris = new Participant("boris", server.port(), tls, null);
             var iva = new Participant("iva", server.port(), tls, null)) {
            ana.login(); boris.login(); iva.login();
            server.console("/slow iva");
            var replies = new ArrayList<CompletableFuture<Frame>>();
            for (int n = 1; n <= 3; n++) replies.add(ana.client.submit(BROADCAST, bytes("burst-" + n)));
            for (var result : replies) reply(result.get(3, TimeUnit.SECONDS), DATA, "delivered=2;failed=1");
            for (int n = 1; n <= 3; n++) {
                ana.expect("ana", "burst-" + n); boris.expect("ana", "burst-" + n);
            }
            iva.client.ended().get(3, TimeUnit.SECONDS);
            check(iva.inbox.isEmpty(), "Held client received a delivery");
            reply(ana.client.request(BROADCAST, bytes("Продължаваме")), DATA, "delivered=2;failed=0");
            ana.expect("ana", "Продължаваме"); boris.expect("ana", "Продължаваме");
            ana.barrier(); boris.barrier();
            System.out.println("iva removed at queue limit 2; 3 x delivered=2;failed=1, next delivered=2;failed=0");
        }
    }

    private static void stage6(ChatServer server) throws Exception {
        try (var ana = new Participant("ana", server.port(), tls, null);
             var boris = new Participant("boris", server.port(), tls, null);
             var iva = new Participant("iva", server.port(), tls, null);
             var wrong = new Participant("ana", server.port(), tls, null)) {
            var members = List.of(ana, boris, iva);
            for (var member : members) {
                reply(member.client.request(LOGIN, bytes(member.name)), ERROR, "AUTH_REQUIRED");
                member.login();
            }
            reply(wrong.client.request(AUTH, new Named("ana", bytes("incorrect-demo-token")).pack()), ERROR, "AUTH_FAILED");
            reply(wrong.client.request(LOGIN, bytes("ana")), ERROR, "AUTH_REQUIRED");
            reply(boris.client.request(BROADCAST, bytes("Защитен чат")), DATA, "delivered=3;failed=0");
            for (var member : members) { member.expect("boris", "Защитен чат"); member.barrier(); }
            wrong.barrier();
            try (var unexpected = new Participant("unknown", server.port(), keys.clientContext(false), null)) {
                throw new AssertionError("Untrusted server accepted");
            } catch (SSLException expected) { System.out.println("Untrusted TLS handshake rejected"); }
            try (var raw = new Socket("localhost", server.port());
                 var mismatch = (SSLSocket) tls.getSocketFactory().createSocket(raw, "wrong.invalid", server.port(), true)) {
                var policy = mismatch.getSSLParameters();
                policy.setEndpointIdentificationAlgorithm("HTTPS");
                mismatch.setSSLParameters(policy);
                mismatch.setSoTimeout(3000);
                mismatch.startHandshake();
                throw new AssertionError("Wrong server name accepted");
            } catch (SSLHandshakeException expected) { System.out.println("Wrong TLS hostname rejected"); }
        }
    }
    public static void main(String[] args) throws Exception {
        String target = args.length == 0 ? "all" : args[0];
        if (!target.matches("all|[1-7]")) throw new IllegalArgumentException("Demo all or 1..7");
        try (var server = new ChatServer(0, 0)) {
            tls = keys.clientContext(true);
            for (int stage = 1; stage <= 7; stage++) {
                if (!target.equals("all") && stage != Integer.parseInt(target)) continue;
                ConsoleIO.line("\n" + List.of("1. TCP кадри и ECHO", "2. Стая с трима участници", "3. UDP брояч с изгубен ACK",
                        "4. NIO с частични записи по 3 байта", "5. Изходяща опашка за бавен клиент",
                        "6. TLS, AUTH и LOGIN", "7. Три модела в един сървър").get(stage - 1));
                switch (stage) {
                    case 1 -> stage1(server);
                    case 2 -> { server.console("/io thread"); chat(server); awaitCleanup(server); mixedRoom(server); }
                    case 3 -> stage3(server);
                    case 4 -> { server.console("/io nio"); server.console("/chunk 3"); chat(server); server.console("/chunk 65536"); }
                    case 5 -> stage5(server);
                    case 6 -> stage6(server);
                    case 7 -> {
                        for (String model : List.of("thread", "pool", "nio")) {
                            server.console("/io " + model);
                            Benchmark.measure("localhost", server.port(), tls, keys, 3, 10, 30, Path.of("results/chat.csv"));
                            awaitCleanup(server);
                        }
                    }
                }
                awaitCleanup(server);
                System.out.println("PASS lab0" + stage);
            }
        }
    }
    private static void awaitCleanup(ChatServer server) throws Exception {
        long until = System.nanoTime() + TimeUnit.SECONDS.toNanos(4);
        while (!server.console("/stats").startsWith("active=0;loggedIn=0;")) {
            check(System.nanoTime() < until, "Connections did not close");
            Thread.sleep(10);
        }
    }
}
```

</details>

<a id="chat-demo"></a>

## Демонстрация — един сървър и три клиента

1. В IntelliJ отворете `ChatServer` и натиснете зелената стрелка до `main()`. Изчакайте `ChatServer ready | TLS localhost:9000 | UDP 9001 | /help`.
2. По същия начин стартирайте `ChatClient.main()`. В прозореца **Run** вече имате по една конзола за всяка програма. Ако клиентът не намира TLS файловете, в **Run → Edit Configurations → Working directory** задайте корена на `chat-solution` за двете конфигурации.
3. В клиентската конзола въведете следните редове един по един. Изчакайте `Connected` след всяка команда `/new`. Изборът с `/use` променя подателя и оставя трите връзки отворени.

```text
/new ana
/new boris
/new iva
/clients
/use ana
Здравейте!
```

Трябва да видите следните четири реда. Редът на извеждане може да се различава. Ще се появят и отделни редове за UDP броячите.

```text
ana < [ana] Здравейте!
boris < [ana] Здравейте!
iva < [ana] Здравейте!
ana DATA #3 delivered=3;failed=0
```

Първият ред от таблицата е току-що изпълнената проверка. Продължете от Борис, като въведете `/use boris`. За всеки следващ участник го изберете с `/use име`, а реда на сървъра напишете в сървърната конзола.

| Кой въвежда | Вход | Очакван резултат |
|---|---|---|
| Ана | `Здравейте!` | Ана, Борис и Ива виждат по веднъж `[ana] Здравейте!`. Ана получава и `delivered=3;failed=0`. |
| Борис | `И аз съм тук.` | И тримата виждат по веднъж `[boris] И аз съм тук.`, включително Ива, която още не е писала. |
| Сървърът | `Занятието започва.` | И тримата виждат по веднъж `[server] Занятието започва.`. |
| Ива | `/quit` | Връзката на Ива приключва. Изберете Ана и проверете с `/users`, че в стаята остават `ana` и `boris`. |
| Ана | `До утре!` | Само Ана и Борис виждат `[ana] До утре!`. Ана получава `delivered=2;failed=0`. |
| Ива | `/new iva` | Входът успява. Едва след него Борис изпраща `Добре дошла!`. Това е първото съобщение в новата сесия на Ива и го виждат и тримата. |

### Стартиране от терминал

От корена на проекта изпълнете в PowerShell командите за компилиране. Масивът съдържа имената на всичките 14 изходни файла, включително теста.

```powershell
$sources = (Get-ChildItem -Path src,test -Recurse -Filter '*.java').FullName
javac --release 21 -encoding UTF-8 -d out @sources
```

В първи терминал изпълнете `java -cp out labnet.ChatServer`, а във втори — `java -cp out labnet.ChatClient`. Ако искате всеки получател да има отделна конзола, стартирайте клиентската команда в три терминала и въведете съответно `/login ana`, `/login boris` и `/login iva`. За автоматичната проверка изпълнете `java -cp out labnet.demo.Demo all`. За да приключите ръчния опит, въведете `/exit` в клиентската конзола и `/quit` в сървърната.

### Допълнителни проверки

Автоматизирайте още три проверки със срок за изчакване на всеки отговор и събитие.

- Докато Ана е в стаята, опитайте `/login ana` от четвърти клиент. След отказа публикувайте от Борис. Съобщението трябва да стигне само до трите влезли клиента.
- Изпратете 2048 букви „я“ в една заявка. И тримата трябва да получат целия текст от 4096 байта. Добавете един символ `!` и изпратете отново. Подателят трябва да получи `BAD_PAYLOAD`, а броят на показаните съобщения да остане същият.
- Затворете процеса на Ива без `/quit`. След като `/users` покаже само Ана и Борис, изпратете нов ред. Очаквайте две доставки, след което проверете, че нов клиент може да заеме освободеното име `iva`.

## Решен пример — колко байта заема „Чат“

Преди да свържете клиентите, проверете един кадър на хартия и в Java. Думата „Чат“ има три букви и шест байта в UTF-8. Към тях протоколът добавя осем байта за идентификатора на заявката, затова полето за дължина трябва да съдържа 14. Запишете примера като `ChatFrame.java`.

```java
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

class ChatFrame {
    public static void main(String[] args) {
        byte[] body = "Чат".getBytes(StandardCharsets.UTF_8);
        int length = Long.BYTES + body.length;
        ByteBuffer frame = ByteBuffer.allocate(6 + length);
        frame.put((byte) 1).put((byte) 0x12).putInt(length).putLong(2L).put(body);
        System.out.println(HexFormat.ofDelimiter(" ").formatHex(frame.array()));
    }
}
```

Стартирайте `java "-Dfile.encoding=UTF-8" ChatFrame.java`. Сравнете резултата по полета с очакваните байтове за версия 1, команда `BROADCAST`, дължина 14, заявка № 2 и текст „Чат“.

```text
01 12 00 00 00 0e 00 00 00 00 00 00 00 02 d0 a7 d0 b0 d1 82
```

## Как да изградите чата в седем упражнения

<a id="lab-1"></a>

### Упражнение 1 — един клиент и един TCP кадър

Започнете с блокиращ клиент и сървър. Изпратете „Чат“ чрез `ECHO` и сравнете върнатите байтове и идентификатора. Подайте един кадър на декодера на части, после два слепени кадъра. Трябва да получите съответно едно и две цели съобщения. Запазете кодека и тези тестове за всички следващи версии. [Упражнение 1 — TCP и приложен протокол](../laboratorno-uprazhnenie-1/zadachi/)

<a id="lab-2"></a>

### Упражнение 2 — общата стая

Добавете регистър на сесиите, `LOGIN`, `USERS` и `BROADCAST`. Клиентът чете от сокета и докато чака клавиатурен вход. Използвайте една и съща операция за разпращане от клиент и от конзолата на сървъра. Реализирайте `/users` чрез `USERS`, за да проверявате кой е в стаята. Изпълнете демонстрацията в четири терминала. После нека Ана и Борис изпратят едновременно по десет различими реда. Всеки от тримата трябва да получи същите 20 публикации по веднъж. Редът между двамата автори може да се различава. [Упражнение 2 — конкурентен TCP сървър](../laboratorno-uprazhnenie-2/zadachi/)

<a id="lab-3"></a>

### Упражнение 3 — надежден UDP брояч на получените съобщения

Включете UDP приемника в процеса на `ChatServer`. При всяко показано събитие клиентът изпраща прираст към брояча на своята UDP сесия. Проследете сесията на Ива, докато разговорът продължава по TCP. За теста запишете по една операция `delta=+1` за всеки показан ред и ги подайте последователно през една UDP сесия. Започнете от нула, покажете три публикации и симулирайте загуба само на първия `ACK` за третата операция. Повторението със същия `SEQ` трябва да получи запазения резултат 3. Проверете трите реда в чата, крайния брояч 3 и повторно изпратената дейтаграма. [Упражнение 3 — UDP и надеждност](../laboratorno-uprazhnenie-3/zadachi/)

<a id="lab-4"></a>

### Упражнение 4 — същите клиенти срещу NIO сървър

Добавете обработка чрез `Selector` и `SocketChannel` в същия `ChatServer`. Двата модела използват общ регистър на участниците. Запазете командите и формата на събитията, за да работят съществуващите клиенти. За всеки получател следете докъде е записан неговият кадър. В тест ограничете едно записване до три байта. Повторете разговора с кирилица и сравнете редовете при тримата с блокиращата версия. Увеличете `delivered` едва когато последният байт за съответния получател е записан. [Упражнение 4 — Java NIO](../laboratorno-uprazhnenie-4/zadachi/)

<a id="lab-5"></a>

### Упражнение 5 — един участник спира да чете

Ограничете изходящата опашка на всяка сесия по брой кадри и байтове. Ако следващото събитие ще надвиши твърдия лимит, прекратете бавната сесия и отчетете неуспех за незавършените записи към нея. За възпроизводим тест задръжте записите само към Ива и задайте малък лимит, например два чакащи кадъра. Нека Ана и Борис четат нормално. Изпратете три публикации, без да чакате предишното потвърждение, като използвате ограниченото подаване на заявки от упражнението. Проверете, че Ана и Борис получават и трите, лимитът се спазва и след отстраняването на Ива следващото съобщение има `delivered=2;failed=0`. [Упражнение 5 — опашки и управление на натоварването](../laboratorno-uprazhnenie-5/zadachi/)

<a id="lab-6"></a>

### Упражнение 6 — защитена стая с TLS

Добавете TLS към общата входна точка на сървъра. За блокиращия модел използвайте TLS сокети, а за NIO — `SSLEngine`. Същият клиент проверява сертификата и при двата модела. За общия чат разширете конфигурацията от един тестов потребител до три имена с отделни тайни, подавани при стартиране. Всеки клиент проверява сертификата и името на сървъра, изпълнява `AUTH`, после `LOGIN` със същото име. Повторете разговора с трима удостоверени участници. Четвърти клиент с грешен токен трябва да получи `AUTH_FAILED` и да остане извън стаята. При недоверен сертификат връзката трябва да приключи още при TLS договарянето. [Упражнение 6 — TLS и удостоверяване](../laboratorno-uprazhnenie-6/zadachi/)

<a id="lab-7"></a>

### Упражнение 7 — измерване на доставката

В работещия сървър изберете последователно нишка за всяка нова връзка, пул от нишки и NIO. За всеки модел отворете нови измервателни връзки от същия `ChatClient`. Използвайте общ порт, стая и протокол във всички опити. Започнете с три клиента, които вече са влезли в стаята и четат непрекъснато. Нека всеки публикува по десет номерирани съобщения. Очаквайте 30 успешни заявки и 90 получени събития, по 30 различни двойки (автор, номер) при всеки клиент. Запишете липсите и дубликатите поотделно. Едва след тази проверка увеличете броя участници. При еднакъв размер на текста и еднаква честота на публикуване измерете публикации/s, получени събития/s и p95 на времето от заявката до собствения `EVENT` по часовника на подателя. Отбележете броя активни клиенти, отказите и изтеклите времена за изчакване във всеки опит. [Упражнение 7 — измерване на производителността](../laboratorno-uprazhnenie-7/zadachi/)

## Какво да предадете

1. Предайте едно Git хранилище с Java клиента, обединения сървър, вградения UDP брояч и тестовете. Отбележете работещия резултат след всеки етап с тагове `lab01` до `lab07`.
2. В `README.md` запишете версията на JDK, командите за компилация, стартиране и тест, портовете и начина за създаване на локални TLS данни. Пазете тайните и частните ключове извън хранилището.
3. В `results/lab01/` до `results/lab07/` приложете входа, очаквания и наблюдавания резултат от проверката за съответния етап. Включете разговора от трите клиентски конзоли, тестовете за едновременни публикации, прекъсване и повторен вход, както и CSV с измерванията и кратко сравнение на трите модела за обслужване.
