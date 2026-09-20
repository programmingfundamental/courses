# Lab 2 — Бележки за преподавателя

Само за преподавателя; не се включва в студентския сайт.

## 1. Цел на упражнението

Concurrency променя ownership и resource accounting, а не само броя threads. Търсете аргументи за atomicity, output serialization и lifecycle. API синтаксисът на executor е второстепенен.

## 2. Очаквано предварително ниво

Работещ codec от Lab 1, Thread/Runnable, synchronized и concurrent collections. Подгответе CLI driver, който задържа connections и изпраща команди. Не предоставяйте готов SessionRegistry.

## 3. Препоръчително разпределение на времето

| Дейност | Минути |
|---|---:|
| Сценарий | 10 |
| Ownership, protocol extensions, pool | 15 |
| Idle-client експеримент | 15 |
| Основна реализация | 50 |
| Race и lifecycle failures | 20 |
| Измервания | 15 |
| Анализ | 10 |
| **Общо** | **135** |

На 40-тата минута трябва да има работещ thread-per-client ECHO. На 90-тата — LOGIN и доставки. Ако command parsing забавя групата, дайте payload test vectors; atomic registry и cleanup остават работа на студентите.

## 4. Как да бъде въведен проблемът

Оставете първия baseline клиент idle и попитайте защо вторият има successful connect, но няма response. След threading „поправката“ стартирайте duplicate LOGIN тест, за да прехвърлите разговора от throughput към correctness.

## 5. Основни точки за обяснение

Acceptor не изпълнява handlers; fixed pool не означава bounded pending queue; permits покриват accepted sockets, не само logged users; session input има един owner; per-session output lock покрива целия frame; registry locks никога не обхващат network writes; graceful shutdown е протокол с deadline.

## 6. Чести грешки на студентите

Thread-safe container се бърка с atomic compound operation; `remove(name)` изтрива replacement; response и EVENT се четат от две нишки; write lock се взема поотделно за header/body; rejected task оставя open socket; queued clients не влизат в tracked set; watchdog чака същия lock като blocked writer; `LongAdder.sum()` се използва за admission correctness.

## 7. Насочващи въпроси

Кой притежава socket между accept и submit? Какво става ако submit хвърли exception? Кое точно трябва да е атомарно? Може ли close да се изпълни два пъти? Кой затваря socket, ако worker никога не го започне? Какво доказва SEND OK?

## 8. Очаквана архитектура на решението

```text
Acceptor -> Admission permits -> bounded SessionExecutor
              |                         |
         tracked sockets          ClientSession
              ^                   /     |      
           Watchdog        Decoder  Dispatcher  serialized writer
                                      |
                                SessionRegistry
```

Състояния: ACCEPTED → QUEUED/RUNNING → REGISTERED → CLOSING → CLOSED. Thread-per-client прескача QUEUED, но пази същите limits. Listener и watchdog са собственост на server; handler owns socket след handoff; tracked set позволява forced close при shutdown.

## 9. Ключови части от примерно решение

**Не показвайте преди възпроизвеждането на race.**

```java
boolean won = users.putIfAbsent(name, session) == null;
// При cleanup: премахва само същата session.
users.remove(name, session);
```

Atomic map операцията не приключва цялата state machine: при failed login session не трябва да запазва claimed name. Logout/disconnect cleanup трябва да бъде idempotent. Резултатът от registration се изпраща след установяване на локалното session state.

Pseudocode за handoff: acquire permit → add tracked socket → try execute → on rejection remove/close/release. В task finally се извиква същият idempotent close. Shutdown затваря listener, waits deadline, затваря tracked sockets и обработва tasks, върнати от shutdownNow.

## 10. Как да се демонстрират edge cases

Поставете barrier непосредствено след отрицателния containsKey, а не преди целия login: така race е детерминиран. Два senders изпращат различими 4096-byte текстове към един reader; проверявайте decoder correctness, не console appearance. За pool exhaustion задайте workers=2, queue=2, cap=4 и пуснете петия клиент. За slow write използвайте ограничен burst и recipient, който не чете; малък send buffer подпомага демонстрацията, но exact threshold е OS-dependent. Watchdog timeout трябва да приключи опита. За shutdown задайте кратък drain и пребройте running/queued cleanup.

## 11. Очаквани резултати

Thread-per-client обслужва независими idle connections с повече threads. Pool показва waiting sessions и latency дори при малък CPU load. След atomic registration има точно един winner. Per-session write serialization запазва frames, но slow writes все още блокират изпращачи; това мотивира Lab 4–5. Няма предварително изискване кой режим да е по-бърз.

## 12. Критерии за оценяване

| Област | Точки | Доказателство |
|---|---:|---|
| Correctness | 25 | commands, event routing, regression tests |
| Protocol/network understanding | 15 | доставка, connection vs request |
| Robustness | 25 | race fix, caps, shutdown, slow peers |
| Code quality | 10 | ownership и локализирана synchronization |
| Experimental work | 15 | concurrency таблица и thread evidence |
| Analysis | 10 | pool/resource trade-offs |
| **Общо** | **100** | |

Не оценявайте максималния брой clients сам по себе си. Partial credit за command coverage не отменя загубени точки при interleaved frames.

## 13. Въпроси за устна проверка

1. Защо containsKey/put е race? — Между двете операции друг caller може да промени map; трябва atomic conditional insertion.
2. Защо pool може да не натовари CPU, но да отказва заявки? — Workers чакат network I/O върху persistent connections.
3. Защо няма global write lock? — Независимите recipients не трябва да се сериализират; lock scope е session.
4. Какво е admission leak? — Socket/permit остава зает по rejection/exception branch.
5. Какво означава BROADCAST delivered? — Локално успешен write, без end-user acknowledgement и без атомарност.
6. Защо interrupt не е достатъчен shutdown plan? — Resources имат explicit owners; затварят се sockets и waiting tasks, следва bounded await.

## 14. Как упражнението се свързва със следващото

Lab 3 отделя transport гаранциите от application semantics чрез UDP; запазва validation и измервателните навици. TCP варианти и SessionRegistry остават за Lab 4/6/7. Изисквайте tag `lab02` и описание на limits, за да могат по-късно архитектурите да се сравняват при еднакви условия.
