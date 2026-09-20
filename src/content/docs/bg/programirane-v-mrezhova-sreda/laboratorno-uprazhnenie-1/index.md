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

## 6. Начален експеримент — 15 минути

1. Направете временен sender с две byte arrays с различим текст и два последователни `out.write(message1); out.write(message2);`. Receiver печата само `read count` и hex bytes, без да ги интерпретира като съобщения.
2. Повторете с receiver buffer 3, 8 и 1024 bytes. При buffer 3 дълго съобщение задължително изисква множество reads.
3. Повторете с един `BufferedOutputStream`: запишете двете arrays и направете един `flush()`. Receiver започва след кратка контролирана пауза. Сливане може да се наблюдава, но не е гарантирано от OS; липсата му не опровергава byte-stream модела.
4. За детерминиран test съединете два encoded frames в `ByteArrayInputStream`, а после ограничете всяко четене до 1 byte чрез wrapper. И в двата случая очаквайте точно две decoded messages, независимо от броя reads.

Запишете `write sizes`, `read sizes`, общи bytes и брой възстановени messages. Не използвайте `sleep()` като „решение“ на framing.

## 7. Основна лабораторна задача — 50 минути

### Стъпка 1 — договор и модел

Създайте `Message`, `ProtocolEncoder`, `ProtocolDecoder`, `TcpClient`, `TcpServer`. `Message` съдържа type, requestId и body; array не трябва да се споделя за промяна между caller и message. Изберете defensive copying или ясно ownership правило и го документирайте.

```java
public interface ProtocolEncoder {
    byte[] encode(Message message) throws ProtocolException;
}
public interface ProtocolDecoder {
    // empty е допустимо само при EOF преди първия byte на нов header.
    Optional<Message> read(InputStream input) throws IOException;
}
```

`ProtocolException` може да наследява `IOException`. Не смесвайте получаване от socket с бизнес обработката в encoder/decoder.

### Стъпка 2 — encoder и test vectors

Използвайте big-endian `DataOutputStream` или `ByteBuffer`. Изчислете дължината след проверката на `body.length <= MAX_PAYLOAD-8`. Пример за `PING`, ID=1:

```text
01 01 00 00 00 08 00 00 00 00 00 00 00 01
```

Напишете round-trip tests за празно ECHO body, UTF-8 bytes на „Мрежа“ и binary body с нулеви bytes. Не използвайте `writeUTF()` — то има собствен формат и не е тази спецификация.

### Стъпка 3 — blocking decoder

Първо прочетете един byte, за да отличите clean EOF. После дочетете останалите 5 header bytes; EOF тук вече е truncated frame. Проверете version/type representation/length **преди** payload allocation. Дочетете payload точно, разчетете ID и валидирайте командата в dispatcher. Не продължавайте след protocol framing error.

### Стъпка 4 — работещ client/server

Baseline сървърът обслужва една accepted connection до нейния край и после приема следващата. Това ограничение е умишлено и ще бъде предмет на Lab 2. Client подава 100 последователни ECHO заявки с различни ID и проверява response bytes. Dispatcher няма достъп до network streams.

Използвайте connect timeout 2000 ms и `setSoTimeout(2000)` за blocking reads. Това е timeout на blocking read, не общ deadline за целия frame и не write timeout. При timeout в средата на frame затворете connection; decoder от тази версия не възобновява частичното четене. Общ deadline ще добавим в Lab 6.

### Стъпка 5 — termination и resources

При `QUIT` server изпраща `OK`, flush-ва и извиква `shutdownOutput()`. Client получава отговора и EOF, затваря своя output и socket. Server изчаква peer EOF най-много 2 s и затваря socket; при нарушение затваря веднага. Обяснете half-close. При I/O error не чакайте нов protocol exchange. Owner на accepted socket е handler-ът в try-with-resources.

## 8. Failure scenarios и edge cases — 20 минути

| Test | Как го предизвиквате | Очакван резултат |
|---|---|---|
| Header на 6 отделни части | по 1 byte във wrapper | точно един frame, без загуба |
| Два frames в един поток | concatenate arrays | два messages в същия ред |
| EOF след 3 header bytes | затворете sender output | truncated header, connection close |
| EOF преди края на payload | обявете 20, изпратете 9 bytes | truncated payload, без dispatch |
| LENGTH=-1, 0, 65537 | raw header от test client | отказ преди allocation |
| VERSION=2 | валиден по размер frame | close без resynchronization |
| Partial frame и пауза >2 s | изпратете header без body | timeout, освободен socket |
| Disconnect докато server отговаря | client close | exception се обработва локално |

Тествайте и точно максималния разрешен ECHO payload. Timeout tests използват интервал с толеранс, не изискват точна милисекунда.

## 9. Наблюдение и измерване — 15 минути

В Wireshark изберете loopback интерфейса и display filter `tcp.port == 9000`. Направете нова connection след началото на capture. Намерете SYN/SYN-ACK/ACK, application bytes и FIN/ACK обмен при `QUIT`. При рязък отказ е възможен RST, но не го очаквайте при всяко `close()`.

Използвайте Follow TCP Stream и съпоставете първите 14 application bytes с PING vector. TCP segment, Wireshark reassembly и Java `read()` са различни наблюдения. Loopback offloading може да промени видимите размери; не извеждайте броя reads от packet count.

Предайте таблица за поне три buffer размера: write calls, read calls, bytes, decoded messages, exceptions. Добавете screenshot или описание с packet numbers за handshake и close; запишете 100/100 успешни round trips или конкретните неуспехи.

## 10. Въпроси за анализ

1. Защо frame може да премине през няколко TCP segments и няколко reads?
2. Къде точно различавате clean EOF от truncated frame?
3. Защо `flush()` не решава framing?
4. Какъв риск има allocation преди проверка на LENGTH?
5. Какво доказва успешен ECHO response, което успешен write не доказва?
6. Защо retry на целия frame след write exception е опасен?
7. Какво остава незащитено при read inactivity timeout?

## 11. Самостоятелно надграждане

Добавете `RecordingInputStream`, който измерва calls и bytes без да променя decoder-а. Изследвайте ефекта на buffering с еднакви payloads и обяснете защо промяната на counts не трябва да променя messages.

## 12. Очакван резултат

Работещ baseline TCP клиент и сървър със самостоятелен codec, 100 проверени ECHO exchanges, explicit close policy и възпроизводим набор от raw-byte tests. Предайте `results/lab01/observations.md`, test commands и capture/packet references.

## 13. Критерии за приемане

- [ ] Header и ID съответстват на byte vector и big-endian договора.
- [ ] Decoder обработва 1-byte chunks и два слепени frames.
- [ ] LENGTH се проверява преди allocation, включително lower bound 8.
- [ ] Clean EOF, truncation и timeout се различават в резултатите.
- [ ] PING/ECHO/QUIT работят и socket се освобождава при error.
- [ ] Има поне четири failure tests и измерена таблица.
- [ ] Handshake, data и termination са документирани.

## 14. Допълнителни задачи

1. Добавете fuzz test с bounded random byte arrays и лимит на времето за decode.
2. Изследвайте `TCP_NODELAY` при малки заявки, без да правите универсални изводи от loopback.
3. Сравнете length-prefix framing с delimiter framing за binary payload и аргументирайте trade-offs.
