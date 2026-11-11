# Помощни материали за Java чата

Отворете този файл в IntelliJ с Markdown Preview. За работа от празен проект започнете с първия разбор. За конкретен въпрос по кода използвайте таблицата.

| Какво проследявате | Код в този проект | Обяснение по редове |
|---|---|---|
| Как „Чат“ се превръща в 20 байта TCP кадър | [Frame](src/labnet/Frame.java) | [1. Кодиране, четене и първи работещ обмен](../lab01-tcp-protocol/hint.md) |
| Защо авторът вижда собственото съобщение точно веднъж | [Room](src/labnet/Room.java), [ChatClient](src/labnet/ChatClient.java), [ChatConnection](src/labnet/blocking/ChatConnection.java) | [2. Регистрация, изпращане и получаване](../lab02-concurrent-server/hint.md) |
| Къде повторението на UDP операцията запазва същия номер | [Counter](src/labnet/udp/Counter.java) | [3. Изгубено потвърждение и брояч](../lab03-reliable-udp/hint.md) |
| Къде остават байтовете от недовършения кадър | [ChatServer](src/labnet/ChatServer.java), [SelectorSession](src/labnet/transport/SelectorSession.java) | [4. Селектор и частичен запис](../lab04-java-nio/hint.md) |
| Защо три публикации могат да чакат Ива и кога получават отказ | [Session](src/labnet/transport/Session.java), [Room](src/labnet/Room.java) | [5. Опашки, граници и WORK](../lab05-backpressure/hint.md) |
| Кой проверява сертификата и кой разрешава името на участника | [DemoKeys](src/labnet/tls/DemoKeys.java), [BlockingSession](src/labnet/transport/BlockingSession.java), [SelectorSession](src/labnet/transport/SelectorSession.java) | [6. TLS, AUTH и LOGIN](../lab06-tls-security/hint.md) |
| Защо 30 публикации дават 90 измерени събития | [Benchmark](src/labnet/demo/Benchmark.java) | [7. Проверки, p95 и CSV](../lab07-performance/hint.md) |

За командите в двете конзоли, очаквания разговор и реда за пускане на тестовете използвайте [ръководството за демонстрация](../hint.md).

## Сглобяване на пълната версия от празен проект

В IntelliJ създайте Java проект с JDK 21 или по-нова версия. Маркирайте `src` като **Sources Root**. Вътре създавайте пакетите според първия ред на всеки файл. Например `package labnet.transport;` означава директория `src/labnet/transport`. Разборите по-горе дават учебния ред за добавяне на поведението. За да сглобите същия краен вариант файл по файл и да проверявате типовете още преди първото стартиране, използвайте следния ред на компилация.

| Стъпка | Какво да създадете | Какво вече можете да проверите |
|---|---|---|
| 1 | `Frame`, `Options` и `ConsoleIO` в основния пакет. | Изпълнете краткия `EchoProbe` от упражнение 1. Очакваният резултат е `2 Чат`. |
| 2 | `Room` и `blocking/ChatConnection`. | Стаята вече приема абстрактен `Room.Peer`, а клиентската връзка разпознава `EVENT` и отговор по номер. И двата класа използват `Frame`. |
| 3 | `udp/Counter` и `tls/DemoKeys`. | Вече имате типовете за отчитане на показан ред и за зареждане на двата TLS контекста. |
| 4 | Първо `transport/Session`, после `BlockingSession` и `SelectorSession` в същия пакет. | Двата транспорта могат да предадат кадър към една и съща стая и да потвърдят завършен запис. |
| 5 | `ChatServer` в основния пакет и `demo/Benchmark`. | Стартирайте сървъра. Изчакайте `ChatServer ready` и въведете `/stats`. Преди вход на клиент очаквайте `active=0;loggedIn=0`. |
| 6 | `ChatClient` в основния пакет. | Пуснете клиента от същата директория и влезте като Ана, Борис и Ива. Всеки трябва да види първия ред на Ана точно веднъж. |

Изпълнявайте съответния ред от PowerShell в корена на новия проект след всяка стъпка. `-cp out` прави вече компилираните типове достъпни за следващата команда. Дръжте оригиналните Java файлове от таблицата отворени, за да виждате също декларациите на пакетите и необходимите `import` редове.

```powershell
javac --release 21 -encoding UTF-8 -d out src/labnet/Frame.java src/labnet/Options.java src/labnet/ConsoleIO.java
javac --release 21 -encoding UTF-8 -cp out -d out src/labnet/Room.java src/labnet/blocking/ChatConnection.java
javac --release 21 -encoding UTF-8 -cp out -d out src/labnet/udp/Counter.java src/labnet/tls/DemoKeys.java
javac --release 21 -encoding UTF-8 -cp out -d out src/labnet/transport/Session.java src/labnet/transport/BlockingSession.java src/labnet/transport/SelectorSession.java
javac --release 21 -encoding UTF-8 -cp out -d out src/labnet/ChatServer.java src/labnet/demo/Benchmark.java
javac --release 21 -encoding UTF-8 -cp out -d out src/labnet/ChatClient.java
```

В първия терминал изпълнете `java -cp out labnet.ChatServer`, а във втория — `java -cp out labnet.ChatClient`. В клиентската конзола въведете:

```text
/new ana
/new boris
/new iva
/use ana
Здравейте!
```

За последна проверка изпълнете `/use iva` и `/quit`, после от Ана изпратете `До утре!`. Броят в потвърждението трябва да се промени от `delivered=3;failed=0` на `delivered=2;failed=0`.
