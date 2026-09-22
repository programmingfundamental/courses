---
title: "Лабораторно упражнение 2 — Concurrent TCP Server"
sidebar:
  order: 2
  label: Упражнение 2
---

# Лабораторно упражнение 2 — Concurrent TCP Server

## 1. Контекст и инженерен проблем

Първият клиент на LabNet остава свързан, без да изпраща команда. Втори клиент успява да установи TCP connection, но не получава application response. След въвеждане на threads проблемът се променя: двама потребители могат да получат едно име, а едновременни отговори повреждат frame stream.

Трябва да обслужим много connections, без да загубим protocol correctness и без неограничено потребление на ресурси.

**Какво използваме от предходното упражнение:** Message, codec, dispatcher skeleton, PING/ECHO/QUIT и raw-frame tests. **Какво ще се използва по-късно:** session lifecycle, командите, atomic registration и thread-pool вариантът са основа за NIO, TLS и сравнителните измервания.

## 2. Учебни цели

- Реализира thread-per-client и ограничен thread-pool вариант.
- Определя ownership на session, input stream и output stream.
- Открива и поправя check-then-act race condition.
- Реализира LOGIN/SEND/BROADCAST/USERS/STATS с точна семантика.
- Измерва поведение при много connections и изчерпан pool.
- Изпълнява graceful shutdown с bounded drain и cleanup.
- Аргументира limits и trade-offs при slow clients.

## 3. Необходими предварителни знания

Работещ Lab 1, Thread/Runnable, synchronized, happens-before, atomic операции, exceptions и executor lifecycle.

## 4. Необходими инструменти

JDK, IDE, Git и няколко terminal процеса. `jcmd <pid> Thread.print` служи за thread dump. Клиентският test driver създава най-много 100 connections в началните експерименти.

## 5. Теоретична подготовка

TCP connection може да стои в OS accept backlog, без application handler да е започнал. При thread-per-client acceptor стартира `new Thread(new ClientSession(socket)).start()`. Това изолира blocking reads, но броят platform threads нараства с connections. Pool ограничава threads, но дълго живеещи sessions могат да заемат всички workers.

```text
acceptor -> admission limit -> client-session executor
                               |    |    |
                            session A/B/C -> shared registry
```

`ExecutorService` е lifecycle abstraction; конкретната queue policy е отделен избор. `Executors.newFixedThreadPool(n)` използва неограничена work queue. Тук създаваме fixed-size `ThreadPoolExecutor` с bounded queue и explicit rejection, за да не складираме произволен брой accepted sockets. Вижте [ThreadPoolExecutor](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html).

```java
ExecutorService sessions = new ThreadPoolExecutor(
    32, 32, 0L, TimeUnit.MILLISECONDS,
    new ArrayBlockingQueue<>(32),
    new ThreadPoolExecutor.AbortPolicy());
```

32 workers + 32 waiting sessions е **учебна конфигурация**, не универсален sizing. Admission cap=64 включва accepted running и queued sockets; sockets в backlog не са application active sessions. При rejection acceptor затваря socket и връща permit. Не използвайте CallerRunsPolicy: blocking handler в acceptor спира новото приемане.

`ConcurrentHashMap` защитава отделни операции, но не прави `containsKey` + `put` атомарна последователност. Също така thread-safe map не прави mutable `ClientSession` thread-safe.

### Разширение на протокола

Header, ID и отговорите са от Lab 1. PING/ECHO/QUIT остават достъпни преди login за diagnostics. `LOGIN` е именуване, не authentication.

| Request | Body | Успех / error |
|---|---|---|
| LOGIN `0x10` | UTF-8 име, regex `[A-Za-z0-9_-]{1,24}` | OK / NAME_IN_USE, ALREADY_LOGGED_IN |
| SEND `0x11` | 1-byte name length + recipient name + UTF-8 text, text 1..4096 bytes | OK / NO_SUCH_USER, SLOW_RECIPIENT |
| BROADCAST `0x12` | UTF-8 text 1..4096 bytes | DATA с ASCII `delivered=N;failed=M` |
| USERS `0x13` | празно | DATA с имена, разделени с LF, sorted snapshot |
| STATS `0x14` | празно | DATA с `active`, `loggedIn`, `requests`, `errors`, `rejected` |

SEND/BROADCAST/USERS/STATS изискват login, иначе `ERROR/NOT_LOGGED_IN`. Невалиден UTF-8 се отхвърля с decoder, настроен на `CodingErrorAction.REPORT`. Response ID съвпада с request ID. `EVENT=0x82` има ID=0 и body: 1-byte sender-name length + sender name + text. Client има един reader, който маршрутизира EVENT отделно от отговорите.

SEND OK означава, че server е приключил локалния write към recipient, не че потребителят го е прочел. BROADCAST брои такива локално успешни writes; не е атомарна доставка до всички. Глобален ред между различни изпращачи не се обещава. При промени на registry USERS е ограничен snapshot, не транзакционен read на целия свят. При твърде много имена върнете `ERROR/RESULT_TOO_LARGE`, вместо да нарушавате MAX_PAYLOAD.
