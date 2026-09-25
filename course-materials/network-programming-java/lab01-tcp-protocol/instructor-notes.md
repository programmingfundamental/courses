# Lab 1 — Бележки за преподавателя

Само за преподавателя; не се включва в студентския сайт.

## 1. Цел на упражнението

Концептуалната цел е отделяне на transport delivery от application message. Важни са invariants и състоянието при неуспех. Синтаксисът на `Socket` и предпочитанието към Data streams или ByteBuffer са второстепенни.

## 2. Очаквано предварително ниво

Студентите пишат Java без помощ за loops/classes, познават exceptions, resource ownership, bytes и TCP endpoint. Преди часа проверете JDK, capture достъп и наличието на празни codec interfaces. Skeleton трябва да спестява setup, а не да съдържа готов decoder.

## 3. Препоръчително разпределение на времето

| Дейност | Минути |
|---|---:|
| Сценарий и хипотеза | 10 |
| Byte stream, framing и спецификация | 15 |
| Начален експеримент | 15 |
| Реализация на codec и server/client | 50 |
| Failure tests | 20 |
| Capture и таблица | 15 |
| Анализ и приемане | 10 |
| **Общо** | **135** |

На 40-тата минута трябва да има наблюдение, а на 90-тата — working ECHO. Ако групата изостава, използвайте готов CLI/parser и тестов sender, но оставете decoder-а за реализация. Optional задачите са извън часа.

## 4. Как да бъде въведен проблемът

Покажете sender с два writes и поискайте предсказание за броя reads. Нека студентите запишат хипотеза, преди да изпълнят експеримента с buffer 3. Не обещавайте, че OS винаги ще слее двата writes.

## 5. Основни точки за обяснение

- Bytes се подреждат от TCP; application boundaries ги определя codec-ът.
- LENGTH включва 8-byte ID и има lower/upper bound.
- Само EOF преди първия byte е clean end of stream.
- Blocking stream writes не връщат short count; exception оставя неопределен частичен progress.
- Read timeout, frame deadline и write deadline са различни политики.
- Stream flush, TCP acknowledgement и application response са различни събития.

## 6. Чести грешки на студентите

`read(buffer)` се приема за „прочети целия frame“; `available()` се използва за framing; UTF-8 character count се записва вместо byte count; `readUTF()` добавя чужд формат; `0xFF` се сравнява с Java signed byte без преобразуване; allocation става преди validation; всяка EOF се приема за нормално затваряне; streams се затварят отделно преждевременно и затварят целия socket.

## 7. Насочващи въпроси

Коя информация липсва след първите три bytes? Какво означава -1 точно в това състояние? Кой определя размера на следващата allocation? Може ли успешният write да ви каже дали dispatcher-ът е изпълнил командата? Как бихте доказали parser correctness без реална мрежа?

## 8. Очаквана архитектура на решението

```text
TcpClient -> Encoder -> socket -> Decoder -> Dispatcher
                                           |
TcpClient <- Decoder <- socket <- Encoder <- Message
```

Codec няма зависимост от Socket. Handler owns accepted socket; dispatcher приема Message и връща Message. Blocking baseline е последователен и не се „оправя“ предварително с threads. Всеки test може да подаде in-memory stream.

## 9. Ключови части от примерно решение

**Не показвайте веднага.** Разкрийте този pseudocode след като групата е посочила проблема с clean EOF:

```text
first = input.read()
if first == -1: return empty
readFully(remaining five header bytes)   // EOF тук е грешка
parse version, type, length
require version == 1 and 8 <= length <= 65536
allocate bounded payload
readFully(payload)
parse positive requestId; decode body
return message
```

Конкретният guard може да бъде `if (length < Long.BYTES || length > MAX_PAYLOAD) throw new ProtocolException("BAD_LENGTH");`. За encoder проверете `body.length > MAX_PAYLOAD - Long.BYTES`, вместо първо да събирате потенциално големи integers. Показвайте пълния guard едва след първия negative-length test.

## 10. Как да се демонстрират edge cases

- За deterministic fragmentation дайте wrapper, който връща най-много 1 byte от delegate read. Не разчитайте само на network sleeps.
- За truncation изпратете първите 3 bytes от vector и `shutdownOutput()`; после повторете с пълен header и 7 от 8 payload bytes.
- За coalescing подайте concatenated byte arrays в един in-memory stream и извикайте decode два пъти.
- За timeout изпратете header, изчакайте над configured read timeout; затворете test socket в finally.
- За oversized input изпратете само header с length 65537. Не изпращайте огромен реален payload.
- Capture започва преди connection; показвайте отделно Follow TCP Stream и packet list.

## 11. Очаквани резултати

Броят reads се променя, броят messages — не. При malformed/truncated input няма dispatch. При clean QUIT се вижда отговор и orderly termination; сегментацията и packet counts зависят от OS. Възможно е два writes да се видят като два reads в конкретен run — това не е transport guarantee.

## 12. Критерии за оценяване

| Област | Точки | Доказателство |
|---|---:|---|
| Correctness | 30 | codec vectors и PING/ECHO/QUIT |
| Protocol/network understanding | 15 | byte stream, EOF и flush обяснения |
| Robustness | 20 | bounds, truncation, timeout, cleanup |
| Code quality | 10 | отделен codec, ownership, constants |
| Experimental work | 15 | fragmentation таблица и capture |
| Analysis | 10 | изводи и ограничения |
| **Общо** | **100** | |

Невъзпроизведен конкретен TCP segment layout не намалява оценката, ако детерминираните tests и анализът са коректни. Unsafe allocation не получава точки в съответната robustness част.

## 13. Въпроси за устна проверка

1. Какво гарантира TCP? — Подредени bytes с transport reliability, без message boundaries и business acknowledgement.
2. Защо ID е в payload? — Общият header остава фиксиран; length включва metadata; response correlation е application задача.
3. Кога EOF е нормален? — Преди нов header, не в започнат frame.
4. Защо не retry-ваме write след exception? — Може вече да има prefix в потока; повторение поврежда framing или дублира операция.
5. Какво ограничава SO_TIMEOUT? — Чакането при read, не общото време на frame и не write.
6. Как ще тествате без Wireshark? — In-memory fragmentation/coalescing и raw socket integration tests; capture е допълнително наблюдение.

## 14. Как упражнението се свързва със следващото

Lab 2 запазва codec и dispatcher contract и променя lifecycle/ownership на sessions. Преди следващия час изисквайте tag `lab01` и passing framing tests: concurrency не трябва да прикрива parser bugs.
