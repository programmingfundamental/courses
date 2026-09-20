# Lab 6 — Бележки за преподавателя

Само за преподавателя; не се включва в студентския сайт.

## 1. Цел на упражнението

Transport protection и defensive application programming са независими отговорности. Студентът трябва да отхвърли „криптирано значи безопасно“ и да покаже trust/identity negative tests, bounded resources и deadlines.

## 2. Очаквано предварително ниво

Работещ blocking transport от Lab 2, codec guards и budgets от Lab 5. Предварително проверете keytool/capture средата. Подгответе skeleton за KeyStore loading и test credentials извън Git; не предоставяйте trust-all workaround.

## 3. Препоръчително разпределение на времето

| Дейност | Минути |
|---|---:|
| Сценарий и threat model | 10 |
| TLS, trust и deadline semantics | 15 |
| Capture и untrusted handshake | 15 |
| TLS adapter, AUTH, guards | 50 |
| Failure suite | 20 |
| Resource observations | 15 |
| Анализ | 10 |
| **Общо** | **135** |

Certificates могат да са генерирани в подготовката, но студентите трябва да обяснят SAN/trust import. На 90-тата минута трябва да има TLS ECHO след AUTH и failing hostname test. SSLEngine не влиза в задължителните 135 минути.

## 4. Как да бъде въведен проблемът

Покажете четимия payload в plain capture. След TLS демонстрацията изпратете authenticated frame с LENGTH=Integer.MAX_VALUE. Попитайте коя част от TLS би предотвратила allocation — отговорът е „никоя“.

## 5. Основни точки за обяснение

Confidentiality/integrity/authentication не са authorization. Trust и endpoint identity са отделни проверки. TLS records не променят LabNet framing. Handshake е expensive work с admission. SO_TIMEOUT е read policy; absolute deadline изисква remaining-time logic. Repeated authentication failures изискват както connection, така и bounded reconnect policy.

## 6. Чести грешки на студентите

Изключване на certificate checking за „да тръгне“; trust store с private key; грешен SAN; implicit handshake по време на protocol read без deadline; network-provided array length; фиксиран read timeout, представен като frame deadline; setSoTimeout(0) след rounding; watchdog чака write lock; tokens в logs/CLI history; per-IP map без expiry/cap; unauthenticated LOGIN се бърка с identity.

## 7. Насочващи въпроси

Кой удостоверява server-а? Кое име очаквахте и къде е проверено? Какво се случва с socket след handshake executor rejection? Колко време може да стои frame, ако идва byte преди всеки inactivity timeout? Как тестът доказва освобождаване на resources?

## 8. Очаквана архитектура на решението

```text
SSLServerSocket accept
        |
 active permit + bounded handshake executor + watchdog
        |
 successful TLS -> bounded blocking session executor
                         |
               codec guards -> AUTH -> dispatcher
```

Client SSLContext има TrustManagerFactory; server context има KeyManagerFactory. Hostname checking е преди explicit handshake. Session owns socket след handoff; failing path owns close. Guard functions са общи с NIO, но NIO timers се управляват в event loop, а blocking TLS deadlines използват read timeout/watchdog.

## 9. Ключови части от примерно решение

**Не показвайте преди анализа на unsafe allocation.**

```java
if (length < Long.BYTES || length > MAX_PAYLOAD) {
    throw new ProtocolException("BAD_LENGTH");
}
byte[] payload = new byte[length];
```

**След slow-frame опита** дайте remaining-deadline подхода:

```text
remaining = deadlineNanos - nanoTime()
if remaining <= 0: close as expired
timeoutMs = max(1, ceil(remaining / 1_000_000))
socket.setSoTimeout(timeoutMs)
read some bytes; update position; repeat with same absolute deadline
```

Start timestamp е от първия header byte; преди него важи idle policy. При общ IOException не се възстановява parser след частично прочетен frame. Отделният write watchdog не взема session writer lock.

## 10. Как да се демонстрират edge cases

Wrong trust: използвайте trust store с различен locally generated certificate. Wrong name: wrap към loopback с peer name wrong.invalid, запазвайки validation. Slow handshake: raw Socket към TLS port без TLS bytes. Slow frame: валиден TLS client подава byte на 500 ms, така че inactivity timeout би се подновявал, а absolute deadline изтича. Huge length: изпратете само header, не голям payload. AUTH limit: три грешни tokens, после reconnect до per-IP cap; reset на test state става само между isolated runs. След всеки test сравнете counts с baseline.

## 11. Очаквани резултати

Trusted correct identity преминава; wrong trust/name не стига до application dispatch. TLS capture скрива application content, но показва metadata. Slow clients се ограничават по elapsed deadline. След повтарящи се failure runs няма растящ брой active sockets/permits; heap може да остане временно висок до GC. Handshake cost зависи от machine/JDK/session reuse и не се фиксира числово.

## 12. Критерии за оценяване

| Област | Точки | Доказателство |
|---|---:|---|
| Correctness | 25 | TLS adapter, AUTH и unchanged codec |
| Protocol/network understanding | 20 | trust, identity, deadline semantics |
| Robustness | 25 | negative tests, caps, cleanup |
| Code quality | 10 | ownership и secret handling |
| Experimental work | 10 | capture и failure/resource matrix |
| Analysis | 10 | threat-to-control аргументация |
| **Общо** | **100** | |

Trust bypass не получава точки за TLS correctness. Не оценявайте по скорост на handshake; целта е защитеното поведение и доказателствата.

## 13. Въпроси за устна проверка

1. Каква е разликата trust/name? — Trusted issuer/certificate не доказва автоматично, че peer е търсеният host.
2. Защо framing остава? — TLS пренася application bytes; records не са LabNet messages.
3. Защо huge length е опасна и под TLS? — Authenticated client пак контролира input; allocation limit е application отговорност.
4. Как slow sender заобикаля inactivity timer? — Подава progress преди всеки timeout; absolute deadline не се рестартира.
5. Защо AUTH не се използва на plain endpoint? — Credential confidentiality изисква защитения канал; Lab 2 LOGIN е само име.
6. Какво правите при пълна failure map? — Explicit bounded refusal policy, без безкрайно разширяване; обсъжда се NAT trade-off.
7. Защо certificate в capture не е единственото доказателство? — Протоколна версия/шифроване ограничават видимостта; negative validation tests доказват проверката.

## 14. Как упражнението се свързва със следващото

Lab 7 сравнява plain TCP архитектурите при общи policies и отделно TLS overhead на blocking варианта. Сменянето едновременно на transport protection и I/O модел не позволява да се изолира причината за performance разлика. Запазете security suite като correctness gate преди load tests.
