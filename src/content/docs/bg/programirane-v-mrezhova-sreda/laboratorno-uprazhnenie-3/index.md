---
title: "Лабораторно упражнение 3 — UDP и reliability"
sidebar:
  order: 3
  label: Упражнение 3
---

# Лабораторно упражнение 3 — UDP и reliability

## 1. Контекст и инженерен проблем

Измервателно устройство изпраща прираст на брояч към LabNet. Повторното изпращане изглежда като лесен начин за справяне със загуба, но ако първият пакет е обработен и само отговорът е изгубен, броячът се увеличава два пъти. Трябва да различим липса на отговор от липса на изпълнение.

**Какво използваме от предходното упражнение:** validation, identifiers, metrics и lifecycle подхода. TCP framing classes остават непокътнати; UDP получава отделен codec. **Какво ще се използва по-късно:** детерминиран fault injection, idempotency и разграничението между submitted, executed и acknowledged операции подпомагат backpressure и load testing.

## 2. Учебни цели

- Реализира DatagramSocket/DatagramPacket обмен с точна проверка на размерите.
- Изгражда stop-and-wait протокол с sequence numbers и ACK.
- Реализира bounded timeout, retransmission и duplicate detection.
- Симулира loss, duplication, delay и reordering с възпроизводим seed.
- Разграничава retry от idempotency и delivery от execution.
- Измерва useful goodput, retries, duplicates и failure rate.
- Аргументира границите на гаранциите при restart и cache expiry.

## 3. Необходими предварителни знания

Lab 1–2, byte encoding, timers, map state, UDP адресиране. Трябва да можете да напишете test със зададен seed и bounded runtime.

## 4. Необходими инструменти

JDK, IDE, terminal, Git. Wireshark е полезен за `udp.port == 9001`, но основното доказателство са counters и server state. Всички fault сценарии могат да се изпълнят в Java без OS network emulator.

## 5. Теоретична подготовка

UDP е connectionless: изпращаме datagram до адрес/port, без TCP handshake. Datagram boundaries се запазват, но доставка, ред и уникалност не са гарантирани. `DatagramSocket.connect()` избира peer и филтрира обмена; не добавя reliability. Приложението определя ACK, retry и state. UDP няма и автоматичен congestion control; упражнението е ограничен лабораторен протокол, не заместител на TCP за Internet. Вижте [UDP usage guidelines](https://www.rfc-editor.org/rfc/rfc8085.html).

### Wire format и инварианти

```text
+-------------+------------+----------+----------+-------------+
| SESSION_ID  | SEQ        | TYPE     | LENGTH   | DATA        |
| 8 bytes     | 8 bytes    | 1 byte   | 2 bytes  | LENGTH      |
+-------------+------------+----------+----------+-------------+
```

Всички integers са big-endian; session/seq са положителни Java long, LENGTH е unsigned 16-bit. Header=19 bytes, `MAX_DATAGRAM=1200`, `MAX_DATA=1181`. Избраният размер намалява риска от fragmentation в типичната лабораторна среда, но не доказва подходящ MTU за произволен network path.

| TYPE | DATA | Значение |
|---|---|---|
| DATA=1 | signed long delta, `-100..100`, LENGTH=8 | добави към session counter |
| ACK=2 | signed long total, LENGTH=8 | резултат след изпълнение на SEQ |
| ERROR=3 | UTF-8 code, 1..64 bytes | SERVER_BUSY, SESSION_EXPIRED или BAD_SEQUENCE |

Client избира random положителен SESSION_ID веднъж за run и започва SEQ=1. Само една DATA заявка е outstanding. Server key е `(source IP, source port, SESSION_ID)`. За нова session се приема само SEQ=1; max active sessions=32. Няма wraparound: преди Long.MAX_VALUE се започва нова session. Counter overflow се проверява с `Math.addExact` и се прекратява тестът с explicit error, вместо да се приема неверен резултат.

Server пази последния изпълнен SEQ, cached ACK и total. Ако пристигне точно следващият SEQ, изпълнява веднъж и запазва отговора **преди изпращането му**. Ако е същият като последния — връща cached ACK, без повторно изпълнение. По-стар SEQ се игнорира и се брои като stale; прескочен SEQ получава BAD_SEQUENCE. Само един receiver thread променя тази map.

Session state се пази поне 60 s от последната валидна активност; retry horizon е под 10 s. При пълен cache новата session получава SERVER_BUSY; не се изтрива active state за място. След expiry или server restart client не може да знае дали последната операция е изпълнена. Това е bounded duplicate suppression в една server lifetime, **не exactly-once гаранция**.

За receive използвайте buffer `MAX_DATAGRAM+1`; packet с върната дължина над 1200 се отхвърля, дори ако по-голям datagram е бил truncated. Проверявайте `actualLength == 19 + declaredLength` и type-specific size преди parsing. При reuse на DatagramPacket възстановявайте `setLength(buffer.length)` преди receive. API описва packet length/capacity в [DatagramPacket](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/DatagramPacket.html).
