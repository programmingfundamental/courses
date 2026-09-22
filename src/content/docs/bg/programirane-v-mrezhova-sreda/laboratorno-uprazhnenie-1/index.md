---
title: "Лабораторно упражнение 1 — TCP sockets, byte streams и application protocol"
sidebar:
  order: 1
  label: Упражнение 1
---

# Лабораторно упражнение 1 — TCP sockets, byte streams и application protocol

## 1. Контекст и инженерен проблем

Система за лабораторни измервания изпраща последователно два резултата към сървър. На разработчика му изглежда естествено две извиквания на `write()` да доведат до две съобщения при получателя. При различен размер на buffer или по-бавна машина резултатите внезапно се „сливат“ или „разрязват“. Проблемът е в договора на приложението: TCP доставя подреден поток от bytes, а не отделни application messages.

**Какво използваме от предходното упражнение:** това е началният baseline; използваме знанията за Java I/O и TCP/IP. **Какво ще се използва по-късно:** wire format, test vectors, `Message`, encoder и blocking decoder остават общи за concurrent и TLS сървърите; NIO ще промени начина на натрупване на bytes.

## 2. Учебни цели

- Анализира границата между TCP delivery и application framing.
- Реализира binary encoder и decoder с ограничена дължина.
- Различава clean EOF, truncated frame, timeout и protocol error.
- Възпроизвежда fragmentation и coalescing чрез контролирани tests.
- Диагностицира handshake, data transfer и termination в capture.
- Аргументира buffering, flush и graceful close policy.

## 3. Необходими предварителни знания

Java exceptions, try-with-resources, byte arrays, числови типове, UTF-8, client/server и TCP ports. Не е необходимо познаване на NIO.

## 4. Необходими инструменти

JDK 25 (или обща JDK 21 среда), IDE, terminal, Git, Wireshark. Използвайте loopback и TCP port 9000. Осигурете два terminal прозореца и пакет `labnet.protocol`.

## 5. Теоретична подготовка

`ServerSocket.accept()` чака connection; полученият `Socket` има input и output byte stream. Blocking означава, че извикващата нишка може да чака за progress. Успешно локално `write()` не доказва, че отсрещното приложение е обработило заявката. TCP предоставя надежден подреден byte stream, но не запазва границите на application writes. Вижте [TCP specification, §2.2](https://www.rfc-editor.org/rfc/rfc9293.html#section-2.2).

```text
write(frame A) + write(frame B)
              |
        TCP byte stream
              |
read: [A header part] [A rest + B header] [B rest]
              |
         protocol decoder -> A, B
```

Framing е правилото, чрез което от този поток възстановяваме съобщенията. Договорът на LabNet е:

```text
+---------+---------+----------+----------------------+
| VERSION | TYPE    | LENGTH   | PAYLOAD              |
| 1 byte  | 1 byte  | 4 bytes  | LENGTH bytes         |
+---------+---------+----------+----------------------+
PAYLOAD = REQUEST_ID (8 bytes) + BODY (LENGTH - 8 bytes)
```

| Поле | Договор |
|---|---|
| VERSION | unsigned byte, точно 1 |
| TYPE | unsigned byte; стойностите са в таблицата по-долу |
| LENGTH | signed Java int, big-endian; приемаме само `8..65536` |
| REQUEST_ID | big-endian long, `1..Long.MAX_VALUE`; requests нарастват строго по connection; бъдещият EVENT от Lab 2 използва 0 |
| BODY | bytes; текст само когато командата изрично изисква UTF-8 |

Така отрицателните lengths са невалидни, а `MAX_FRAME=6+65536`. За празно body `LENGTH=8`, а не 0. Encoder и decoder споделят едни constants. Валидирането предхожда allocation; frame от непозната версия прекратява connection, без опит за „поправяне“ на потока. Server пази само последния requestId, за да отхвърля ненарастващ ID без неограничен set; при изчерпване на long диапазона client започва нова connection. Ограничението за ред се отнася до requests, не до бъдещите asynchronous responses.

| TYPE | Request body | Отговор |
|---|---|---|
| `PING 0x01` | празно | `OK 0x80`, същия ID, празно body |
| `ECHO 0x02` | произволни bytes до 65528 | `DATA 0x81`, същия ID и body |
| `QUIT 0x03` | празно | `OK`, flush, ограничено graceful close |
| неизвестен | frame и ID са валидни | `ERROR 0xFF`, UTF-8 body `BAD_TYPE` |

Некоректно body на известна команда получава `ERROR/BAD_PAYLOAD`. `ERROR` отговорите са до 256 bytes body. Връзка с невалиден header, ID или truncated payload се затваря; не изпращайте непременно error, ако безопасното framing вече е загубено. Client приема само очакван response type и ID.

`InputStream.read()` може да върне по-малко bytes от поисканите. `DataInputStream.readFully()` или собствен bounded loop решава точното прочитане, но блокира и може да завърши с `EOFException`. `available()` не показва дали цял frame е пристигнал. Вижте [DataInputStream](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/io/DataInputStream.html).

При стандартния blocking `OutputStream.write(byte[])` няма върнат брой записани bytes: при нормално приключване са подадени всички, а при exception може да е изпратена част. Не изпращайте отново целия frame по същата connection след такъв exception. Short writes с числов резултат ще обработваме при NIO в Lab 4. `flush()` изпразва application buffer, но не създава message boundary и не е remote acknowledgement. Вижте [OutputStream](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/io/OutputStream.html).
