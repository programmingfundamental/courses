# Упражнение 6 — TLS, доверие и вход по име

**След общата стая и ограниченията.** При връзка от Ана има три последователни събития. Първо TLS установява защитена връзка с проверено име на сървъра. После `AUTH` проверява учебния токен на Ана. Накрая `LOGIN` заема името `ana` в стаята. Запазете `Frame`, `Room.publish` и обработката на съобщенията и за двата транспорта.

## 1. Подгответе локалните ключове преди сървърът да започне да приема

Създайте `DemoKeys`. За локалния опит сървърът и клиентът се пускат от една работна директория. `server.p12` съдържа частния ключ на сървъра. Клиентът зарежда `trust.p12` с доверения публичен сертификат и токена на избраното учебно име.

[DemoKeys.java · редове 11–31](../chat-solution/src/labnet/tls/DemoKeys.java#L11-L31)

```java
public final class DemoKeys {
    public static final List<String> NAMES = List.of("ana", "boris", "iva");
    private final Path directory = Path.of(".local", "tls");

    private static String secret() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    // keytool receives the password through its environment, never through its argument list.
    private static void keytool(Path cwd, String password, String... arguments) throws Exception {
        String executable = File.separatorChar == '\\' ? "keytool.exe" : "keytool";
        var command = new ArrayList<String>();
        command.add(Path.of(System.getProperty("java.home"), "bin", executable).toString());
        command.addAll(List.of(arguments));
        command.addAll(List.of("-storepass:env", "LABNET_STORE_PASSWORD", "-storetype", "PKCS12"));
        var process = new ProcessBuilder(command).directory(cwd.toFile()).inheritIO();
        process.environment().put("LABNET_STORE_PASSWORD", password);
        if (process.start().waitFor() != 0) throw new IOException("keytool failed");
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 12–13 | Запишете трите начални имена и пътя под текущата работна директория. |
| 15–18 | Създайте 24 случайни байта със `SecureRandom` и ги представете като 48 шестнадесетични знака. Така се създават паролата за хранилището и отделните токени. |
| 21–23 | Методът изпълнява инструмента на JDK. Изберете името му за съответната операционна система. |
| 24–26 | Започнете списък с аргументи от пълния път в текущия `java.home` и добавете операцията, например генериране на ключ. |
| 27 | Опцията `-storepass:env` казва на инструмента да вземе паролата от променлива на средата. Типът на хранилището е PKCS12. |
| 28 | Създайте процеса с правилната работна директория и свържете стандартните му потоци с родителския процес. |
| 29 | Задайте паролата в средата на дъщерния процес. |
| 30 | Изчакайте резултата от `keytool`. При ненулев изход прекратете подготовката. |

[DemoKeys.java · редове 33–54](../chat-solution/src/labnet/tls/DemoKeys.java#L33-L54)

```java
    public synchronized void prepare() throws Exception {
        if (Files.isDirectory(directory)) { tokens(); serverContext(); clientContext(true); return; }
        Files.createDirectories(directory.getParent());
        Path staging = Files.createTempDirectory(directory.getParent(), "keys-");
        try {
            String password = secret();
            keytool(staging, password, "-genkeypair", "-alias", "chat", "-keyalg", "RSA", "-keysize", "2048",
                    "-validity", "365", "-keystore", "server.p12", "-dname", "CN=localhost", "-ext", "SAN=dns:localhost,ip:127.0.0.1");
            keytool(staging, password, "-exportcert", "-alias", "chat", "-keystore", "server.p12", "-file", "server.cer");
            keytool(staging, password, "-importcert", "-noprompt", "-alias", "chat", "-file", "server.cer", "-keystore", "trust.p12");
            Files.writeString(staging.resolve("store.pass"), password);
            for (String name : NAMES) Files.writeString(staging.resolve(name + ".token"), secret());
            Files.move(staging, directory);
        } finally {
            if (Files.exists(staging)) {
                try (var files = Files.list(staging)) {
                    for (Path file : files.toList()) Files.delete(file);
                }
                Files.delete(staging);
            }
        }
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 33–34 | При вече съществуваща директория опитайте да заредите токените и двата TLS контекста. Така следващото стартиране използва същата идентичност. |
| 35–36 | Създайте родителската директория и временна директория за текущата подготовка. |
| 38 | Генерирайте общата локална парола за хранилищата. |
| 39–40 | Създайте RSA двойка с ключ 2048 бита и самоподписан сертификат за 365 дни. SAN включва `localhost` и `127.0.0.1` за проверката на името. |
| 41 | Изнесете само сертификата в `server.cer`. |
| 42 | Внесете този конкретен сертификат в учебното хранилище на доверие. |
| 43–44 | Запишете локалната парола и различен, нов токен за всеки от тримата. |
| 45 | Преместете подготвената директория на окончателното място след успешното създаване на всички файлове. |
| 46–49 | При останала временна директория обходете и изтрийте файловете от тази подготовка. |
| 51 | Премахнете изпразнената временна директория. |

[DemoKeys.java · редове 56–85](../chat-solution/src/labnet/tls/DemoKeys.java#L56-L85)

```java
    public byte[] token(String name) throws IOException {
        checkName(name);
        Path token = directory.resolve(name + ".token");
        if (!Files.isRegularFile(token)) throw new IOException("No demo credential for " + name);
        return Files.readAllBytes(token);
    }
    private void checkName(String name) throws IOException {
        if (!name.matches("[a-z][a-z0-9_-]{0,23}") || name.equals("server")) throw new IOException("Invalid demo name");
    }
    public byte[] provision(String name) throws IOException {
        checkName(name);
        try { Files.writeString(directory.resolve(name + ".token"), secret(), StandardOpenOption.CREATE_NEW); }
        catch (FileAlreadyExistsException existing) { /* Keep the same local identity after restart. */ }
        return token(name);
    }
    public Map<String, byte[]> tokens() throws IOException {
        var result = new HashMap<String, byte[]>();
        try (var files = Files.newDirectoryStream(directory, "*.token")) {
            for (Path file : files) {
                String filename = file.getFileName().toString();
                String name = filename.substring(0, filename.length() - ".token".length());
                result.put(name, token(name));
            }
        }
        return result;
    }
    private KeyStore store(String file, char[] password) throws Exception {
        var store = KeyStore.getInstance("PKCS12");
        try (var in = Files.newInputStream(directory.resolve(file))) { store.load(in, password); }
        return store;
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 56–58 | Проверете учебното име и образувайте пътя до неговия файл. |
| 59–60 | Сигнализирайте липсващ токен или върнете байтовете му. |
| 62–63 | За име на локален файл са позволени малки латински букви, цифри, `_` и `-` с начална буква и до 24 знака. `server` е запазено. |
| 65–67 | При създаване на участник проверете името и използвайте `CREATE_NEW`, за да не замените съществуващ токен. |
| 68–69 | Ако файлът вече съществува, прочетете и върнете наличния токен. |
| 71–74 | Подгответе карта и обходете само файловете с разширение `.token`. |
| 75–77 | Отделете името от разширението и го свържете със стойността от `token(name)`. |
| 80 | Върнете картата за сървърната проверка на AUTH. |
| 82–85 | Създайте PKCS12 обект, заредете посочения файл с паролата и затворете входния поток. |

[DemoKeys.java · редове 87–107](../chat-solution/src/labnet/tls/DemoKeys.java#L87-L107)

```java
    public SSLContext serverContext() throws Exception {
        char[] password = Files.readString(directory.resolve("store.pass")).toCharArray();
        var managers = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        managers.init(store("server.p12", password), password);
        var context = SSLContext.getInstance("TLSv1.3");
        context.init(managers.getKeyManagers(), null, null);
        return context;
    }
    public SSLContext clientContext(boolean trusted) throws Exception {
        var managers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        if (trusted) managers.init(store("trust.p12", Files.readString(directory.resolve("store.pass")).toCharArray()));
        else {
            var empty = KeyStore.getInstance("PKCS12");
            empty.load(null, null);
            managers.init(empty);
        }
        var context = SSLContext.getInstance("TLSv1.3");
        context.init(null, managers.getTrustManagers(), null);
        return context;
    }
}
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 87–90 | Сървърът прочита паролата, създава фабрика за ключови мениджъри и я инициализира с `server.p12`. |
| 91–93 | Създайте TLS 1.3 контекст за представяне на сървърния сертификат и върнете този контекст. |
| 95–97 | Клиентът създава фабрика за доверие. За нормална връзка зарежда локалното `trust.p12`. |
| 98–101 | Отрицателната проверка инициализира празно хранилище. Така същият валиден сървърен сертификат е недоверен за този тестов клиент. |
| 103–105 | Клиентският контекст използва получените мениджъри за доверие. Удостоверяването на човека ще стане с AUTH след TLS. |

[ChatServer.java · редове 33–38](../chat-solution/src/labnet/ChatServer.java#L33-L38)

```java
    public ChatServer(int port, int udpPort) throws Exception {
        keys.prepare();
        for (int n = 1; n <= 16; n++) keys.provision("bench" + n);
        tls = keys.serverContext();
        room = new Room(keys.tokens());
        selector = Selector.open();
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 33–34 | При създаване на единствения сървър подгответе учебните ключове. |
| 35 | При добавяне на упражнение 7 осигурете акаунти `bench1`…`bench16` преди зареждане на картата. |
| 36–37 | Заредете сървърния TLS контекст и подайте токените на една обща стая. |
| 38 | Едва с готовия контекст продължете към отваряне на селектора. |

[Room.java · редове 29–32](../chat-solution/src/labnet/Room.java#L29-L32)

```java
    public Room(Map<String, byte[]> tokens) {
        this.tokens = new ConcurrentHashMap<>(tokens);
    }
    public void provision(String name, byte[] token) { tokens.put(name, token); }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 29–30 | В конструктора направете конкурентна карта от началните токени. |
| 32 | При добавяне на участник запишете очакваната стойност за новото име. |

[ChatServer.java · редове 112–118](../chat-solution/src/labnet/ChatServer.java#L112-L118)

```java
    public String console(String line) throws Exception {
        String[] words = line.split(" ", 2);
        if (line.equals("/stats")) return room.stats() + ";newConnections=" + model;
        if (line.equals("/help")) return "/stats, /user name, /io nio|thread|pool, /chunk 1..65536, /slow name|off, /drop-ack seq, /quit";
        if (words.length == 2) {
            switch (words[0]) {
                case "/user" -> { room.provision(words[1], keys.provision(words[1])); return "User ready: " + words[1]; }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 112–113 | Разделете командата от остатъка на реда. При сървърна публикация ще използвате оригиналния цял ред. |
| 114–115 | `/stats` показва и модела за нови връзки, а `/help` изброява сървърните команди. |
| 116–118 | За `/user maria` осигурете локален токен, въведете го в стаята и потвърдете готовността на името. |

## 2. Обвийте съществуващия TCP сокет и проверете името

В `ChatConnection` вече имате успешно свързан `plain`. Добавете следния блок преди да вземете входния и изходния поток. В основния клиент `ChatClient` винаги подава доверения TLS контекст.

[ChatConnection.java · редове 31–40](../chat-solution/src/labnet/blocking/ChatConnection.java#L31-L40)

```java
            if (tls != null) {
                SSLSocket secure = (SSLSocket) tls.getSocketFactory().createSocket(plain, host, port, true);
                connected = secure;
                SSLParameters policy = secure.getSSLParameters();
                policy.setEndpointIdentificationAlgorithm("HTTPS");
                secure.setSSLParameters(policy);
                secure.setSoTimeout(3000);
                secure.startHandshake();
                secure.setSoTimeout(0);
            }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 31–32 | Обвийте свързания сокет с клиентски `SSLSocket` за действителния хост. Последният аргумент обвързва затварянето и на основния сокет. |
| 33–34 | Оттук нататък обвивката е активната връзка. Получете параметрите ѝ за проверка. |
| 35 | `HTTPS` тук е името на алгоритъм за проверка на идентичността на хоста. Приложният протокол продължава да е LabNet. |
| 36 | Приложете параметрите преди началото на договарянето. |
| 37–38 | Задайте ограничение за блокиращо четене при договарянето и започнете TLS обмена. |
| 39 | Върнете неограничено изчакване за обичайното четене на клиента, за да може той спокойно да слуша чужди публикации. |

[BlockingSession.java · редове 10–36](../chat-solution/src/labnet/transport/BlockingSession.java#L10-L36)

```java
public final class BlockingSession extends Session implements Runnable {
    private final SSLSocket socket;
    private final ScheduledExecutorService timers;
    public BlockingSession(Socket accepted, SSLContext context, Room room, Consumer<Session> removed,
                           ScheduledExecutorService timers, Semaphore handshakes) throws IOException {
        super(room, removed, handshakes);
        this.timers = timers;
        socket = (SSLSocket) context.getSocketFactory().createSocket(accepted, "localhost", accepted.getPort(), true);
        socket.setUseClientMode(false);
        socket.setEnabledProtocols(new String[]{"TLSv1.3"});
    }
    public void run() {
        try {
            var deadline = timers.schedule(this::close, 3, TimeUnit.SECONDS);
            try { socket.startHandshake(); } finally { deadline.cancel(false); }
            handshakeDone();
            Thread.ofVirtual().name("tls-writer").start(this::write);
            var input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            while (!closed && !quitting) {
                int version = input.readUnsignedByte();
                var frameDeadline = timers.schedule(this::close, 2, TimeUnit.SECONDS);
                Frame frame;
                try { frame = Frame.read(version, input); } finally { frameDeadline.cancel(false); }
                dispatch(frame);
            }
            // The writer closes the connection after the final QUIT response.
        } catch (IOException | RuntimeException failure) { close(); }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 10–12 | Блокиращата сесия може да бъде подадена на нишка като `Runnable`. Съхранява TLS сокет и изпълнител за таймерите. |
| 13–16 | Приемете общите ресурси, предайте ги към `Session` и запазете таймерите. |
| 17 | Обвийте вече приетия сокет и свържете затварянето му с обвивката. |
| 18–19 | Изберете сървърната роля и TLS 1.3. |
| 21–24 | В обработващата нишка планирайте затваряне след три секунди. Отменете таймера при излизане от `startHandshake`. |
| 25 | Освободете общото разрешение за договаряне след успех. |
| 26–27 | Стартирайте един писател за тази връзка и подгответе блокиращия четец на кадри. |
| 28–30 | Докато участникът е активен, изчакайте първия байт. Едва след него започнете двусекундния срок за довършване на кадъра. |
| 31–33 | Подайте прочетената версия към общия декодер, отменете таймера и предайте готовия кадър на `dispatch`. |
| 35–36 | При нормален QUIT завършването на отговора ще затвори връзката. При грешка в четенето или договарянето я затворете тук. |

[BlockingSession.java · редове 38–51](../chat-solution/src/labnet/transport/BlockingSession.java#L38-L51)

```java
    private void write() {
        try {
            var output = socket.getOutputStream();
            for (Write item; (item = awaitHead()) != null;) {
                var deadline = timers.schedule(this::close, 2, TimeUnit.SECONDS);
                try { output.write(item.bytes().array()); }
                finally { deadline.cancel(false); }
                written(item);
            }
        } catch (IOException | InterruptedException | RuntimeException failure) { close(); }
    }
    protected void wakeWriter() { /* awaitHead uses the session monitor */ }
    protected void closeTransport() throws IOException { socket.close(); }
}
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 38–41 | Вземете изходния поток и изчакайте достъпен елемент от общата опашка. |
| 42–44 | Планирайте затваряне при блокирал запис над две секунди. Изпратете целия кодиран масив и отменете таймера. |
| 45 | Обявете успеха на конкретния кадър след завършилото `write`. |
| 47 | При I/O грешка, прекъсната нишка или неочаквана грешка затворете сесията и приключете чакащите записи. |
| 49 | Блокиращият писател се събужда от `notifyAll` на опашката, затова тук не се изисква отделен сигнал. |
| 50 | Затворете TLS сокета на този участник. |

## 3. Добавете TLS към NIO сесията

Селекторът от упражнение 4 получава шифрованите записи. `SSLEngine.unwrap` извлича от тях приложните байтове за вече построения декодер. В другата посока `wrap` превръща част от LabNet кадъра в TLS запис.

[SelectorSession.java · редове 61–79](../chat-solution/src/labnet/transport/SelectorSession.java#L61-L79)

```java
    private boolean unwrap() throws IOException {
        netIn.flip();
        var result = engine.unwrap(netIn, plain);
        netIn.compact();
        switch (result.getStatus()) {
            case CLOSED -> close();
            case BUFFER_OVERFLOW -> throw new IOException("TLS application buffer full");
            case BUFFER_UNDERFLOW -> { if (!netIn.hasRemaining()) throw new IOException("TLS record too large"); return false; }
            case OK -> { if (result.bytesProduced() > 0) decode(); }
        }
        return result.bytesConsumed() > 0 || result.bytesProduced() > 0
                || result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED;
    }
    private void wrap(ByteBuffer source) throws IOException {
        netOut.clear();
        var result = engine.wrap(source, netOut);
        netOut.flip();
        if (result.getStatus() != SSLEngineResult.Status.OK) throw new IOException("TLS wrap: " + result.getStatus());
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 61–63 | Превключете мрежовия вход за четене. `unwrap(netIn, plain)` консумира наличните TLS байтове и добавя разчетеното в приложния буфер. |
| 64 | Съберете неизползваните мрежови байтове и подгответе място за следващо четене. |
| 65–66 | Проверете статуса. При `CLOSED` затворете сесията. |
| 67 | `BUFFER_OVERFLOW` означава недостатъчно място в приложния буфер. Този учебен транспорт прекратява връзката с грешка. |
| 68 | При `BUFFER_UNDERFLOW` е необходим още мрежов вход. Ако целият входен буфер вече е запълнен, откажете прекалено големия запис. |
| 69 | При нормален резултат задвижете LabNet декодера само ако са произведени приложни байтове. |
| 71–72 | Върнете дали има напредък по данните или договарянето. Цикълът решава дали да продължи веднага. |
| 74–75 | За изходящ TLS запис подгответе празен буфер за запис. |
| 76–77 | Шифровайте следващата част от източника и превключете изходния буфер за четене от `SocketChannel`. |
| 78 | Неочакван статус за запис води до грешка за сесията. |

[SelectorSession.java · редове 101–124](../chat-solution/src/labnet/transport/SelectorSession.java#L101-L124)

```java
            switch (engine.getHandshakeStatus()) {
                case NEED_TASK -> {
                    Runnable task = engine.getDelegatedTask();
                    if (task == null) throw new IOException("Missing TLS task");
                    doingTask = true;
                    try {
                        handshakeTasks.execute(() -> {
                            try { task.run(); } finally { doingTask = false; wakeWriter(); }
                        });
                    } catch (RejectedExecutionException busy) { close(); }
                }
                case NEED_WRAP -> wrap(EMPTY.duplicate());
                case NEED_UNWRAP, NEED_UNWRAP_AGAIN -> { if (!unwrap()) { interests(); return; } }
                case FINISHED, NOT_HANDSHAKING -> {
                    if (!established) { established = true; handshakeDone(); }
                    if (!quitting && !readPaused && netIn.position() > 0 && unwrap()) continue;
                    Write item = head();
                    if (item == null) { interests(); return; }
                    wrap(item.bytes());
                    if (!item.bytes().hasRemaining()) flushed = item;
                }
            }
        }
        interests();
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 101–103 | Попитайте TLS машината каква стъпка изисква. При `NEED_TASK` вземете делегираната задача. |
| 104–105 | Проверете дали задачата е действително налична и маркирайте машината като заета. |
| 107–110 | Изпълнете я в ограничения пул за договаряне. При край възстановете достъпа до машината и събудете селектора. При отказ от пула затворете сесията. |
| 112 | При `NEED_WRAP` подайте празен приложен буфер, защото TLS има собствени данни за изпращане. |
| 113 | При `NEED_UNWRAP` или `NEED_UNWRAP_AGAIN` извикайте `unwrap`. При нужда от още байтове обновете интересите и върнете управлението. |
| 114–115 | При установено договаряне маркирайте връзката и еднократно освободете разрешението. |
| 116 | Ако вече има входни байтове и четенето е разрешено, опитайте да ги обработите. |
| 117–118 | Вземете първия готов изходящ кадър. При празна или задържана опашка върнете управлението. |
| 119–120 | Шифровайте наличните приложни байтове. Когато те се изчерпат, помнете записа във `flushed` до действителното изписване на `netOut` в упражнение 4. |
| 124 | Преизчислете интереса към четене и писане. |

## 4. Удостоверете участника преди да заемете име

В `ChatClient.open` подайте тялото `Named(name, token).pack()` към AUTH и изчакайте `OK`. Едва тогава подайте `LOGIN`. На сървъра добавете следния метод и го извикайте от `Room.handle` на ред 90. [Последователността в ChatClient.java](../chat-solution/src/labnet/ChatClient.java#L35-L45)

[Room.java · редове 120–134](../chat-solution/src/labnet/Room.java#L120-L134)

```java
    private CompletableFuture<Boolean> authenticate(Peer peer, long id, byte[] body) throws IOException {
        if (peer.principal != null) return error(peer, id, "ALREADY_AUTHENTICATED");
        Named credential = Named.unpack(body);
        byte[] token = credential.value();
        if (token.length < 16 || token.length > 128) return error(peer, id, "BAD_PAYLOAD");
        text(token);
        byte[] expected = tokens.get(credential.name());
        if (expected == null || !MessageDigest.isEqual(expected, token)) {
            var result = error(peer, id, "AUTH_FAILED");
            if (++peer.authFailures >= 3) result.thenRun(peer::close);
            return result;
        }
        peer.principal = credential.name();
        return peer.send(new Frame(OK, id, EMPTY));
    }
```

| Ред | Какво означава в тази стъпка |
|---|---|
| 120–121 | При повторно AUTH за вече установена самоличност върнете `ALREADY_AUTHENTICATED`. |
| 122–123 | Разопаковайте заявеното име и байтовете на токена. |
| 124–125 | Изисквайте от 16 до 128 байта и валидно UTF-8 кодиране. |
| 126 | Намерете очакваните байтове по заявеното име. |
| 127–128 | При непознато име или несъвпадение по `MessageDigest.isEqual` върнете общия код `AUTH_FAILED`. |
| 129 | След трети неуспех по тази връзка насрочете затварянето ѝ след завършване на отговора. |
| 130 | Върнете резултата от записа на грешката. |
| 132–133 | Запомнете удостовереното име в сесията и върнете `OK`. При `LOGIN` ред 50 го сравнява с исканото име в стаята. |

**Проверка.** В терминал от `chat-solution` изпълнете `java -cp out labnet.demo.Demo 6`. Тримата удостоверени участници трябва да получат съобщението „Защитен чат“. Клиент с грешен токен получава `AUTH_FAILED`. При празно хранилище на доверие и при проверка за друго име тестът отчита отказ още при TLS договарянето. Очаквайте `PASS lab06`. Учебната настройка съхранява ключовете и токените локално в `.local/tls`.

[Съдържание на ръководството](../hint.md) · [Следващо упражнение — Измерване на доставката](../lab07-performance/hint.md)
