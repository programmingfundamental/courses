# Lab 4 — Бележки за преподавателя

Само за преподавателя; не се включва в студентския сайт.

## 1. Цел на упражнението

Да се превърне implicit blocking call stack в explicit state machine. Основните идеи са readiness, progress, ownership и fairness; списъкът от NIO classes не е цел сам по себе си.

## 2. Очаквано предварително ниво

Коректен TCP codec и достатъчно увереност в buffers и lifecycle. Предоставете празен event-loop skeleton и fake-writer interface. Не пренасяйте целия chat server: задължителният обем е PING/ECHO/QUIT.

## 3. Препоръчително разпределение на времето

| Дейност | Минути |
|---|---:|
| Проблем и архитектурни хипотези | 10 |
| Readiness, buffers, decoder state | 15 |
| Partial header и busy-loop експеримент | 15 |
| NIO реализация | 50 |
| Failure tests | 20 |
| Сравнителни измервания | 15 |
| Анализ | 10 |
| **Общо** | **135** |

На 40-тата минута проверете, че всеки студент може да нарисува HEADER/BODY състоянията. На 90-тата трябва да има ECHO и fake-writer passing test. Optional chat остава извън занятието.

## 4. Как да бъде въведен проблемът

Покажете thread dump на много idle blocking sessions и попитайте кои threads вършат работа. След предложението за selector изпратете само два header bytes: къде ще останат, докато пристигнат останалите?

## 5. Основни точки за обяснение

Readiness не е completion; нулев progress е нормален; ByteBuffer position е част от неприключилата операция; decoder state е per connection; selected keys се консумират; idle sockets са често writable; fairness трябва да покрива и user-space buffers; event loop не поема slow work.

## 6. Чести грешки на студентите

Accepted channel остава blocking; `readFully` се пренася в event loop; clear изтрива unread suffix; flip се извиква два пъти; partial write buffer се rewind-ва; OP_WRITE остава винаги включен; key не се премахва; exception от един client прекратява целия server; deferred parsed frames чакат readiness, което няма да дойде; output bytes се броят по capacity вместо remaining.

## 7. Насочващи въпроси

Колко bytes липсват точно сега? Кой пази position между select cycles? Как ще докажете partial-write correctness без да молите OS за конкретно поведение? Какво ви събужда, ако remaining frame вече е в user-space buffer? Какво ще се случи с останалите clients при 50 ms sleep в handler?

## 8. Очаквана архитектура на решението

Един owner управлява selector, keys, decoder state, output buffers и close. Dispatcher за задължителните команди е кратка чиста логика. Shared protocol validation е независима от transport. Следващото занятие добавя workers чрез message passing, а не чрез произволен достъп до ConnectionState.

```text
accept/read/write readiness
           |
       event loop -> ConnectionState -> bounded buffers
           |
     bounded immediate dispatcher
```

## 9. Ключови части от примерно решение

**Показвайте едва след fake-writer теста.**

```java
ByteBuffer head = state.outbound.peekFirst();
int written = channel.write(head);
state.queuedBytes -= written;
if (!head.hasRemaining()) state.outbound.removeFirst();
// Caller спира при written == 0 или изчерпан fairness budget.
if (state.outbound.isEmpty()) {
    key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
}
```

Snippet-ът предполага valid key, непразна queue и ownership от event loop. Тези preconditions трябва да се обяснят; не е цял write handler. За EOF проверката първо разграничете partial decoder state от complete pending responses. Ако има полузатворен input и пълен response, той може да се drain-не преди close.

## 10. Как да се демонстрират edge cases

Параметризирайте parser test с всяка split boundary в 14-byte PING vector. За fake write използвайте counts 3,0,2,0,3 до края и сравнете exact output. За real slow reader ограничете burst, намалете send buffer и наблюдавайте queuedBytes; deterministic test остава основното доказателство. За fairness подайте concatenated frames, повече от per-tick budget, и спрете изпращането: ако последните не се dispatch-нат, е намерен user-buffer scheduling bug. За busy loop сравнете пет секунди с/без празен OP_WRITE.

## 11. Очаквани резултати

При idle clients NIO използва приблизително постоянен брой application I/O threads. Това не гарантира по-ниска latency при всеки workload. Buffer/state bugs се проявяват детерминирано в unit harness. Slow peer увеличава output backlog; cap прекратява този connection, без да събаря останалите.

## 12. Критерии за оценяване

| Област | Точки | Доказателство |
|---|---:|---|
| Correctness | 30 | stateful decode и exact partial writes |
| Protocol/network understanding | 15 | readiness vs completion |
| Robustness | 20 | EOF, caps, fairness, cleanup |
| Code quality | 10 | single ownership, shared validation |
| Experimental work | 15 | deterministic tests и comparison |
| Analysis | 10 | trade-offs и ограничения |
| **Общо** | **100** | |

Не награждавайте NIO само за по-малко threads. Busy loop с passing ECHO губи robustness точки.

## 13. Въпроси за устна проверка

1. Какво означава OP_READ? — Възможен read progress/EOF, не задължително full message.
2. Защо write=0 не е error? — Non-blocking каналът временно не приема bytes.
3. Какво пази compact? — Unread suffix, който се премества за следващо допълване.
4. Защо не rewind след partial write? — Повторно изпраща вече приет prefix.
5. Защо local ready queue може да е нужна? — Kernel readiness не представя already-buffered work.
6. Кога blocking модел може да е разумен? — Ограничени connections, по-прост lifecycle и workload с различни waits; отговорът трябва да е conditional.

## 14. Как упражнението се свързва със следващото

Lab 5 поставя бавна WORK команда зад bounded worker queue. Същият event loop запазва ownership на channels и използва completion handoff. Запазете working pure NIO ECHO baseline за Lab 7.
