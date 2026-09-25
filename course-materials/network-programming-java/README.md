# Мрежово програмиране с Java

Практически курс за магистри към дисциплината „Програмиране в мрежова среда“: **7 лабораторни упражнения по 135 минути**. Общият проект е **LabNet** — услуга за обмен на съобщения и изпълнение на контролирани задачи през собствен binary protocol. Развиваме протокола, сменяме модела за I/O, въвеждаме ограничени ресурси и сравняваме измереното поведение.

Всяка лаборатория следва: **проблем → теория → минимален експеримент → реализация → откази → измерване → анализ → надграждане**. Посочените времена са за задължителната работа; самостоятелните и допълнителните задачи са извън тези 135 минути. Не се предоставя готов сървър: interfaces, спецификации и отделни snippets задават договорите, а студентите реализират механизмите.

## Аудитория и предварителни знания

Необходими са добро владеене на Java и ООП, exceptions и resource management, основи на threads и synchronization, TCP/IP адресиране, процеси и файлови дескриптори. Преди първото занятие проверете, че можете да компилирате Java проект, да стартирате два JVM процеса и да обясните разликата между IP адрес и port.

## Резултати от обучението

След курса студентът може да:

- специфицира, кодира и валидира binary application protocol;
- диагностицира fragmentation, EOF, timeout и lifecycle на връзка;
- реализира и сравнява blocking, thread-pool и selector server;
- конструира ограничен reliable UDP протокол и обясни границите на гаранциите му;
- отделя network I/O от application processing с bounded queues;
- прилага backpressure, admission control и fairness;
- конфигурира TLS с проверка на доверие и идентичност и ограничава некоректен вход;
- провежда възпроизводим load test и интерпретира throughput, latency percentiles и грешки.

## Програма и зависимости

| № | Студентско упражнение | Използва | Артефакт за следващите занятия |
|---|---|---|---|
| 1 | [TCP sockets, byte streams и application protocol](lab01-tcp-protocol/lab01.md) | Java I/O | Message, encoder, blocking decoder, клиент, baseline server, test vectors |
| 2 | [Concurrent TCP Server](lab02-concurrent-server/lab02.md) | Lab 1 | SessionRegistry, bounded connection pool, команден dispatcher |
| 3 | [UDP и reliability](lab03-reliable-udp/lab03.md) | payload validation и измервания от Lab 1–2 | UDP adapter, fault injector, retry/deduplication tests |
| 4 | [Java NIO и event-driven networking](lab04-java-nio/lab04.md) | TCP protocol и dispatcher от Lab 1–2; fault mindset от Lab 3 | IncrementalDecoder, ConnectionState, event loop |
| 5 | [Async processing, queues и backpressure](lab05-backpressure/lab05.md) | Lab 4 | bounded worker/completion/output pipeline и metrics |
| 6 | [TLS и defensive network programming](lab06-tls-security/lab06.md) | blocking transport от Lab 2; validation и budgets от Lab 5 | TLS adapter и общи defensive policies |
| 7 | [Performance engineering и сравнителен анализ](lab07-performance/lab07.md) | всички TCP варианти; измервателни принципи от Lab 3 | Java load generator, CSV, технически доклад |

```text
TCP + Framing
      |
      v
Concurrency
      |
      v
UDP + Reliability       (отделен transport adapter)
      |
      v
NIO / Event Loop        (стъпва върху TCP кода от Lab 1–2)
      |
      v
Queues + Backpressure
      |
      v
TLS + Defensive Programming
      |
      v
Performance Engineering
```

Линейната схема показва учебния ред, а таблицата — зависимостите на кода. UDP не замества TCP версията. TLS в задължителната част използва blocking sockets; интеграция на `SSLEngine` с selector е допълнителна задача. Това позволява да се запази реалистичен обем за едно занятие.

## Софтуер и подготовка

Препоръчителна версия: **JDK 25 LTS**, без preview features. Упражненията могат да се изпълняват и с JDK 21; всички участници в сравнителен експеримент използват една и съща версия и JVM flags. Статусът на LTS версиите е описан в [Java SE roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html).

Необходими са IDE, terminal, Git и Wireshark с възможност за capture на loopback интерфейса. `keytool`, `jcmd` и JFR идват с JDK. На Windows loopback capture изисква подходящата Npcap инсталация. Ако capture липсва, преподавателят предоставя запис от собствената лабораторна демонстрация; protocol tests остават задължителни. Не са необходими външни Java библиотеки или build framework. JUnit може да се използва по избор; Java test runner с explicit checks е достатъчен.

Преди занятията подгответе skeleton проекта, празните методи и командите за стартиране, без решена network логика. Студентите запазват работещата версия от всяка седмица. За Lab 6 сертификатите се генерират предварително или в началните 15 минути. За Lab 7 се подготвя CSV заглавие и skeleton на генератора, за да остане време за измервания.

