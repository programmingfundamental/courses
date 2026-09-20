# Lab 3 — Бележки за преподавателя

Само за преподавателя; не се включва в студентския сайт.

## 1. Цел на упражнението

Да се разграничат доставка, потвърждение и ефект. Студентите трябва да видят защо retries създават duplicates и защо bounded deduplication не е exactly-once. Datagram API синтаксисът е второстепенен спрямо state machine.

## 2. Очаквано предварително ниво

Byte parsing, timestamps, maps и собствените tests от Lab 1–2. Предварително подгответе configurable CLI и примерен deterministic drop rule; оставете ACK/retry/cache за студентите.

## 3. Препоръчително разпределение на времето

| Дейност | Минути |
|---|---:|
| Сценарий с брояч | 10 |
| Datagram contract и state machine | 15 |
| Unreliable/retry експеримент | 15 |
| Codec, sender, receiver, injector | 50 |
| Failure scenarios | 20 |
| Измервания | 15 |
| Анализ | 10 |
| **Общо** | **135** |

На 40-тата минута групата трябва да е наблюдавала двойно изпълнение; на 90-тата — lost ACK recovery. При недостиг на време дайте injector skeleton с bounded priority queue, но не receiver state machine.

## 4. Как да бъде въведен проблемът

Изпратете „добави 1“, изпълнете го и скрийте ACK. Попитайте sender-а дали да retry-не. Покажете total=2 в наивната реализация. Питайте каква допълнителна информация трябва да пазят и двете страни.

## 5. Основни точки за обяснение

UDP има boundaries, но не reliable ordered stream. Retry identity се запазва между изпращанията. Receiver трябва да пази резултата, а не само факта на получаване. Cache lifetime е част от договора. Deadline не се рестартира при всяко чуждо съобщение. Backoff ограничава повтарящия се товар, но не представлява пълен congestion control.

## 6. Чести грешки на студентите

Нов SEQ при retry; dedup само по SEQ без session/endpoint; ACK преди apply/cache; повторно изчисляване на резултата при duplicate; reset на timeout при чужд ACK; buffer reuse без setLength reset; случайно загубени queued bytes поради mutable array; безкраен retry; cache eviction на active session; обещание за exactly-once след restart.

## 7. Насочващи въпроси

Може ли client да различи lost DATA от lost ACK? Коя операция е идемпотентна? Как ще отговорите на повторен SEQ, ако total вече е променен от следваща операция? Какво знаете след UNKNOWN_OUTCOME? Кой ограничава паметта на fault injector-а?

## 8. Очаквана архитектура на решението

```text
Sender state -> client fault queue -> UDP -> receiver state/cache
     ^                                         |
     +--------- UDP <- server fault queue <- cached ACK
```

По един owner за sender timers и receiver cache. Всяка queue има explicit cap. Cache key включва source endpoint и session. Stop-and-wait допуска един outstanding request; подредбата на по-нови DATA не изисква reorder buffer.

## 9. Ключови части от примерно решение

**Не показвайте преди lost ACK дискусията.**

```text
if unknown session:
    accept only seq=1 and available cache slot
if seq == lastSeq:
    resend cachedAck
else if seq == lastSeq + 1:
    nextTotal = checkedAdd(total, delta)
    cachedAck = encode(session, seq, nextTotal)
    commit nextTotal, lastSeq, cachedAck
    send cachedAck
else if seq < lastSeq:
    count stale; ignore
else:
    send BAD_SEQUENCE
```

Преди всяка проверка по-горе трябва да има size/type/session validation. Събиране на long трябва да е checked. При cache expiry unknown SEQ>1 връща SESSION_EXPIRED; стар SEQ=1 след restart е неразличим от нова session, което е точно ограничението на гаранцията.

## 10. Как да се демонстрират edge cases

Първо използвайте deterministic rules: drop първото DATA(2), drop първото ACK(3), duplicate DATA(4), delay копие на DATA(4) до след ACK(5). После включете probabilities. За 100% loss ограничете опита до 10 s. За oversized packet изпратете 1201 bytes и проверете, че не се приема truncated prefix. За wrong ACK използвайте втори socket с друг source port. За crash ambiguity рестартирайте receiver след лог `APPLIED`, преди ACK send; това е controlled test hook, а не race с ръчно спиране на процеса.

## 11. Очаквани резултати

При беззагубен канал почти няма retries. Lost ACK увеличава traffic, но не броя effects. По-висок loss увеличава failure rate или времето, но кратки random runs не са строго монотонни. След timeout може да има applied, но unacknowledged последна операция. Не изисквайте final total да е равен само на acknowledged count без тази уговорка.

## 12. Критерии за оценяване

| Област | Точки | Доказателство |
|---|---:|---|
| Correctness | 25 | wire format, stop-and-wait, cached ACK |
| Protocol/network understanding | 20 | reliability, idempotency, ambiguity |
| Robustness | 20 | malformed packets, deadlines, bounded state |
| Code quality | 10 | ownership и отделен codec/injector |
| Experimental work | 15 | deterministic tests + seeds |
| Analysis | 10 | goodput/retry/guarantee trade-offs |
| **Общо** | **100** | |

Високият goodput не носи самостоятелни точки. Нечестно exactly-once твърдение губи точки за understanding/analysis дори happy path да работи.

## 13. Въпроси за устна проверка

1. Защо lost ACK води до duplicate? — Sender няма доказателство за изпълнение и повтаря същата logical operation.
2. Какво идентифицира операцията? — Endpoint/session/sequence; retry запазва identity.
3. Защо пазим отговора? — Повторението трябва да върне първоначалния резултат без нов effect.
4. Какво означава UNKNOWN_OUTCOME? — Възможни са и изпълнение, и липса на изпълнение; timeout не е negative acknowledgement.
5. Защо max datagram не е универсален MTU? — Path/encapsulation варират; размерът е лабораторна policy.
6. Кои TCP свойства липсват? — Пълен flow/congestion control, stream ordering за произволен прозорец, connection lifecycle и още failure handling.

## 14. Как упражнението се свързва със следващото

Lab 4 се връща към TCP codec и dispatcher, но използва подхода „байтовете/събитията могат да дойдат в неудобен ред и момент“. Fault injection и bounded state мисленето преминават към incremental parsing и partial writes. UDP implementation остава отделен adapter и не се включва като равностоен TCP performance baseline.
