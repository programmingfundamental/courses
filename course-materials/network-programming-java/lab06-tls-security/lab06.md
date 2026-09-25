# Лабораторно упражнение 6 — TLS и defensive network programming

## 1. Контекст и инженерен проблем

LabNet вече работи в споделена лабораторна мрежа. Plain TCP позволява наблюдател да прочете payloads, а клиент с валидна connection може да подаде огромна дължина или да задържи ресурси с бавен frame. Нужни са две отделни защити: доверен encrypted transport и ограничена, валидираща application логика.

**Какво използваме от предходното упражнение:** resource budgets, deadlines и ownership; TLS се добавя върху blocking thread-pool transport от Lab 2 със същия codec. **Какво ще се използва по-късно:** defensive test suite и отделно измерване на handshake и steady-state cost в Lab 7. Задължителната задача не пренася TLS в NIO event loop.

## 2. Учебни цели

- Конфигурира SSLContext, server key material и client trust store.
- Реализира SSLSocket/SSLServerSocket с проверка на peer identity.
- Различава confidentiality, integrity, authentication и authorization.
- Диагностицира TLS handshake failure без изключване на validation.
- Отхвърля malformed frames преди allocation/dispatch.
- Реализира bounded handshake/frame/write/idle deadlines и authentication attempts.
- Проверява resource cleanup при неуспешни и бавни clients.

## 3. Необходими предварителни знания

Lab 1–2 codec и lifecycle, Lab 5 budgets, basic certificate/public-key concepts. Криптографските primitives не се реализират ръчно.

## 4. Необходими инструменти

JDK с `keytool`, IDE, terminal, Git и Wireshark за сравнение на plain/TLS capture. Подгответе локална директория `certs/` в студентския проект, изключена от Git. Използвайте TLS port 9443.

## 5. Теоретична подготовка

TLS защитава поверителността и целостта на application bytes и може да удостовери peer чрез certificate. Certificate chain трябва да е trusted и заявеното server име трябва да съответства на удостоверената идентичност. Encryption без правилна identity validation не е достатъчна. TLS не валидира нашия LENGTH и не ограничава connection count. Вижте [JSSE API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/package-summary.html).

```text
LabNet Message -> същият encoder -> TLS records -> TCP
LabNet Message <- същият decoder <- TLS records <- TCP
```

`SSLContext` получава key managers за собствената identity и trust managers за чуждите certificates. Server private key остава само на server. Client trust store съдържа public certificate/trust anchor, не server private key. За лабораторията изрично доверяваме self-signed server certificate; това е controlled trust setup.

