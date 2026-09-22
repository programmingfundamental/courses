---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 6. Начален експеримент — 15 минути

Направете non-blocking connection и изпратете само първите 2 bytes от header. При readable event опитайте временния грешен подход „прочети веднъж и parse-ни целия header“. Документирайте missing bytes/underflow, после отстранете този код.

Втори кратък опит: регистрирайте OP_WRITE постоянно, без output. За 5 s пребройте select returns и празни write-ready handlers. После премахнете OP_WRITE и сравнете CPU/loop counters. Този умишлен дефект се използва само в контролирания експеримент.

## 7. Основна лабораторна задача — 50 минути

### Стъпка 1 — accept и ConnectionState

Създайте ServerSocketChannel, `configureBlocking(false)`, bind и registration с OP_ACCEPT. Всеки accepted SocketChannel също преминава в non-blocking mode преди registration. При cap=64 connection се затваря веднага; броят се конфигурира за по-късните тестове.

```java
final class ConnectionState {
    final ByteBuffer header = ByteBuffer.allocate(6);
    ByteBuffer body; // null, докато header не е валидиран
    final Deque<ByteBuffer> outbound = new ArrayDeque<>();
    long queuedBytes;
    long lastProgressNanos;
    boolean inputEnded;
    boolean closingAfterFlush;
}
```

`ArrayDeque` е безопасен тук само защото **един event-loop thread** е owner. Логическият byte cap е 256 KiB на connection, а frame count cap=64; enqueue проверява и двата. Самият container не налага тези ограничения автоматично.

### Стъпка 2 — stateful decoder

```java
interface IncrementalDecoder {
    // Консумира наличния prefix и връща най-много един завършен message.
    Optional<Message> feed(ByteBuffer source) throws ProtocolException;
    boolean hasPartialFrame();
}
```

Caller повтаря feed само докато има bytes и budget. Test-нете feed с всеки split point на golden vector, включително split вътре в LENGTH и ID. Ако source съдържа frame A и половината от B, A се dispatch-ва, B остава в decoder state. Споделете validation функции с blocking codec; не копирайте разминаващи се constants.

### Стъпка 3 — четене и fairness

На един key обработвайте до 64 KiB или 32 messages за tick. Read=0 връща управлението към event loop. EOF с partial frame е error; EOF между frames позволява drain на вече queued responses, след което close. Application commands се изпълняват само когато са кратки и ограничени; няма sleep, файлов I/O или blocking network writes.

Ако използвате accumulator и остават **вече прочетени** complete frames след frame budget, добавете connection в bounded local ready queue. Не чакайте нов OP_READ за bytes, които вече са в user buffer. При двубуферния вариант ограничете самите reads така, че да не натрупвате такъв скрит backlog.

### Стъпка 4 — partial writes

Encoder връща отделен buffer в read mode; queue пази неговата текуща position. При OP_WRITE записвайте head, намалявайте queuedBytes с реално върнатия count и remove-вайте само buffer без remaining bytes. При write=0 приключете handler-а. Когато queue се изпразни, изключете OP_WRITE. Не извиквайте `rewind()` след partial write — това дублира bytes.

За deterministic test дефинирайте transport adapter:

```java
interface OutboundWriter {
    int write(ByteBuffer source) throws IOException;
}
```

Fake writer приема най-много 3 bytes и периодично връща 0, като придвижва position само за приетите bytes. Събраният output трябва да е byte-for-byte идентичен с encoder output.

### Стъпка 5 — cleanup и event loop

В selected-key iteration премахнете key от set преди обработка, проверявайте validity след операции, които могат да close-нат connection. IOException затваря само засегнатия connection. Close cancel-ва key, затваря channel, освобождава buffers и намалява active counter точно веднъж.

Проверявайте idle deadline на всеки select cycle с максимално select wait 250 ms; задайте idle timeout=10 s за лабораторните опити. QUIT маркира closingAfterFlush; след OK drain изпълнете bounded close. При server shutdown спрете accept, забранете нови requests, drain-вайте output до 3 s, после затворете channels и selector.

## 8. Failure scenarios и edge cases — 20 минути

| Случай | Стимул | Очакване |
|---|---|---|
| Header split на всяка граница | feed golden vector по части | едно и също Message |
| Два frames + partial трети | concatenated input | два dispatch-а и запазен suffix |
| Partial write/zero write | fake writer с max=3 | няма повторени/пропуснати bytes |
| EOF в body | client half-close | protocol error, key cleanup |
| Slow reader | client спира reads, bounded ECHO burst | output cap, close по policy |
| Invalid length | raw oversized header | reject преди body allocation |
| Busy writer | постоянен поток от един client | другите получават обслужване |
| Disconnect при readiness | close преди handler | server loop остава жив |

В тази лаборатория overflow policy е **close на slow connection** с metric; по-фина read-pause стратегия идва в Lab 5.

## 9. Наблюдение и измерване — 15 минути

Сравнете thread-per-client от Lab 2 и NIO при 10 и 100 connections, ако limits и средата позволяват. Запишете еднакви payloads, timeout, TCP_NODELAY и connection cap. Измерете threads, CPU при idle, completed requests, errors, latency на тих client до активен sender, maximum queuedBytes.

Не сравнявайте 32-worker pool с 100 NIO connections като доказателство за „по-бърз API“, без да отчетете waiting sessions. Отделете архитектурния limit от parser cost. Задължителният резултат е таблица и поне един обяснен trade-off, не числов победител.

## 10. Въпроси за анализ

1. Защо readable event не означава complete frame?
2. Какво се губи при clear вместо compact в accumulator?
3. Защо OP_WRITE трябва да се изключва при празна queue?
4. Кой притежава buffer след enqueue?
5. Защо zero write не означава disconnect?
6. Как се обработват bytes, които вече са прочетени, но чакат fairness budget?
7. Как CPU-heavy dispatcher влияе на всички connections?

## 11. Самостоятелно надграждане

Добавете explicit per-tick fairness budget и local ready queue към accumulator вариант. Сравнете latency на тих клиент при един непрекъснат sender преди/след промяната; докажете, че residual frames не остават завинаги необработени.

## 12. Очакван резултат

NioServer работи със същия TcpClient и ECHO vectors, без thread на connection. Предайте decoder/partial-write tests, state diagram и сравнение в `results/lab04/`.

## 13. Критерии за приемане

- [ ] Всички channels са non-blocking преди registration.
- [ ] Decoder пази partial state и използва shared protocol bounds.
- [ ] Zero/partial writes запазват exact bytes и position.
- [ ] OP_WRITE е включен само при pending output.
- [ ] Event loop не изпълнява blocking application работа.
- [ ] Queues, active channels, fairness и deadlines са ограничени.
- [ ] EOF/disconnect/shutdown освобождават key и channel.
- [ ] Има сравнение с blocking variant и поне четири failure tests.

## 14. Допълнителни задачи

1. Реализирайте NIO chat commands с asynchronous delivery receipts, запазващи SEND semantics от Lab 2.
2. Сравнете direct и heap ByteBuffers при еднакъв workload и измерете allocation pressure.
3. Изследвайте gathering writes за header/body с правилно запазване на partial progress.
