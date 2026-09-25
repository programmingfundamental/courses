---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 6. Начален експеримент

Направете временен unreliable режим: 20 DATA съобщения със стойност +1, без ACK/retry. Fault injector drop-ва всеки пети пакет, а всеки седми дублира. Сравнете изпратени операции и total на server. После добавете „повтори, ако няма отговор“, но без deduplication, и drop-нете първия ACK. Докажете двойното изпълнение на една логическа операция.

Запишете timeline `send(seq) → execute(seq) → dropped ACK → resend(seq) → execute(seq)` и посочете коя стъпка трябва да се промени.

## 7. Основна лабораторна задача

### Стъпка 1 — отделен datagram codec

Създайте `UdpMessage`, `UdpCodec`, `ReliableUdpClient`, `UdpReceiver`, `FaultInjector`. Не четете payload „на части“ от няколко datagrams — всеки receive е самостоятелно съобщение.

```java
interface FaultInjector {
    void enqueue(byte[] immutablePacket, SocketAddress target, long nowNanos);
    void sendDue(DatagramSocket socket, long nowNanos) throws IOException;
}
record PendingRequest(long sessionId, long sequence,
                      byte[] encoded, int attempts, long deadlineNanos) {}
```

### Стъпка 2 — bounded fault injector

Поддържайте priority queue с due timestamps, капацитет 128, един owner и seeded `Random`. Настройки: loss probability, duplicate probability, maxDelayMs и seed. Клонирайте packet bytes при enqueue. При overflow drop-нете и отчетете `injectorOverflow`; не чакайте в receiver thread.

Injector работи на изхода **и на client, и на server**, за да губи DATA и ACK. Основният loop изпраща due packets, после receive с timeout до следващия timer (минимум 1 ms). Различни delays водят до reordering на копия. Добавете deterministic rules като `drop first ACK for seq=3` за regression tests; seed сам по себе си не фиксира OS scheduling.

### Стъпка 3 — stop-and-wait sender

Първоначален RTO=200 ms, след timeout умножете по 2 до cap 1600 ms. Максимум **5 изпращания общо**: първо + 4 retransmissions. Общ deadline=10 s включва чакане и injector delays. Няма безкраен retry; след изчерпване връщате `UNKNOWN_OUTCOME` и спирате session-а.

ACK е валиден само ако source endpoint, SESSION_ID, SEQ, TYPE и LENGTH съвпадат с очакваното. Чужд/стар/повреден datagram не рестартира deadline. Timeout е application policy: ACK може да се забави, без DATA да е изгубен.

### Стъпка 4 — receiver и deduplication

Реализирайте state machine от теорията. Променете total и cached result като една логическа операция в един receiver thread; изпратете ACK след това. Не използвайте само `Set<SEQ>`: sender трябва да получи същия резултат, ако ACK е бил изгубен.

### Стъпка 5 — controlled scenarios

Изпълнете по 100 последователни операции за loss=0%, 10%, 30% с фиксиран seed и documented delay. Изпълнете поне три seeds за вероятностните сценарии. Не приемайте, че всеки run с loss=30% ще завърши успешно; retry budget е краен. Потвърдете total за acknowledged prefix и отбележете ambiguity на последната timeout операция.

## 8. Failure scenarios и edge cases

| Случай | Как се причинява | Очакване |
|---|---|---|
| Lost DATA | drop първото DATA(seq=2) | retry, точно едно изпълнение |
| Lost ACK | drop първото ACK(seq=3) | повторен cached ACK, без втори delta |
| Duplicate DATA | duplicate seq=4 | total се изменя веднъж |
| Reordering | задръжте copy на seq=4, пуснете след seq=5 | stale copy не връща total назад |
| 100% loss | probability=1 | край до deadline/attempt limit |
| Wrong ACK | различен session/seq/source | игнориране, deadline не се удължава |
| Malformed/truncated datagram | length mismatch, 1201 bytes | reject без allocation по входната стойност |
| Restart/expiry | restart след apply, преди ACK | UNKNOWN_OUTCOME; няма exactly-once твърдение |

Stop-and-wait не допуска две нови DATA операции едновременно, затова reordering демонстрацията използва забавено копие на стара операция. Не представяйте този test като sliding-window протокол.

## 9. Наблюдение и измерване

Съберете: unique operations, data datagrams sent, retries, ACKs, duplicates, stale packets, timeouts, injector drops/overflow, final total, duration. Goodput е acknowledged application data bytes / elapsed seconds; wire throughput включва headers, ACKs и retransmissions и е различна величина.

| seed | loss | delay | acknowledged | retransmissions | duplicates | failures | goodput B/s |
|---|---|---|---|---|---|---|---|
| попълнете | | | | | | | |

Съпоставете client и server logs по session/seq. Не приспадайте server timestamp от client timestamp за latency; измервайте RTT на client. Rate limit-нете изпращането и пазете fault queue bounded.

## 10. Въпроси за анализ

1. Защо lost ACK е по-опасен от простата интуиция „пакетът не е пристигнал“?
2. Как се различават idempotent операция и deduplication слой?
3. Защо cached ACK трябва да съдържа предишния резултат?
4. Какво става с гаранциите при process restart?
5. Как RTO влияе на latency и redundant traffic?
6. Защо transport checksum не заменя protocol validation?
7. Кои TCP услуги още липсват след вашия stop-and-wait протокол?

## 11. Самостоятелно надграждане

Добавете RTT estimator и adaptive RTO. Не използвайте RTT samples от retransmitted requests, при които не знаете кое изпращане е предизвикало ACK. Сравнете retries и completion time при променлив delay с фиксирания RTO.

## 12. Очакван резултат

Reliable UDP demonstrator с bounded stop-and-wait, повторяем fault harness и честно описание на гаранциите. В `results/lab03/` предайте CSV, timeline за lost ACK и анализ на restart ambiguity.

## 13. Критерии за приемане

- [ ] Datagram length и type-specific bounds се проверяват.
- [ ] Има ACK matching по endpoint/session/sequence.
- [ ] Загуба на ACK не води до повторно изпълнение.
- [ ] Retry count, общ deadline, cache и fault queue са ограничени.
- [ ] Loss/duplication/delay/reordering имат concrete tests.
- [ ] 100% loss приключва без busy loop или безкрайно чакане.
- [ ] Докладът отделя guaranteed behavior от restart/expiry ограниченията.

## 14. Допълнителни задачи

1. Добавете bounded sliding window с отделна спецификация за out-of-order packets.
2. Изследвайте operation key с persistent journal и crash points; опишете durability trade-offs.
3. Добавете pacing и ограничение на общия retry traffic между няколко clients.