За raw SSLSocket включете endpoint identification **преди** handshake. Стандартното Java име на алгоритъма е `"HTTPS"`; тук то избира само certificate hostname проверката. Обменът остава LabNet binary protocol върху TLS, без web протокол. Вижте [SSLParameters.setEndpointIdentificationAlgorithm](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/net/ssl/SSLParameters.html#setEndpointIdentificationAlgorithm(java.lang.String)).

```java
SSLParameters parameters = socket.getSSLParameters();
parameters.setEndpointIdentificationAlgorithm("HTTPS");
socket.setSSLParameters(parameters);
socket.setEnabledProtocols(new String[] { "TLSv1.3" });
socket.startHandshake();
```

Това е client snippet. `socket` трябва да е създаден с очакваното hostname, а SSLContext — с реалния trust store. Не използвайте trust-all TrustManager или permissive verifier. Използвайте default secure cipher selection на JDK, а не ръчно подбран списък от стари algorithms.

### Defensive invariants

Никога не приемайте network-provided length като безопасна allocation инструкция. Следният код е **умишлено небезопасен**:

```java
int length = in.readInt();
byte[] payload = new byte[length];
```

Negative length, огромна стойност, truncated frame и slow sender водят до различни failure paths. Guard-ът от Lab 1 остава задължителен и под TLS. Добавяме ограничения на **времето и сумарните ресурси**, не само на размера на един frame.

| Policy | Начална стойност | Как се прилага |
|---|---:|---|
| Active TCP/TLS connections | 64 | permit преди handshake |
| Едновременни handshakes | 4 | bounded handshake executor, без waiting socket queue |
| Absolute handshake deadline | 3 s | watchdog close + bounded read timeout |
| Frame completion deadline | 2 s от първия byte | remaining-time read timeout преди всяко read |
| Idle connection | 10 s | inactivity timer |
| Write/close drain deadline | 2 s | watchdog затваря socket |
| MAX_PAYLOAD | 65536 | guard преди allocation |
| Authentication failures | 3 на connection | close при лимита |

## 6. Начален експеримент

Capture-нете ECHO с отличим тестов текст през plain TCP и намерете текста във Follow TCP Stream. После изпълнете същата заявка върху TLS, без да експортирате session keys, и проверете, че wire payload не се вижда като plain LabNet bytes. Все още се наблюдават адреси, ports, packet sizes и timings — TLS не скрива целия traffic pattern.

След това посочете client към trust store, който не съдържа server certificate. Наблюдавайте handshake failure и потвърдете, че application dispatcher не е получил request. Запазете error category, без private keys или secrets в логовете.

## 7. Основна лабораторна задача

### Стъпка 1 — certificates и trust

Изпълнете от студентския проект след създаване на `certs/`; паролите се въвеждат интерактивно. Командите са едноредови и работят в terminal с JDK tools:

```sh
keytool -genkeypair -alias labnet-server -keyalg RSA -keysize 3072 -validity 30 -storetype PKCS12 -keystore certs/server.p12 -dname "CN=localhost" -ext "SAN=dns:localhost,ip:127.0.0.1"
keytool -exportcert -alias labnet-server -keystore certs/server.p12 -rfc -file certs/server.crt
keytool -printcert -file certs/server.crt
keytool -importcert -alias labnet-server -file certs/server.crt -storetype PKCS12 -keystore certs/client-trust.p12
```

Проверете fingerprint по локално изнесения certificate и потвърдете import само за този certificate. SAN определя позволените имена. Командните options са описани в [keytool manual](https://docs.oracle.com/en/java/javase/25/docs/specs/man/keytool.html).

### Стъпка 2 — SSLContext и transport adapter

```java
SSLContext serverContext(Path keyStore, char[] password)
        throws GeneralSecurityException, IOException;
SSLContext clientContext(Path trustStore, char[] password)
        throws GeneralSecurityException, IOException;
```

Заредете PKCS12 чрез KeyStore, инициализирайте KeyManagerFactory за server и TrustManagerFactory за client, после `SSLContext.getInstance("TLS")`. Create-нете SSLServerSocket/SSLSocket от контекстите. Извикайте explicit handshake преди protocol reads; разрешете TLSv1.3 на двете страни. Existing encoder/decoder работят със същите InputStream/OutputStream interfaces.

Acceptor предава handshake на bounded executor с 4 workers и `SynchronousQueue`; rejection затваря connection. След success socket се предава към bounded session executor от Lab 2. Ownership се сменя само при успешно submit; всяка друга пътека затваря и освобождава permit. Не изпълнявайте handshake в acceptor thread.

### Стъпка 3 — доверие и identity tests

Проверете trusted localhost success, untrusted certificate failure и trusted certificate с wrong hostname failure. За последния test отворете TCP към loopback и wrap-нете socket с очакваното peer име `wrong.invalid`, като оставите endpoint identification включен. Така test-ът достига TLS на правилния port, без да изисква DNS промени.

### Стъпка 4 — authentication и repeated failures

Добавете `AUTH=0x15`. Body: 1-byte username length + ASCII username от Lab 2 + UTF-8 token с 16..128 bytes. Server test configuration задава един `LABNET_USER` и random `LABNET_TOKEN` през environment; липсваща конфигурация отказва startup. Това е лабораторен authentication adapter, не production identity system.

AUTH се допуска само на TLS endpoint. Сравнете user и token с очакваните, използвайки bounded input и `MessageDigest.isEqual` за token bytes. Никога не log-вайте token. При успех маркирайте authenticated principal; последващ LOGIN може да заяви само същото име. PING/QUIT могат да бъдат diagnostics преди AUTH; ECHO и останалите application commands на TLS endpoint изискват AUTH. Повторен AUTH след успех връща `ALREADY_AUTHENTICATED`; грешен credential — общо `AUTH_FAILED`, без различаване „няма user“/„грешен token“.

След 3 неуспеха затворете connection. За reconnect abuse добавете bounded per-source-IP failure map: най-много 1024 записи, TTL=60 s, блокиране след 5 failures/60 s. Updates са атомарни; при пълен map отказвайте нови authentication attempts, вместо да създавате unbounded state. Това може да засегне честни users зад един NAT — документирайте trade-off.

### Стъпка 5 — absolute deadlines и defensive tests

За frame deadline отбележете `System.nanoTime()` при първия byte. Преди всяко следващо read изчислете оставащото време; при <=0 затворете. Преобразувайте в timeout с закръгляне нагоре и minimum 1 ms — `setSoTimeout(0)` означава безкрайно чакане. Не използвайте едно `readFully()` с фиксиран inactivity timeout като доказателство за absolute frame deadline.

Handshake/write watchdog може да е един periodic scan на tracked sessions, вместо unbounded timer task на всеки byte. Deadline close не взема write lock. При expiry всички buffers, permits и queued sessions се освобождават точно веднъж. Malformed input се log-ва с bounded reason code и sampling, за да не се превърне log volume в нов resource problem.

## 8. Failure scenarios и edge cases

| Случай | Провокация | Очакване |
|---|---|---|
| Negative/huge length | -1 и Integer.MAX_VALUE в header | отказ преди allocation |
| Invalid version/command | VERSION=2; unknown TYPE в валиден frame | close / BAD_TYPE според договора |
| Truncated frame | TLS close след partial body | без dispatch, local cleanup |
| Slow frame | по 1 byte на 500 ms | absolute deadline, въпреки активност |
| Idle connection | handshake без requests | close по idle timeout |
| Slow handshake | raw TCP client не започва TLS | handshake deadline и свободен slot |
| Connection exhaustion | повече от cap sockets | bounded refusal, server остава жив |
| Wrong trust/hostname | отделни client contexts | handshake failure, без trust bypass |
| Repeated AUTH failures | 3 в една / 5 през reconnect | bounded lockout и липса на token logs |
| Slow response reader | спира да чете | write watchdog, освободен worker |

Сравнете counters преди/след 100 неуспешни опита: active sessions, handshakes и permits трябва да се върнат към baseline. Памeтта може да не спадне веднага заради GC; retained resource count е по-точен test от моментен heap размер.

## 9. Наблюдение и измерване

Съберете handshake success/failure по категории, duration, timeout counts, invalid frames, auth failures и active resources. Използвайте `-Djavax.net.debug=ssl:handshake` само при конкретен диагностичен run, после го изключете за performance. Не добавяйте секрети в report.

Предайте threat-to-control таблица: проблем → guard/limit → test → наблюдение. Разграничете handshake time от steady-state ECHO latency. Capture показва encrypted records, но успешната identity проверка се доказва с negative tests, не само с липса на четим текст.

## 10. Въпроси за анализ

1. Защо encrypted connection с wrong peer е недостатъчна?
2. Каква е разликата между server certificate trust и проверка на hostname?
3. Защо TLS не предотвратява oversized payload allocation?
4. Как slow byte trick заобикаля само inactivity timeout?
5. Защо trust store не трябва да съдържа server private key?
6. Какво защитава per-connection AUTH limit и какво пропуска?
7. Какви компромиси въвежда per-IP limiter при NAT?
8. Кой затваря socket при failed handoff между handshake и session executor?

## 11. Самостоятелно надграждане

Добавете mutual TLS с отделен client certificate, server trust store и `setNeedClientAuth(true)`. Свържете удостоверения certificate principal с позволено LabNet име; успешен TLS handshake сам по себе си не разрешава произволен LOGIN.

## 12. Очакван резултат

TLS adapter със същия frame codec, проверка на trust и hostname, ограничен AUTH механизъм и defensive suite. В `results/lab06/` има threat-to-control матрица и resource cleanup evidence, без private keys или tokens.

## 13. Критерии за приемане

- [ ] SSLContext използва отделни server key и client trust материали.
- [ ] Identity validation е включена и wrong hostname се отхвърля.
- [ ] Application dispatcher не работи преди успешен handshake.
- [ ] Няма allocation по unvalidated network length.
- [ ] Handshake/frame/write/idle deadlines имат различими tests.
- [ ] AUTH attempts и per-IP state са bounded.
- [ ] Повтарящи се failures не оставят sockets/permits/workers заети.
- [ ] Capture и logs не разкриват tokens/private keys.

## 14. Допълнителни задачи

1. Интегрирайте SSLEngine с NIO, включително NEED_WRAP/NEED_UNWRAP/NEED_TASK, buffer limits и close_notify.
2. Изследвайте certificate rotation с два последователни локални certificates и explicit trust update.
3. Направете bounded property-based malformed-frame harness през plain и TLS transport с еднакви очаквания.
