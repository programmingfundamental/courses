# Упражнение 1 — От „Чат“ до TCP кадър

**Първа работеща версия.** Напишете `Frame` преди сървъра. Клиентът трябва да изпрати „Чат“, а получателят да възстанови точно същите байтове и същия номер на заявката. За този първи опит са достатъчни JDK 21+, един файл от проекта и два локални TCP сокета.

## 1. Определете какво точно пътува по връзката

Създайте пакет `labnet` и файл `Frame.java` в `src/labnet`. На този етап използвайте `ECHO` за заявка и `DATA` за отговор. Имената в общия списък показват и типовете, които ще започнете да обработвате в следващите упражнения.

[Frame.java · редове 1–18](../chat-solution/src/labnet/Frame.java#L1-L18)

```java
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

## 2. Добавете кодиране, после проверка на дължината

Начертайте първо кадъра за `ECHO` с номер 2. „Чат“ заема шест байта в UTF-8. Полето за дължина е 8 + 6 = 14, а по TCP ще изпратим общо 6 + 14 = 20 байта.

[Frame.java · редове 20–31](../chat-solution/src/labnet/Frame.java#L20-L31)

```java
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

## 3. Възстановете кадъра от поток

TCP може да достави заглавието и тялото на отделни четения. Затова първо прочетете полетата с известна ширина, после изчакайте целия обявен брой байтове.

[Frame.java · редове 33–43](../chat-solution/src/labnet/Frame.java#L33-L43)

```java
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

### Първа проверка, преди да има стая

В терминал в `chat-solution` компилирайте `Frame`. Запишете малкия опит по-долу като `EchoProbe.java` в работната директория на опита. Той използва произволен свободен порт на localhost и приключва след един обмен.

```powershell
javac -encoding UTF-8 -d out src/labnet/Frame.java
java -cp out EchoProbe.java
```

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

`listener` избира порт, `sender` се свързва, а `accept()` връща сървърния край на същата връзка. `request` съдържа текста преди сериализация. Първият `write` изпраща кадъра, а първият `Frame.read` го възстановява при получателя. Сървърният край сменя типа на `DATA` и запазва идентификатора и тялото. Вторите `write` и `Frame.read` връщат отговора. Очакваният единствен ред е `2 Чат`.

## 4. Добавете конзолите на основните приложения

След успешния обмен отделете клиента и сървъра в два класа с `main`. Започнете с четене от конзолата и избор на порт. Общите настройки по-долу се използват при всяко упражнение.

[Options.java · редове 1–20](../chat-solution/src/labnet/Options.java#L1-L20)

```java
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

[ConsoleIO.java · редове 1–17](../chat-solution/src/labnet/ConsoleIO.java#L1-L17)

```java
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

[ChatServer.java · редове 149–160](../chat-solution/src/labnet/ChatServer.java#L149-L160)

```java
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

[ChatClient.java · редове 131–146](../chat-solution/src/labnet/ChatClient.java#L131-L146)

```java
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

**Проверка в завършения проект.** От `chat-solution` изпълнете `.\build.ps1` и `java -cp out labnet.demo.Demo 1`. Очаквайте отделни отговори `ECHO #2 -> Чат`, `ECHO #3 -> Б` и `ECHO #4 -> В`, после `PASS lab01`. В тази проверка заявка № 1 вече е използвана от `AUTH`. Следващата стъпка е да задържите три връзки едновременно и да разпратите един и същ текст към тях.

[Съдържание на ръководството](../hint.md) · [Следващо упражнение — Една стая и три независими клиента](../lab02-concurrent-server/hint.md)
