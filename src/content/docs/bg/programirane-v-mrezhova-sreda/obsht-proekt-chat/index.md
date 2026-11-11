---
title: "Общ проект — многопотребителски чат с Java"
sidebar:
  label: Общ проект — чат
tableOfContents:
  maxHeadingLevel: 2
---

# Общ проект — многопотребителски чат с Java

В този решен пример ще изпратите „Чат“ през един TCP сокет, а след това ще превърнете обмена в обща стая. Финалното приложение има един `ChatServer` и един `ChatClient`. От клиентската конзола ще отворите три независими връзки и ще проверите кой получава всяка публикация.

[Създайте проекта в IntelliJ](#chat-setup) · [Пуснете разговора с три клиента](#chat-demo) · [ChatServer.java](#code-server) · [ChatClient.java](#code-client) · [Всички файлове](#chat-code)

| Стъпки на решението | Резултат, който можете да проверите |
|---|---|
| [1. От „Чат“ до TCP кадър](#lab-1) | Изпратете 20 байта и прочетете обратно заявка № 2 с текст „Чат“. |
| [2. Една стая и три независими клиента](#lab-2) | Всеки получава по един общ ред. Само Ива получава личното съобщение до нея. |
| [3. Надежден UDP брояч](#lab-3) | Изгубете третото потвърждение. При втория опит броячът на Ива остава 3. |
| [4. Частично четене и запис с Java NIO](#lab-4) | Ограничете записа до три байта и възстановете целия текст с кирилица. |
| [5. Бавен получател и ограничени опашки](#lab-5) | Задръжте записите към Ива. След запълване на опашката ѝ Ана и Борис продължават разговора. |
| [6. TLS, доверие и вход по име](#lab-6) | Проверете сертификата, удостоверете клиента и тогава заемете име в стаята. |
| [7. Измерване на доставката](#lab-7) | Сравнете трите модела на сървъра при 30 публикации и 90 очаквани събития. |

<a id="chat-setup"></a>

## Създайте проекта в IntelliJ

1. Изберете **File → New → Project → Java**, име `chat-solution`, **JDK 21** и система за изграждане **IntelliJ**. Ако разполагате с готовия пример от курса, изберете **File → Open** и отворете папката `course-materials/network-programming-java/chat-solution`.
2. В новия проект маркирайте `src` като **Sources Root**. С **New → Package** създайте `labnet` и подпакетите `labnet.blocking`, `labnet.transport`, `labnet.udp`, `labnet.tls` и `labnet.demo`. До `src` създайте `test`, маркирайте я като **Test Sources Root** и в нея създайте `labnet.demo` за класа `Demo`.
3. Започнете с `src/labnet/Frame.java`. За всеки следващ файл изберете съответния пакет и **New → Java Class**. Когато сглобявате цял клас, копирайте файла от [пълния код](#chat-code), включително `package` и `import`. [Първата проверка](#echo-probe) изисква само `Frame` и кратката програма `EchoProbe`.
4. При пускане на `ChatServer` и `ChatClient` от IntelliJ задайте корена на проекта за **Working directory** на двете конфигурации. Така клиентът ще отвори данните за доверие и вход, които сървърът е създал при първото стартиране.

Във всяко упражнение първо виждате какво се добавя, после откъса от съответния Java файл и под него таблица с обяснение по редове. Номерата са от **пълния файл**. В IntelliJ `Ctrl+N` намира класа, а `Ctrl+G` отваря посочения ред. Например в `Frame.java` на ред 21 ще видите защо към дължината на текста се прибавят осем байта.

Започнете с упражнение 1 и изпълнете `EchoProbe` веднага след `Frame`. Откъсите за следващите упражнения показват крайния вариант на класовете. В таблиците ще видите кога се добавя всяка зависимост. Проверките `Demo 1` до `Demo 7` се изпълняват след [компилирането на целия проект и тестовия клас](#chat-build).

<a id="lab-1"></a>

## Упражнение 1 — От „Чат“ до TCP кадър

**Първа работеща версия.** Напишете `Frame` преди сървъра. Клиентът трябва да изпрати „Чат“, а получателят да възстанови точно същите байтове и същия номер на заявката. За този първи опит са достатъчни JDK 21+, един файл от проекта и два локални TCP сокета.

### 1. Определете какво точно пътува по връзката

Създайте пакет `labnet` и файл `Frame.java` в `src/labnet`. На този етап използвайте `ECHO` за заявка и `DATA` за отговор. Имената в общия списък показват и типовете, които ще започнете да обработвате в следващите упражнения.

```java title="src/labnet/Frame.java · редове 1–18"
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
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 1 | Класът принадлежи на пакета `labnet`. Същия пакет ще използват двете входни точки. |
| 3–5 | Потокът чете числови полета, `ByteBuffer` ги подрежда в паметта, а класовете за кодиране превръщат текст в байтове и обратно. |
| 7 | Един `record` държи типа на операцията, 64-битовия идентификатор и тялото. Java създава достъпите `type()`, `id()` и `body()`. |
| 8 | `PING`, `ECHO` и `QUIT` са първите команди. При упражнение 2 `LOGIN` и `BROADCAST` добавят вход и публикация. |
| 9 | `SEND`, `USERS` и `STATS` служат на стаята. `WORK` влиза в упражнение 5, `AUTH` в упражнение 6. |
| 10 | `OK` е потвърждение, `DATA` носи резултат, `EVENT` пристига самостоятелно, а `ERROR` връща причина за отказ. |
| 11 | Шестте байта на заглавната част са един за версия, един за тип и четири за дължина. Ограничението 65536 се отнася за полезния товар, включително идентификатора. |
| 12 | Един празен масив служи например за тялото на `PING`. |
| 14 | Превърнете текста в UTF-8 преди да изчислите размера му. |
| 16–17 | Декодерът превръща байтовете в текст. При невалидна поредица хвърля `CharacterCodingException`, която по-късно стаята превръща в отговор за невалидно тяло. |

### 2. Добавете кодиране, после проверка на дължината

Начертайте първо кадъра за `ECHO` с номер 2. „Чат“ заема шест байта в UTF-8. Полето за дължина е 8 + 6 = 14, а по TCP ще изпратим общо 6 + 14 = 20 байта.

```java title="src/labnet/Frame.java · редове 20–31"
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
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 20 | Методът връща буфер, готов за четене или подаване към канал. |
| 21 | Към размера на тялото добавете осемте байта на `long id`. |
| 22 | Спрете преди заделяне на буфера, ако полезният товар надвишава лимита. |
| 23 | Заделете място и за заглавната част. Първите два записа са версия 1 и типът като един байт. |
| 24 | Запишете дължината, идентификатора и тялото в този ред. `flip()` премества позицията в началото и задава границата на записаните байтове. Новият `ByteBuffer` използва big-endian. |
| 27 | Един и същ метод ще валидира заглавието в блокиращия и NIO декодера. |
| 28–29 | Версията трябва да е 1, а обявеният размер да побира идентификатора и да е до 65536. При нарушение прекратете четенето с грешка. |
| 30 | Върнете проверения размер за следващото заделяне на памет. |

### 3. Възстановете кадъра от поток

TCP може да достави заглавието и тялото на отделни четения. Затова първо прочетете полетата с известна ширина, после изчакайте целия обявен брой байтове.

```java title="src/labnet/Frame.java · редове 33–43"
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
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 33–34 | При обикновено четене вземете версията като число от 0 до 255 и извикайте общия декодер. |
| 36 | Втората форма приема вече прочетената версия. Тя ще позволи на сървъра да пуска таймер от първия байт на кадъра. |
| 37 | Прочетете типа като беззнаков байт. Например `0xff` става 255. |
| 38 | Прочетете четирибайтовата дължина и я проверете преди създаване на масив. |
| 39 | Вземете първите осем байта от полезния товар за `id`. |
| 40 | Заделете място само за останалата част. При `PING` размерът на този масив е нула. |
| 41 | `readFully` чака всички байтове или хвърля изключение при преждевременен край на потока. |
| 42 | Предайте на приложението вече разглобените полета. |

<a id="echo-probe"></a>

#### Първа проверка, преди да има стая

Запишете [целия Frame.java](#code-frame) в `src/labnet` и следната програма като `EchoProbe.java` в корена на проекта. Той използва произволен свободен порт на localhost и приключва след един обмен.

```java
import labnet.Frame;
import java.io.DataInputStream;
import java.net.*;

class EchoProbe {
    public static void main(String[] args) throws Exception {
        var host = InetAddress.getLoopbackAddress();
        try (var listener = new ServerSocket(0, 1, host);
             var sender = new Socket(host, listener.getLocalPort());
             var receiver = listener.accept()) {
            var request = new Frame(Frame.ECHO, 2, Frame.bytes("Чат"));
            sender.getOutputStream().write(request.encode().array());
            var received = Frame.read(new DataInputStream(receiver.getInputStream()));
            var reply = new Frame(Frame.DATA, received.id(), received.body());
            receiver.getOutputStream().write(reply.encode().array());
            var result = Frame.read(new DataInputStream(sender.getInputStream()));
            System.out.println(result.id() + " " + Frame.text(result.body()));
        }
    }
}
```

В терминал в корена на проекта изпълнете двете команди.

```powershell
javac -encoding UTF-8 -d out src/labnet/Frame.java
java -cp out EchoProbe.java
```

`listener` избира порт, `sender` се свързва, а `accept()` връща сървърния край на същата връзка. `request` съдържа текста преди сериализация. Първият `write` изпраща кадъра, а първият `Frame.read` го възстановява при получателя. Сървърният край сменя типа на `DATA` и запазва идентификатора и тялото. Вторите `write` и `Frame.read` връщат отговора. Очакваният единствен ред е `2 Чат`.

### 4. Добавете конзолите на основните приложения

След успешния обмен отделете клиента и сървъра в два класа с `main`. Започнете с четене от конзолата и избор на порт. Общите настройки по-долу се използват при всяко упражнение.

```java title="src/labnet/Options.java · редове 1–20"
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

| Ред | Какво означава в тази стъпка |
|---|---|
| 1–6 | `Options` е в основния пакет. Картата свързва име като `port` с подадената текстова стойност. |
| 7–8 | Конструкторът получава аргументите от `main`. Нечетен брой означава опция без стойност. |
| 9–11 | Обхождайте по двойки. Проверете префикса `--` и запазете името без префикса с непосредствено следващата стойност. |
| 14 | При липсващ ключ върнете зададената стойност по подразбиране. |
| 15–16 | За числова опция преобразувайте избрания текст в `int`. |
| 17–18 | Отхвърлете стойност извън включителните граници и върнете валидното число. |

```java title="src/labnet/ConsoleIO.java · редове 1–17"
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

| Ред | Какво означава в тази стъпка |
|---|---|
| 1–6 | Внесете класовете за потоци и UTF-8. Съберете операциите за конзолата в общ клас. |
| 7 | При пренасочен изход този писател използва UTF-8 и изпразва буфера при `println`. |
| 8 | Частният конструктор оставя помощните методи достъпни статично. |
| 9–11 | В IntelliJ или при пренасочване четете `System.in` като UTF-8. Когато Java има терминална конзола, вземете нейния четец. `BufferedReader` предоставя `readLine()`. |
| 13–15 | Изведете целия ред през терминалния писател или през UTF-8 писателя за пренасочен изход. |

```java title="src/labnet/ChatServer.java · редове 149–160"
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

| Ред | Какво означава в тази стъпка |
|---|---|
| 149–150 | JVM влиза в `main`. Разчетете опциите от терминала. |
| 151 | Създайте един сървър в `try` с автоматично затваряне. TCP портът по подразбиране е 9000. Параметърът за UDP порт 9001 ще потрябва при упражнение 3. Порт 0 избира свободен порт. |
| 152 | Покажете действителните портове, след като конструкторът е отворил услугите. |
| 153–154 | Четете редове до край на входа или до сървърната команда `/quit`. |
| 155–156 | Подайте един ред на `console`, покажете резултата и обработете грешка само за текущата команда. |

```java title="src/labnet/ChatClient.java · редове 131–146"
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

| Ред | Какво означава в тази стъпка |
|---|---|
| 131 | При затваряне приключете всяка локална връзка и изчистете картата. Тя получава съдържание от упражнение 2. |
| 132–135 | Клиентът чете хост и портове. Стойностите по подразбиране сочат към местния сървър. Клиентът трябва да знае вече избран порт, затова долната граница е 1. |
| 136 | Покажете командите за начало. |
| 137–138 | При `--name ana` директно отворете връзка. Без аргумента конзолата изчаква `/new`. |
| 139–140 | Четете по един ред, докато входът е отворен. |
| 141–142 | `command` връща `false` при край. Изведете грешката от единична команда и приемете следващия ред. |

**Проверка в завършения проект.** След компилация изпълнете `java -cp out labnet.demo.Demo 1`. Очаквайте отделни отговори `ECHO #2 -> Чат`, `ECHO #3 -> Б` и `ECHO #4 -> В`, после `PASS lab01`. В тази проверка заявка № 1 вече е използвана от `AUTH`. Следващата стъпка е да задържите три връзки едновременно и да разпратите един и същ текст към тях.

Във вече свързан клиент опитайте и `/ping`, последвано от `/echo Чат`. Първата заявка получава `OK`. При втората проверете текста и номера на върнатия кадър.

<a id="lab-2"></a>

## Упражнение 2 — Една стая и три независими клиента

**След TCP обмена.** В упражнение 1 клиентът изчаква само своя отговор. Сега Ана трябва да вижда съобщения и когато Борис пише, без да натиска Enter. Първо добавете име към кадъра, после регистър на сървъра и независим четец за всяка клиентска връзка.

### 1. Опаковайте името заедно с текста

Добавете `Named` вътре във `Frame`. За събитие от Ана структурата е: един байт със стойност 3, трите ASCII байта на `ana`, после всички UTF-8 байтове на текста. Получателят знае откъде започва текстът, дори той да съдържа интервали.

```java title="src/labnet/Frame.java · редове 45–65"
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

### 2. Регистрирайте участниците и освободете името при излизане

Създайте `Room` веднъж на сървъра. За всяка приета връзка създавайте отделен `Peer`. Физическата връзка влиза в `connected` при приемане, а в `members` влиза едва след успешен `LOGIN`. Така `/users` показва хората в стаята.

```java title="src/labnet/Room.java · редове 12–26"
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

```java title="src/labnet/Room.java · редове 33–58"
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

### 3. Разпратете едно събитие и върнете едно потвърждение

Това е мястото, на което чатът става общ. При три участника сървърът създава един `EVENT` и нарежда три записа. След тях връща един `DATA` само на автора. Подателят също трябва да фигурира в снимката на стаята.

```java title="src/labnet/Room.java · редове 60–75"
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

```java title="src/labnet/Room.java · редове 77–103"
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

```java title="src/labnet/Room.java · редове 104–118"
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

```java title="src/labnet/Room.java · редове 149–153"
    private CompletableFuture<Boolean> error(Peer peer, long id, String code) {
        errors.increment();
        return peer.send(new Frame(ERROR, id, bytes(code)));
    }
}
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 149–151 | Съберете отговорите за грешка на едно място. Увеличете брояча и изпратете `ERROR` с номера на първоначалната заявка и текстов код. |

### 4. Дайте на всяка връзка четец и собствена номерация

Създайте `ChatConnection`. Главната конзола подава команди, докато една отделна нишка чете целия вход от сокета. Така присъстващият клиент може да види публикация по време на въвеждане.

```java title="src/labnet/blocking/ChatConnection.java · редове 12–30"
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

```java title="src/labnet/blocking/ChatConnection.java · редове 41–72"
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

```java title="src/labnet/blocking/ChatConnection.java · редове 73–91"
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

### 5. Свържете три такива връзки с една конзола

Първо добавете картата и избора на подател, после командите. В обединения проект `open` е и мястото за две по-късни добавки. В упражнение 3 показаният ред задейства UDP брояча. В упражнение 6 преди `LOGIN` се изпраща `AUTH`.

```java title="src/labnet/ChatClient.java · редове 16–56"
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

```java title="src/labnet/ChatClient.java · редове 58–84"
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

```java title="src/labnet/ChatClient.java · редове 92–113"
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

```java title="src/labnet/ChatClient.java · редове 124–130"
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

<a id="lab-3"></a>

## Упражнение 3 — UDP брояч с повторно потвърждение

**След първия разговор.** Добавете отчет колко чат събития е показала Ива. Ако UDP потвърждението се изгуби, клиентът ще изпрати повторно същата операция. Сървърът трябва да върне същата сума. Работете в `labnet.udp.Counter`. Съществуващият чат продължава да пренася текста по TCP.

### 1. Задайте границите на една дейтаграма

Добавете вложения запис `Packet` преди изпращача и приемника. И двата ще го използват. При `delta=+1` дейтаграмата съдържа 19 байта заглавие и осем байта тяло.

```java title="src/labnet/udp/Counter.java · редове 11–29"
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

### 2. Добавете приемника в сървърния процес

Пазете по едно състояние за всяка комбинация от адрес, порт на изпращача и номер на сесията. Всички повторения от един клиент трябва да достигат едно и също състояние.

```java title="src/labnet/udp/Counter.java · редове 31–58"
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

```java title="src/labnet/udp/Counter.java · редове 59–79"
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

### 3. Изпратете една операция и повторете само при липса на ACK

Първо изградете DATA пакета. После го изпращайте в цикъла за опити. Ако увеличите `seq` вътре в този цикъл, сървърът ще възприеме повторението като ново показано съобщение.

```java title="src/labnet/udp/Counter.java · редове 82–111"
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

### 4. Свържете брояча към показването на събитие

Добавете `Monitor` след работещия обмен DATA → ACK. Четецът на чата трябва да може да покаже следващ ред, докато предишната UDP операция изчаква повторение.

```java title="src/labnet/udp/Counter.java · редове 114–132"
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

```java title="src/labnet/ChatServer.java · редове 39–45"
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

```java title="src/labnet/ChatServer.java · редове 134–138"
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

В `ChatClient.open` добавете по един `Monitor` на ред 36. На редове 39–41 първо отпечатайте текста и чак след това извикайте `displayed()`. Така и тримата участници в една клиентска конзола имат собствени числа. [ChatClient.java · редове 35–46](#code-client)

**Проверка.** На сървъра въведете `/drop-ack 3`. В нови клиентски сесии Ана изпраща три реда след влизането на Ива. При Ива третият текст се показва веднъж и третият UDP резултат е `Receipt[seq=3, total=3, attempts=2]`. Върнете `/drop-ack 0`. Автоматичният опит е `java -cp out labnet.demo.Demo 3`. Броячът се отнася за тази измервателна сесия. След повече от 60 секунди неактивност приемникът може да е забравил предишния номер.

<a id="lab-4"></a>

## Упражнение 4 — Частично четене и запис с Java NIO

**След работещата стая.** Запазете `Room.handle` като обработчик на вече сглобен кадър. Сега заменете изчакването в `readFully` за NIO връзките с буфер за всяка сесия. Една нишка трябва да може да премине към Борис, докато липсват последните два байта от кадъра на Ана.

### 1. Дайте на сървъра един слушащ канал и един селектор

В крайния код и трите модела приемат връзките през същия порт. Моделът се избира за новата връзка при `accept()`. По-долу полетата за TLS и работниците са показани на окончателните им места, за да можете да свържете надгражданията от упражнения 5 и 6.

```java title="src/labnet/ChatServer.java · редове 13–31"
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

```java title="src/labnet/ChatServer.java · редове 46–58"
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

```java title="src/labnet/ChatServer.java · редове 60–85"
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

```java title="src/labnet/ChatServer.java · редове 86–110"
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

### 2. Дайте на всяка NIO връзка собствени буфери

Първо упражнете сглобяването на приложен кадър с `plain`. В завършения защитен транспорт има още два буфера. `netIn` и `netOut` държат TLS данните върху канала, а `plain` получава дешифрираните байтове за LabNet.

```java title="src/labnet/transport/SelectorSession.java · редове 11–43"
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

### 3. Сглобете кадъра, без да загубите недочетения остатък

Нека първо дойдат шестте байта на заглавието и само част от полезния товар. Ако консумирате заглавието окончателно, при следващото четене ще вземете байтове от тялото за версия и тип. Затова поставете отметка преди заглавието.

```java title="src/labnet/transport/SelectorSession.java · редове 45–60"
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

### 4. Довършете записа преди да го отчетете

След като шифрованите байтове са в `netOut`, каналът може да приеме само част от тях. Този код пази позицията между две извиквания на `pump`. В упражнение 5 ще добавите контрол на опашката в началото на същия метод.

```java title="src/labnet/transport/SelectorSession.java · редове 87–100"
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

```java title="src/labnet/transport/SelectorSession.java · редове 124–134"
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

```java title="src/labnet/ChatServer.java · редове 119–124"
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

<a id="lab-5"></a>

## Упражнение 5 — Бавен получател и ограничена работа

**След частичния запис.** Нека Ива временно спре да чете. Ана и Борис трябва да продължат разговора, а сървърът да знае за кои публикации е приключил записът. Добавете обща изходяща опашка в `Session`, преди да въвеждате демонстрационното задържане и асинхронния `WORK`.

### 1. Опишете какво чака в опашката на един получател

```java title="src/labnet/transport/Session.java · редове 12–41"
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

### 2. Добавете две различни операции — приемане за запис и завършен запис

В `send` върнете незавършен резултат веднага след поставянето в опашката. В `written` го завършете едва след изписване на кадъра. Така броячът в `Room.publish` може да отчете какво действително е станало при всеки получател.

```java title="src/labnet/transport/Session.java · редове 42–66"
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

```java title="src/labnet/transport/Session.java · редове 67–98"
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

```java title="src/labnet/transport/SelectorSession.java · редове 80–87"
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

### 3. Направете бавния получател възпроизводим

Свържете `/slow iva` в конзолата на сървъра с `Session.slow`. След това добавете в клиента три изпращания без междинно чакане. Ако Ана чака първия отговор преди втората публикация, задържаната опашка на Ива няма да достигне третия кадър.

```java title="src/labnet/ChatServer.java · редове 125–133"
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

```java title="src/labnet/ChatClient.java · редове 119–123"
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

### 4. Изнесете бавната приложна работа в отделен пул

Добавете `WORK` към типа кадър и в `Room.handle`. Клиентът задава цена 0…50 ms и текст. Селекторът трябва да може да обслужи `PING` от друг клиент, докато работникът изчаква.

```java title="src/labnet/Room.java · ред 27"
    private final ThreadPoolExecutor workers = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(64));
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 27 | Четири работника ползват опашка за 64 чакащи задачи. Ограничението е общо за стаята. |

```java title="src/labnet/ChatClient.java · редове 114–118"
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

```java title="src/labnet/Room.java · редове 135–148"
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

<a id="lab-6"></a>

## Упражнение 6 — TLS, доверие и вход по име

**След общата стая и ограниченията.** При връзка от Ана има три последователни събития. Първо TLS установява защитена връзка с проверено име на сървъра. После `AUTH` проверява учебния токен на Ана. Накрая `LOGIN` заема името `ana` в стаята. Запазете `Frame`, `Room.publish` и обработката на съобщенията и за двата транспорта.

### 1. Подгответе локалните ключове преди сървърът да започне да приема

Създайте `DemoKeys`. За локалния опит сървърът и клиентът се пускат от една работна директория. `server.p12` съдържа частния ключ на сървъра. Клиентът зарежда `trust.p12` с доверения публичен сертификат и токена на избраното учебно име.

```java title="src/labnet/tls/DemoKeys.java · редове 11–31"
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
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 12–13 | Запишете трите начални имена и пътя под текущата работна директория. |
| 15–18 | Създайте 24 случайни байта със `SecureRandom` и ги представете като 48 шестнадесетични знака. Така се създават паролата за хранилището и отделните токени. |
| 21–23 | Методът изпълнява инструмента на JDK. Изберете името му за съответната операционна система. |
| 24–26 | Започнете списък с аргументи от пълния път в текущия `java.home` и добавете операцията, например генериране на ключ. |
| 27 | Опцията `-storepass:env` казва на инструмента да вземе паролата от променлива на средата. Типът на хранилището е PKCS12. |
| 28 | Създайте процеса с правилната работна директория и свържете стандартните му потоци с родителския процес. |
| 29 | Задайте паролата в средата на дъщерния процес. |
| 30 | Изчакайте резултата от `keytool`. При ненулев изход прекратете подготовката. |

```java title="src/labnet/tls/DemoKeys.java · редове 33–54"
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
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 33–34 | При вече съществуваща директория опитайте да заредите токените и двата TLS контекста. Така следващото стартиране използва същата идентичност. |
| 35–36 | Създайте родителската директория и временна директория за текущата подготовка. |
| 38 | Генерирайте общата локална парола за хранилищата. |
| 39–40 | Създайте RSA двойка с ключ 2048 бита и самоподписан сертификат за 365 дни. SAN включва `localhost` и `127.0.0.1` за проверката на името. |
| 41 | Изнесете само сертификата в `server.cer`. |
| 42 | Внесете този конкретен сертификат в учебното хранилище на доверие. |
| 43–44 | Запишете локалната парола и различен, нов токен за всеки от тримата. |
| 45 | Преместете подготвената директория на окончателното място след успешното създаване на всички файлове. |
| 46–49 | При останала временна директория обходете и изтрийте файловете от тази подготовка. |
| 51 | Премахнете изпразнената временна директория. |

```java title="src/labnet/tls/DemoKeys.java · редове 56–85"
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
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 56–58 | Проверете учебното име и образувайте пътя до неговия файл. |
| 59–60 | Сигнализирайте липсващ токен или върнете байтовете му. |
| 62–63 | За име на локален файл са позволени малки латински букви, цифри, `_` и `-` с начална буква и до 24 знака. `server` е запазено. |
| 65–67 | При създаване на участник проверете името и използвайте `CREATE_NEW`, за да не замените съществуващ токен. |
| 68–69 | Ако файлът вече съществува, прочетете и върнете наличния токен. |
| 71–74 | Подгответе карта и обходете само файловете с разширение `.token`. |
| 75–77 | Отделете името от разширението и го свържете със стойността от `token(name)`. |
| 80 | Върнете картата за сървърната проверка на AUTH. |
| 82–85 | Създайте PKCS12 обект, заредете посочения файл с паролата и затворете входния поток. |

```java title="src/labnet/tls/DemoKeys.java · редове 87–107"
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

| Ред | Какво означава в тази стъпка |
|---|---|
| 87–90 | Сървърът прочита паролата, създава фабрика за ключови мениджъри и я инициализира с `server.p12`. |
| 91–93 | Създайте TLS 1.3 контекст за представяне на сървърния сертификат и върнете този контекст. |
| 95–97 | Клиентът създава фабрика за доверие. За нормална връзка зарежда локалното `trust.p12`. |
| 98–101 | Отрицателната проверка инициализира празно хранилище. Така същият валиден сървърен сертификат е недоверен за този тестов клиент. |
| 103–105 | Клиентският контекст използва получените мениджъри за доверие. Удостоверяването на човека ще стане с AUTH след TLS. |

```java title="src/labnet/ChatServer.java · редове 33–38"
    public ChatServer(int port, int udpPort) throws Exception {
        keys.prepare();
        for (int n = 1; n <= 16; n++) keys.provision("bench" + n);
        tls = keys.serverContext();
        room = new Room(keys.tokens());
        selector = Selector.open();
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 33–34 | При създаване на единствения сървър подгответе учебните ключове. |
| 35 | При добавяне на упражнение 7 осигурете акаунти `bench1`…`bench16` преди зареждане на картата. |
| 36–37 | Заредете сървърния TLS контекст и подайте токените на една обща стая. |
| 38 | Едва с готовия контекст продължете към отваряне на селектора. |

```java title="src/labnet/Room.java · редове 29–32"
    public Room(Map<String, byte[]> tokens) {
        this.tokens = new ConcurrentHashMap<>(tokens);
    }
    public void provision(String name, byte[] token) { tokens.put(name, token); }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 29–30 | В конструктора направете конкурентна карта от началните токени. |
| 32 | При добавяне на участник запишете очакваната стойност за новото име. |

```java title="src/labnet/ChatServer.java · редове 112–118"
    public String console(String line) throws Exception {
        String[] words = line.split(" ", 2);
        if (line.equals("/stats")) return room.stats() + ";newConnections=" + model;
        if (line.equals("/help")) return "/stats, /user name, /io nio|thread|pool, /chunk 1..65536, /slow name|off, /drop-ack seq, /quit";
        if (words.length == 2) {
            switch (words[0]) {
                case "/user" -> { room.provision(words[1], keys.provision(words[1])); return "User ready: " + words[1]; }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 112–113 | Разделете командата от остатъка на реда. При сървърна публикация ще използвате оригиналния цял ред. |
| 114–115 | `/stats` показва и модела за нови връзки, а `/help` изброява сървърните команди. |
| 116–118 | За `/user maria` осигурете локален токен, въведете го в стаята и потвърдете готовността на името. |

### 2. Обвийте съществуващия TCP сокет и проверете името

В `ChatConnection` вече имате успешно свързан `plain`. Добавете следния блок преди да вземете входния и изходния поток. В основния клиент `ChatClient` винаги подава доверения TLS контекст.

```java title="src/labnet/blocking/ChatConnection.java · редове 31–40"
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
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 31–32 | Обвийте свързания сокет с клиентски `SSLSocket` за действителния хост. Последният аргумент обвързва затварянето и на основния сокет. |
| 33–34 | Оттук нататък обвивката е активната връзка. Получете параметрите ѝ за проверка. |
| 35 | `HTTPS` тук е името на алгоритъм за проверка на идентичността на хоста. Приложният протокол продължава да е LabNet. |
| 36 | Приложете параметрите преди началото на договарянето. |
| 37–38 | Задайте ограничение за блокиращо четене при договарянето и започнете TLS обмена. |
| 39 | Върнете неограничено изчакване за обичайното четене на клиента, за да може той спокойно да слуша чужди публикации. |

```java title="src/labnet/transport/BlockingSession.java · редове 10–36"
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
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 10–12 | Блокиращата сесия може да бъде подадена на нишка като `Runnable`. Съхранява TLS сокет и изпълнител за таймерите. |
| 13–16 | Приемете общите ресурси, предайте ги към `Session` и запазете таймерите. |
| 17 | Обвийте вече приетия сокет и свържете затварянето му с обвивката. |
| 18–19 | Изберете сървърната роля и TLS 1.3. |
| 21–24 | В обработващата нишка планирайте затваряне след три секунди. Отменете таймера при излизане от `startHandshake`. |
| 25 | Освободете общото разрешение за договаряне след успех. |
| 26–27 | Стартирайте един писател за тази връзка и подгответе блокиращия четец на кадри. |
| 28–30 | Докато участникът е активен, изчакайте първия байт. Едва след него започнете двусекундния срок за довършване на кадъра. |
| 31–33 | Подайте прочетената версия към общия декодер, отменете таймера и предайте готовия кадър на `dispatch`. |
| 35–36 | При нормален QUIT завършването на отговора ще затвори връзката. При грешка в четенето или договарянето я затворете тук. |

```java title="src/labnet/transport/BlockingSession.java · редове 38–51"
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

| Ред | Какво означава в тази стъпка |
|---|---|
| 38–41 | Вземете изходния поток и изчакайте достъпен елемент от общата опашка. |
| 42–44 | Планирайте затваряне при блокирал запис над две секунди. Изпратете целия кодиран масив и отменете таймера. |
| 45 | Обявете успеха на конкретния кадър след завършилото `write`. |
| 47 | При I/O грешка, прекъсната нишка или неочаквана грешка затворете сесията и приключете чакащите записи. |
| 49 | Блокиращият писател се събужда от `notifyAll` на опашката, затова тук не се изисква отделен сигнал. |
| 50 | Затворете TLS сокета на този участник. |

### 3. Добавете TLS към NIO сесията

Селекторът от упражнение 4 получава шифрованите записи. `SSLEngine.unwrap` извлича от тях приложните байтове за вече построения декодер. В другата посока `wrap` превръща част от LabNet кадъра в TLS запис.

```java title="src/labnet/transport/SelectorSession.java · редове 61–79"
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
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 61–63 | Превключете мрежовия вход за четене. `unwrap(netIn, plain)` консумира наличните TLS байтове и добавя разчетеното в приложния буфер. |
| 64 | Съберете неизползваните мрежови байтове и подгответе място за следващо четене. |
| 65–66 | Проверете статуса. При `CLOSED` затворете сесията. |
| 67 | `BUFFER_OVERFLOW` означава недостатъчно място в приложния буфер. Този учебен транспорт прекратява връзката с грешка. |
| 68 | При `BUFFER_UNDERFLOW` е необходим още мрежов вход. Ако целият входен буфер вече е запълнен, откажете прекалено големия запис. |
| 69 | При нормален резултат задвижете LabNet декодера само ако са произведени приложни байтове. |
| 71–72 | Върнете дали има напредък по данните или договарянето. Цикълът решава дали да продължи веднага. |
| 74–75 | За изходящ TLS запис подгответе празен буфер за запис. |
| 76–77 | Шифровайте следващата част от източника и превключете изходния буфер за четене от `SocketChannel`. |
| 78 | Неочакван статус за запис води до грешка за сесията. |

```java title="src/labnet/transport/SelectorSession.java · редове 101–124"
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
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 101–103 | Попитайте TLS машината каква стъпка изисква. При `NEED_TASK` вземете делегираната задача. |
| 104–105 | Проверете дали задачата е действително налична и маркирайте машината като заета. |
| 107–110 | Изпълнете я в ограничения пул за договаряне. При край възстановете достъпа до машината и събудете селектора. При отказ от пула затворете сесията. |
| 112 | При `NEED_WRAP` подайте празен приложен буфер, защото TLS има собствени данни за изпращане. |
| 113 | При `NEED_UNWRAP` или `NEED_UNWRAP_AGAIN` извикайте `unwrap`. При нужда от още байтове обновете интересите и върнете управлението. |
| 114–115 | При установено договаряне маркирайте връзката и еднократно освободете разрешението. |
| 116 | Ако вече има входни байтове и четенето е разрешено, опитайте да ги обработите. |
| 117–118 | Вземете първия готов изходящ кадър. При празна или задържана опашка върнете управлението. |
| 119–120 | Шифровайте наличните приложни байтове. Когато те се изчерпат, помнете записа във `flushed` до действителното изписване на `netOut` в упражнение 4. |
| 124 | Преизчислете интереса към четене и писане. |

### 4. Удостоверете участника преди да заемете име

В `ChatClient.open` подайте тялото `Named(name, token).pack()` към AUTH и изчакайте `OK`. Едва тогава подайте `LOGIN`. На сървъра добавете следния метод и го извикайте от `Room.handle` на ред 90. [Последователността в ChatClient.java](#code-client)

```java title="src/labnet/Room.java · редове 120–134"
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
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 120–121 | При повторно AUTH за вече установена самоличност върнете `ALREADY_AUTHENTICATED`. |
| 122–123 | Разопаковайте заявеното име и байтовете на токена. |
| 124–125 | Изисквайте от 16 до 128 байта и валидно UTF-8 кодиране. |
| 126 | Намерете очакваните байтове по заявеното име. |
| 127–128 | При непознато име или несъвпадение по `MessageDigest.isEqual` върнете общия код `AUTH_FAILED`. |
| 129 | След трети неуспех по тази връзка насрочете затварянето ѝ след завършване на отговора. |
| 130 | Върнете резултата от записа на грешката. |
| 132–133 | Запомнете удостовереното име в сесията и върнете `OK`. При `LOGIN` ред 50 го сравнява с исканото име в стаята. |

**Проверка.** В терминал от `chat-solution` изпълнете `java -cp out labnet.demo.Demo 6`. Тримата удостоверени участници трябва да получат съобщението „Защитен чат“. Клиент с грешен токен получава `AUTH_FAILED`. При празно хранилище на доверие и при проверка за друго име тестът отчита отказ още при TLS договарянето. Очаквайте `PASS lab06`. Учебната настройка съхранява ключовете и токените локално в `.local/tls`.

<a id="lab-7"></a>

## Упражнение 7 — Измерване на доставката

**След доказана доставка и удостоверяване.** Измервайте същата операция при три начина за обслужване на връзка. Пуснете един сървър, избирайте модела за новите връзки и отваряйте нови измервателни клиенти за всеки опит. При три клиента с по десет публикации целта е 30 успешни заявки и 90 уникални получавания.

### 1. Подайте измерването от съществуващата клиентска конзола

```java title="src/labnet/ChatClient.java · редове 85–90"
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

```java title="src/labnet/demo/Benchmark.java · редове 118–125"
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

### 2. Постройте предварително точната множина на очакваните събития

Съобщението `post-00001` от `bench1` е различно от същия текст от `bench2`. В рамките на една входяща кутия ключът е двойката автор и текст. Използвайте различна кутия за всеки получател.

```java title="src/labnet/demo/Benchmark.java · редове 17–41"
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

### 3. Отворете клиентите и проверете всяко получаване

```java title="src/labnet/demo/Benchmark.java · редове 42–64"
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

### 4. Подайте публикациите и отделете отговор от събитие

Отговорът `DATA` се проверява при подателя, а събитията се проверяват в обработчиците на получателите. Един успешен `DATA` при три измервателни клиента изисква още три отделни наблюдения в техните входящи кутии.

```java title="src/labnet/demo/Benchmark.java · редове 65–85"
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

### 5. Докажете доставката и запишете CSV

```java title="src/labnet/demo/Benchmark.java · редове 86–106"
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

```java title="src/labnet/demo/Benchmark.java · редове 107–116"
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

### 6. Приключете общия сървър

След последния опит затворете клиентите и въведете `/quit` в сървърната конзола. При `try` с ресурс в `main` това стига до `ChatServer.close`.

```java title="src/labnet/ChatServer.java · редове 141–148"
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

**Проверка.** На сървъра подайте `/io thread`, а на клиента `/bench 3 10`. Повторете със `/io pool` и `/io nio`, като изчаквате края на всеки опит. Дръжте броя на останалите участници постоянен. В трите реда на `results/chat.csv` очаквайте `published=30`, `expected_events=90`, `received=90`, `missing=0`, `duplicates=0` и `own_samples=30`. Сравнявайте измерените стойности за скорост и p95 от собственото изпълнение. Автоматичният сценарий `java -cp out labnet.demo.Demo 7` изпълнява трите модела срещу един сървър и приключва с `PASS lab07`.

След успешния опит с трима участници изпълнете `/bench 6 10`. Шестимата измервателни клиенти трябва да публикуват общо 60 съобщения и да получат общо 360 събития. За съпоставими опити задайте `/chunk 65536` и запазете еднакъв състав на стаята за трите модела. Генераторът използва учебните имена от `bench1` до `bench16`. В CSV колоната `p95_own_ms` отчита в милисекунди 95-ия персентил на времето от изпращането до получаването на собственото събитие.

<a id="chat-demo"></a>

## Демонстрация с Ана, Борис и Ива

1. Отворете `ChatServer.main()` и натиснете зелената стрелка. Изчакайте `ChatServer ready`. При първото стартиране сървърът създава локалния сертификат за `localhost` и отделните учебни токени. По подразбиране използва TCP порт 9000 за чата с TLS и UDP порт 9001 за броячите.
2. Стартирайте `ChatClient.main()` веднъж. Въведете командите по-долу в клиентската конзола, като изчакате `Connected` за всяко име. Всяка връзка има свой четец, номера на заявки и UDP сесия. `/use` избира кой ще пише, докато и трите връзки продължават да получават.

```text
/new ana
/new boris
/new iva
/use ana
Здравейте!
```

Проверете кой е получил съобщението по префикса пред `<`. Името в квадратните скоби е авторът. След `AUTH` и `LOGIN` първата публикация на Ана е заявка № 3. Редът на извеждане може да се различава. Между събитията могат да се появят и потвърждения по UDP.

```text
ana < [ana] Здравейте!
boris < [ana] Здравейте!
iva < [ana] Здравейте!
ana DATA #3 delivered=3;failed=0
```

3. Напишете `Занятието започва.` в **сървърната** конзола. При всеки от тримата авторът трябва да е `[server]`. В клиентската конзола сравнете `/clients` (връзките, отворени тук) и `/users` (участниците в стаята).
4. Изберете `/use iva` и въведете `/quit`. Изберете `/use ana` и изпратете `До утре!`. Вече виждате два получени реда и `delivered=2;failed=0`. След `/new iva` Ива отново участва и получава следващите публикации. За ново учебно име използвайте `/user maria` на сървъра и `/new maria` при клиента.
5. За три отделни конзоли в готовия проект стартирайте конфигурацията **ChatClient** три пъти и отворете различно име във всяка. От PowerShell в папката на готовия проект `.\show-chat.ps1` пуска един сървър и три клиента в отделни прозорци. Ако вече сте пуснали ръчната демонстрация, завършете я с `/exit` при клиента и `/quit` при сървъра, преди да стартирате скрипта.

### Проследете съобщението на Ана в кода

При обикновен текст от Ана проследете тези шест места в този ред:

1. `ChatClient.command` избира текущата връзка и извиква `request(BROADCAST, …)`.
2. `ChatConnection.submit` записва бъдещия отговор по номер и изпраща кадъра.
3. `Session.dispatch` валидира номера, а `Room.handle` предава текста и името от сесията към `Room.publish`.
4. `Room.publish` избира участниците. `Session.send` поставя по един EVENT в опашката на всеки, включително Ана.
5. `Session.written` завършва отделния запис. След всички записи сървърът подава DATA с броячите към Ана.
6. При всеки получател `ChatConnection.receive` разчита EVENT, конзолата показва реда и `Counter.Monitor.displayed` подава UDP измерването. DATA при Ана завършва чакащата заявка. Получателите четат независимо, така че терминалите могат да отпечатат тези резултати в различен ред.

<a id="chat-build"></a>

## Компилирайте пълната версия от празен проект

Ако създавате проекта от кода на страницата, добавяйте файловете по групите в таблицата. След всяка група изпълнявайте съответната команда от блока под нея. Така `javac` може да намери всички типове, от които зависи следващият клас.

| Стъпка | Какво да създадете | Какво вече можете да проверите |
|---|---|---|
| 1 | `Frame`, `Options` и `ConsoleIO` в основния пакет. | Изпълнете краткия `EchoProbe` от упражнение 1. Очакваният резултат е `2 Чат`. |
| 2 | `Room` и `blocking/ChatConnection`. | Стаята вече приема абстрактен `Room.Peer`, а клиентската връзка разпознава `EVENT` и отговор по номер. И двата класа използват `Frame`. |
| 3 | `udp/Counter` и `tls/DemoKeys`. | Вече имате типовете за отчитане на показан ред и за зареждане на двата TLS контекста. |
| 4 | Първо `transport/Session`, после `BlockingSession` и `SelectorSession` в същия пакет. | Двата транспорта могат да предадат кадър към една и съща стая и да потвърдят завършен запис. |
| 5 | `ChatServer` в основния пакет и `demo/Benchmark`. | Стартирайте сървъра. Изчакайте `ChatServer ready` и въведете `/stats`. Преди вход на клиент очаквайте `active=0;loggedIn=0`. |
| 6 | `ChatClient` в основния пакет. | Пуснете клиента от същата директория и влезте като Ана, Борис и Ива. Всеки трябва да види първия ред на Ана точно веднъж. |

Изпълнявайте съответния ред от PowerShell в корена на новия проект след всяка стъпка. `-cp out` прави вече компилираните типове достъпни за следващата команда. В следващия раздел ще намерите всеки файл с декларацията на пакета и всички необходими `import` редове.

```powershell
javac --release 21 -encoding UTF-8 -d out src/labnet/Frame.java src/labnet/Options.java src/labnet/ConsoleIO.java
javac --release 21 -encoding UTF-8 -cp out -d out src/labnet/Room.java src/labnet/blocking/ChatConnection.java
javac --release 21 -encoding UTF-8 -cp out -d out src/labnet/udp/Counter.java src/labnet/tls/DemoKeys.java
javac --release 21 -encoding UTF-8 -cp out -d out src/labnet/transport/Session.java src/labnet/transport/BlockingSession.java src/labnet/transport/SelectorSession.java
javac --release 21 -encoding UTF-8 -cp out -d out src/labnet/ChatServer.java src/labnet/demo/Benchmark.java
javac --release 21 -encoding UTF-8 -cp out -d out src/labnet/ChatClient.java
```

Накрая добавете `Demo.java` в `test/labnet/demo` и го компилирайте срещу готовите класове. Стартирайте проверките от същата папка.

```powershell
javac --release 21 -encoding UTF-8 -cp out -d out test/labnet/demo/Demo.java
java -cp out labnet.demo.Demo all
```

В готовия проект `.\build.ps1` компилира `src` и `test` с една команда. `Demo all` отваря свободни локални портове и извежда по един резултат от `PASS lab01` до `PASS lab07`. За отделен етап заменете `all` с номер от 1 до 7. За ръчната демонстрация отворете два терминала в корена на проекта и пуснете `java -cp out labnet.ChatServer` в първия и `java -cp out labnet.ChatClient` във втория.

<a id="chat-code"></a>

## Пълен код за IntelliJ

Всеки блок по-долу е отделен файл. Пътят над кода показва папката и пакета. Копирайте целия блок, включително вътрешните класове. Двете входни точки са [ChatServer.java](#code-server) и [ChatClient.java](#code-client).

[Frame](#code-frame) · [Options](#code-options) · [ConsoleIO](#code-consoleio) · [Room](#code-room) · [ChatConnection](#code-chatconnection) · [Counter](#code-counter) · [DemoKeys](#code-demokeys) · [Session](#code-session) · [BlockingSession](#code-blockingsession) · [SelectorSession](#code-selectorsession) · [ChatServer](#code-server) · [Benchmark](#code-benchmark) · [ChatClient](#code-client) · [Demo](#code-demo)

<a id="code-frame"></a>

### Frame.java

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

<a id="code-options"></a>

### Options.java

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

<a id="code-consoleio"></a>

### ConsoleIO.java

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

<a id="code-room"></a>

### Room.java

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

<a id="code-chatconnection"></a>

### ChatConnection.java

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

<a id="code-counter"></a>

### Counter.java

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

<a id="code-demokeys"></a>

### DemoKeys.java

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

<a id="code-session"></a>

### Session.java

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

<a id="code-blockingsession"></a>

### BlockingSession.java

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

<a id="code-selectorsession"></a>

### SelectorSession.java

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

<a id="code-server"></a>

### ChatServer.java

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

<a id="code-benchmark"></a>

### Benchmark.java

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

<a id="code-client"></a>

### ChatClient.java

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

<a id="code-demo"></a>

### Demo.java

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