## Работа с repository-то

Тази директория съдържа учебните материали. Реализацията се поддържа в отделен студентски Git repository с примерна структура:

```text
src/labnet/protocol/      Message, Types, Encoder, Decoder
src/labnet/blocking/      TcpClient, TcpServer, ClientSession
src/labnet/service/       CommandDispatcher, SessionRegistry
src/labnet/udp/           ReliableUdpClient, UdpReceiver, FaultInjector
src/labnet/nio/           NioServer, ConnectionState, IncrementalDecoder
src/labnet/async/         WorkService, AdmissionPolicy
src/labnet/tls/           TlsContexts, TlsClient, TlsServer
src/labnet/load/          LoadGenerator, Recorder
test/labnet/              protocol, lifecycle и integration checks
results/labXX/            конфигурация, CSV, наблюдения, анализ
```

Примерни команди след създаване на класовете (списъкът `sources.txt` съдържа по един `.java` път на ред):

```sh
javac -encoding UTF-8 -d out @sources.txt
java -ea -cp out labnet.blocking.TcpServer --port 9000
java -ea -cp out labnet.blocking.TcpClient --host localhost --port 9000
```

CLI парсерът е малък метод от standard library и е част от подготовката. Записвайте `java -version`, commit, flags, seed, machine/OS, limits и test command. Използвайте Git tags `lab01` … `lab07`, вместо да презаписвате baseline. Сертификати с private keys, пароли, heap dumps и големи capture файлове не се добавят в Git; commit-ват се instructions и обезличени резултати.

## Общ договор на LabNet

Подробната wire спецификация е в Lab 1; тя е обща за всички TCP реализации. `VERSION=1`, header е 6 bytes, `MAX_PAYLOAD=65536`, big-endian. Всеки request/response payload започва с положителен 64-bit `requestId`; `EVENT` използва `requestId=0`. ID е уникален в рамките на TCP connection. Дължината включва ID и body. Limits за отделните команди могат да са по-малки от максимума на frame.

`PING=0x01`, `ECHO=0x02`, `QUIT=0x03`; Lab 2 добавя `LOGIN=0x10`, `SEND=0x11`, `BROADCAST=0x12`, `USERS=0x13`, `STATS=0x14`; Lab 5 добавя `WORK=0x20`, а Lab 6 — `AUTH=0x15` само върху TLS. Отговорите са `OK=0x80`, `DATA=0x81`, `EVENT=0x82`, `ERROR=0xFF`. Неизвестна команда с валиден frame получава `ERROR/BAD_TYPE`; невалиден framing прекратява connection. Никой decoder не се опитва да се ресинхронизира чрез търсене на „правдоподобен“ header в payload.

Request IDs нарастват строго в реда на подаване по connection; server пази само последния приет ID, вместо неограничен set. Response IDs могат да пристигат в друг ред. По подразбиране има най-много един outstanding request на connection. Lab 5 въвежда ограничено pipelining и correlation по ID; `QUIT` се изпраща след изчакване на всички отговори. Login името от Lab 2 е идентификатор, не доказателство за самоличност. Lab 6 отделно въвежда authentication.

## Общи правила и предаване

1. За всяко занятие предайте source, commands, поне четири failure tests, измервания и кратки отговори на въпросите за анализ.
2. Преди измерване напишете хипотеза; след него разграничете наблюдение от обяснение. Не измисляйте резултати и не обявявайте архитектура за универсален победител.
3. Използвайте `System.nanoTime()` за интервали в един процес, wall clock за човешки logs. Не изваждайте timestamps от различни машини без clock synchronization.
4. Всеки socket/channel, task и buffer има owner, lifecycle и limit. Всеки timeout уточнява дали ограничава inactivity или цяла операция.
5. Java `OutputStream.write(byte[])` няма short-write return value; NIO `SocketChannel.write()` има. TCP segmentation не е application framing.
6. Не допускайте безкрайни retries, неограничени queues, unvalidated allocations или blocking работа в selector thread.
7. Експериментите се изпълняват на loopback или разрешена лабораторна мрежа с конфигурирани limits. Първо потвърдете correctness, после увеличавайте товара.
8. Оценява се аргументираният инженерeн процес, а не най-високото число за requests/s. Скалата във всяка преподавателска бележка е 100 точки.

## Авторство и публикуване на материалите

`labXX.md` са източникът за студентските страници в сайта. След редакция изпълнете `node scripts/sync-network-labs.mjs` от корена на repository-то; `--check` проверява дали копията са актуални. Файловете `instructor-notes.md` са предназначени за преподаватели и не се копират в сайта. Това не ги прави поверителни в публично Git repository; при нужда от ограничен достъп те трябва да се разпространяват през отделно преподавателско хранилище.
