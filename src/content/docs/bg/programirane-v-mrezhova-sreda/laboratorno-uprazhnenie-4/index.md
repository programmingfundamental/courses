---
title: "Лабораторно упражнение 4 — Java NIO и event-driven networking"
sidebar:
  order: 4
  label: Упражнение 4
---

# Лабораторно упражнение 4 — Java NIO и event-driven networking

## 1. Контекст и инженерен проблем

LabNet има много свързани устройства, които изпращат рядко. Blocking pool пази worker за всяка активна session, дори когато няма данни. Искаме една нишка да следи много connections, но това означава да пазим незавършените операции като explicit state.

**Какво използваме от предходното упражнение:** fault-oriented tests и работа с state; реалният TCP код идва от Lab 1–2. **Какво ще се използва по-късно:** event loop, incremental decoder, output queues и metrics се разширяват с workers и backpressure в Lab 5.

## 2. Учебни цели

- Реализира non-blocking accept/read/write с Selector.
- Проектира per-connection decoder и lifecycle state.
- Обработва partial reads, partial writes и zero progress.
- Управлява ByteBuffer position/limit и ownership.
- Диагностицира busy loop и неправилно OP_WRITE interest.
- Измерва fairness и resource usage при много idle clients.
- Сравнява event loop и thread-per-connection без универсален победител.

## 3. Необходими предварителни знания

Passing codec tests от Lab 1, concurrent server от Lab 2, ясно разбиране на blocking calls. TCP wire format не се променя.

## 4. Необходими инструменти

JDK, IDE, terminal, Git, `jcmd` за threads и CPU/process наблюдение. Използвайте същия TcpClient срещу NIO port 9002.

## 5. Теоретична подготовка

Selector уведомява за readiness, а не за приключена application операция. `OP_READ` не означава „има цял frame“. `SocketChannel.read()` може да върне положителен count, 0 или -1; `write()` може да запише само част или 0. Не правете blocking retry loop при zero progress. Вижте [SocketChannel](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/channels/SocketChannel.html).

```text
Client A --+
Client B --+--> Selector --> Event Loop --> ConnectionState
Client C --+                       |         header/body decoder
                                  +-------> bounded outbound queue
```

| Interest | Действие | Причина да приключим обработката |
|---|---|---|
| OP_ACCEPT | приемане и non-blocking registration | accept() връща null или accept budget е изчерпан |
| OP_READ | натрупване и decode | read=0, EOF, byte/frame budget |
| OP_WRITE | изпразване на queued ByteBuffers | write=0, queue empty или byte budget |

Регистрирайте OP_WRITE само когато има pending bytes; иначе много sockets са постоянно writable и select loop може да консумира CPU без полезна работа. Обработеният key се премахва от selected-key set; readiness bits могат да са няколко едновременно. `Selector.wakeup()` събужда select при външна работа, но не е queue за данни. Вижте [Selector](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/channels/Selector.html).

`ByteBuffer` има position, limit и capacity. След read в write mode `flip()` подготвя за parsing; `compact()` запазва unread suffix при accumulator стратегия, докато `clear()` го изхвърля логически. При двубуферен decoder header и body могат да се пълнят директно и да се flip-ват при завършване. Вижте [ByteBuffer](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/ByteBuffer.html).

### Договор на incremental decoder

```text
HEADER: 6-byte buffer -> validate version/length
                           |
                           v
BODY: bounded length buffer -> validate ID -> emit Message
                           |
                           +-----------------> HEADER
```

Всеки connection има собствен decoder. Частичен header/body се запазва между select cycles. Decoder не блокира и не чете InputStream. След version/length validation може да allocate-не до 65536 bytes за body. Header buffer е фиксиран; няма неограничено разширяване. Message трябва да притежава данните си, ако buffer-ът ще се използва повторно.

В задължителната NIO версия реализирайте PING/ECHO/QUIT. Chat commands от Lab 2 могат да връщат `ERROR/NOT_SUPPORTED`; така се пренася transport ядро, без blocking ClientSession writers да попадат в event loop. Lab 7 сравнява общата ECHO функционалност.
