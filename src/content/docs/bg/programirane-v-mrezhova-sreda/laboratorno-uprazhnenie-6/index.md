---
title: "Лабораторно упражнение 6 — TLS и defensive network programming"
sidebar:
  order: 6
  label: Упражнение 6
---

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
