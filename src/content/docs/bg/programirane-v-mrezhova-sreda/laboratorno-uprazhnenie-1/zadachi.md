---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 6. Начален експеримент

1. Направете временен sender с две byte arrays с различим текст и два последователни `out.write(message1); out.write(message2);`. Receiver печата само `read count` и hex bytes, без да ги интерпретира като съобщения.
2. Повторете с receiver buffer 3, 8 и 1024 bytes. При buffer 3 дълго съобщение задължително изисква множество reads.
3. Повторете с един `BufferedOutputStream`: запишете двете arrays и направете един `flush()`. Receiver започва след кратка контролирана пауза. Сливане може да се наблюдава, но не е гарантирано от OS; липсата му не опровергава byte-stream модела.
4. За детерминиран test съединете два encoded frames в `ByteArrayInputStream`, а после ограничете всяко четене до 1 byte чрез wrapper. И в двата случая очаквайте точно две decoded messages, независимо от броя reads.

Запишете `write sizes`, `read sizes`, общи bytes и брой възстановени messages. Не използвайте `sleep()` като „решение“ на framing.

## 7. Основна лабораторна задача

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

## 8. Failure scenarios и edge cases

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

## 9. Наблюдение и измерване

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
