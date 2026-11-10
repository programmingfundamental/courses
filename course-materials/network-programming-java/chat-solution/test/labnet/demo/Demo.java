package labnet.demo;

import labnet.*;
import labnet.blocking.*;
import labnet.tls.DemoKeys;
import labnet.udp.Counter;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import static labnet.Frame.*;

public final class Demo {
    private static final DemoKeys keys = new DemoKeys();
    private static SSLContext tls;
    private static void check(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
    }
    private static void reply(Frame frame, int type, String body) throws IOException {
        check(frame.type() == type && Arrays.equals(frame.body(), bytes(body)), "Unexpected response #" + frame.id() + ": " + text(frame.body()));
    }
    private static final class Participant implements AutoCloseable {
        final String name;
        final ChatConnection client;
        final BlockingQueue<ChatConnection.Message> inbox = new LinkedBlockingQueue<>();
        Participant(String name, int port, SSLContext tls, Counter.Monitor monitor) throws IOException {
            this.name = name;
            client = new ChatConnection("localhost", port, tls, message -> {
                inbox.add(message);
                if (monitor != null) monitor.displayed();
            });
        }
        void login() throws Exception {
            reply(client.request(AUTH, new Named(name, keys.token(name)).pack()), OK, "");
            reply(client.request(LOGIN, bytes(name)), OK, "");
        }
        void expect(String author, String content) throws Exception {
            var wanted = new ChatConnection.Message(author, content);
            check(wanted.equals(inbox.poll(3, TimeUnit.SECONDS)), name + " missing " + author + ": " + content);
        }
        void barrier() throws Exception {
            reply(client.request(PING, EMPTY), OK, "");
            check(inbox.isEmpty(), name + ": unexpected event " + inbox.peek());
        }
        public void close() { client.close(); }
    }

    private static void stage1(ChatServer server) throws Exception {
        try (var socket = (SSLSocket) tls.getSocketFactory().createSocket("localhost", server.port())) {
            socket.setSoTimeout(3000);
            var out = socket.getOutputStream();
            var in = new DataInputStream(socket.getInputStream());
            out.write(new Frame(AUTH, 1, new Named("ana", keys.token("ana")).pack()).encode().array());
            reply(Frame.read(in), OK, "");
            byte[] fragmented = new Frame(ECHO, 2, bytes("Чат")).encode().array();
            // Split after two header bytes. Also test the decoder with forced one-byte reads.
            out.write(fragmented, 0, 2);
            out.write(fragmented, 2, fragmented.length - 2);
            var shortReads = new ByteArrayInputStream(fragmented) {
                public synchronized int read(byte[] b, int off, int len) { return super.read(b, off, Math.min(1, len)); }
            };
            Frame decoded = Frame.read(new DataInputStream(shortReads));
            check(decoded.id() == 2 && text(decoded.body()).equals("Чат"), "One-byte reads");
            var pair = new ByteArrayOutputStream();
            pair.write(new Frame(ECHO, 3, bytes("Б")).encode().array());
            pair.write(new Frame(ECHO, 4, bytes("В")).encode().array());
            out.write(pair.toByteArray());
            int id = 1;
            for (String expected : List.of("Чат", "Б", "В")) {
                var frame = Frame.read(in);
                check(frame.id() == ++id, "Wrong echo ID");
                reply(frame, DATA, expected);
                ConsoleIO.line("ECHO #" + id + " -> " + expected);
            }
        }
    }

    private static void chat(ChatServer server) throws Exception {
        int port = server.port();
        try (var ana = new Participant("ana", port, tls, null);
             var boris = new Participant("boris", port, tls, null);
             var iva = new Participant("iva", port, tls, null);
             var visitor = new Participant("ana", port, tls, null)) {
            var members = List.of(ana, boris, iva);
            for (var participant : members) participant.login();
            reply(visitor.client.request(AUTH, new Named("ana", keys.token("ana")).pack()), OK, "");
            reply(visitor.client.request(LOGIN, bytes("ana")), ERROR, "NAME_IN_USE");
            reply(visitor.client.request(BROADCAST, bytes("Hi")), ERROR, "NOT_LOGGED_IN");
            reply(ana.client.request(BROADCAST, bytes("Здравейте!")), DATA, "delivered=3;failed=0");
            for (var member : members) {
                member.expect("ana", "Здравейте!");
                ConsoleIO.line(member.name + " получи [ana] Здравейте!");
            }
            check("delivered=3;failed=0".equals(server.console("Занятието започва.")), "Console receipt");
            for (var member : members) {
                member.expect("server", "Занятието започва.");
                ConsoleIO.line(member.name + " получи [server] Занятието започва.");
            }
            String boundary = "я".repeat(2048);
            reply(boris.client.request(BROADCAST, bytes(boundary)), DATA, "delivered=3;failed=0");
            for (var member : members) member.expect("boris", boundary);
            for (byte[] invalid : List.of(EMPTY, bytes(boundary + "!"), new byte[]{(byte) 0xff}))
                reply(boris.client.request(BROADCAST, invalid), ERROR, "BAD_PAYLOAD");
            try (var writers = Executors.newFixedThreadPool(2)) {
                var jobs = new ArrayList<Future<Void>>();
                var start = new CyclicBarrier(2);
                for (var sender : List.of(ana, boris)) jobs.add(writers.submit(() -> {
                    start.await(3, TimeUnit.SECONDS);
                    for (int n = 1; n <= 10; n++)
                        reply(sender.client.request(BROADCAST, bytes("ред " + n)), DATA, "delivered=3;failed=0");
                    return null;
                }));
                for (var job : jobs) job.get(15, TimeUnit.SECONDS);
            }
            for (var member : members) {
                Set<ChatConnection.Message> expected = new HashSet<>();
                for (String author : List.of("ana", "boris"))
                    for (int n = 1; n <= 10; n++) expected.add(new ChatConnection.Message(author, "ред " + n));
                for (int n = 0; n < 20; n++) check(expected.remove(member.inbox.poll(3, TimeUnit.SECONDS)), "Duplicate or missing event");
                member.barrier();
            }
            visitor.barrier();
            reply(iva.client.request(QUIT, EMPTY), OK, "");
            reply(ana.client.request(USERS, EMPTY), DATA, "ana\nboris");
            reply(ana.client.request(BROADCAST, bytes("До утре!")), DATA, "delivered=2;failed=0");
            ana.expect("ana", "До утре!"); boris.expect("ana", "До утре!");
            try (var rejoined = new Participant("iva", port, tls, null)) {
                rejoined.login();
                reply(boris.client.request(BROADCAST, bytes("Добре дошла!")), DATA, "delivered=3;failed=0");
                rejoined.expect("boris", "Добре дошла!");
                rejoined.barrier();
            }
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
            while (!text(ana.client.request(USERS, EMPTY).body()).equals("ana\nboris")) {
                check(System.nanoTime() < deadline, "Abrupt disconnect cleanup");
                Thread.sleep(10);
            }
            try (var after = new Participant("iva", port, tls, null)) { after.login(); }
        }
    }

    private static void stage3(ChatServer server) throws Exception {
        var receipts = new LinkedBlockingQueue<Counter.Receipt>();
        server.console("/drop-ack 3");
        try (var monitor = new Counter.Monitor("localhost", server.udpPort(), receipts::add);
             var ana = new Participant("ana", server.port(), tls, null);
             var boris = new Participant("boris", server.port(), tls, null);
             var iva = new Participant("iva", server.port(), tls, monitor)) {
            ana.login(); boris.login(); iva.login();
            for (int n = 1; n <= 3; n++) {
                String line = "съобщение " + n;
                reply(ana.client.request(BROADCAST, bytes(line)), DATA, "delivered=3;failed=0");
                for (var participant : List.of(ana, boris, iva)) participant.expect("ana", line);
                var ack = receipts.poll(3, TimeUnit.SECONDS);
                check(ack != null && ack.seq() == n && ack.total() == n && ack.attempts() == (n == 3 ? 2 : 1), "UDP ACK " + ack);
                System.out.println("iva UDP " + ack);
            }
        }
    }

    private static void mixedRoom(ChatServer server) throws Exception {
        List<Participant> people = new ArrayList<>();
        try {
            List<String> models = List.of("nio", "thread", "pool");
            for (int i = 0; i < models.size(); i++) {
                server.console("/io " + models.get(i));
                var person = new Participant(DemoKeys.NAMES.get(i), server.port(), tls, null);
                people.add(person);
                person.login();
            }
            var ana = people.getFirst();
            reply(ana.client.request(BROADCAST, bytes("Обща стая")), DATA, "delivered=3;failed=0");
            for (var person : people) person.expect("ana", "Обща стая");
            reply(ana.client.request(SEND, new Named("iva", bytes("Лично")).pack()), OK, "");
            people.getLast().expect("ana", "Лично");
            reply(ana.client.request(SEND, new Named("missing", bytes("Hi")).pack()), ERROR, "NO_SUCH_USER");
            for (int i = 0; i < models.size(); i++) {
                var person = people.get(i);
                var work = person.client.submit(WORK, new byte[]{0, 0, 0, 30, 65});
                reply(person.client.request(PING, EMPTY), OK, "");
                reply(work.get(3, TimeUnit.SECONDS), DATA, "A");
                reply(person.client.request(WORK, new byte[]{0, 0, 0, 51}), ERROR, "BAD_PAYLOAD");
                String stats = text(person.client.request(STATS, EMPTY).body());
                check(stats.contains("loggedIn=3;") && stats.endsWith("model=" + models.get(i).toUpperCase(Locale.ROOT)), stats);
                person.barrier();
            }
        } finally { people.forEach(Participant::close); }
    }

    private static void stage5(ChatServer server) throws Exception {
        try (var ana = new Participant("ana", server.port(), tls, null);
             var boris = new Participant("boris", server.port(), tls, null);
             var iva = new Participant("iva", server.port(), tls, null)) {
            ana.login(); boris.login(); iva.login();
            server.console("/slow iva");
            var replies = new ArrayList<CompletableFuture<Frame>>();
            for (int n = 1; n <= 3; n++) replies.add(ana.client.submit(BROADCAST, bytes("burst-" + n)));
            for (var result : replies) reply(result.get(3, TimeUnit.SECONDS), DATA, "delivered=2;failed=1");
            for (int n = 1; n <= 3; n++) {
                ana.expect("ana", "burst-" + n); boris.expect("ana", "burst-" + n);
            }
            iva.client.ended().get(3, TimeUnit.SECONDS);
            check(iva.inbox.isEmpty(), "Held client received a delivery");
            reply(ana.client.request(BROADCAST, bytes("Продължаваме")), DATA, "delivered=2;failed=0");
            ana.expect("ana", "Продължаваме"); boris.expect("ana", "Продължаваме");
            ana.barrier(); boris.barrier();
            System.out.println("iva removed at queue limit 2; 3 x delivered=2;failed=1, next delivered=2;failed=0");
        }
    }

    private static void stage6(ChatServer server) throws Exception {
        try (var ana = new Participant("ana", server.port(), tls, null);
             var boris = new Participant("boris", server.port(), tls, null);
             var iva = new Participant("iva", server.port(), tls, null);
             var wrong = new Participant("ana", server.port(), tls, null)) {
            var members = List.of(ana, boris, iva);
            for (var member : members) {
                reply(member.client.request(LOGIN, bytes(member.name)), ERROR, "AUTH_REQUIRED");
                member.login();
            }
            reply(wrong.client.request(AUTH, new Named("ana", bytes("incorrect-demo-token")).pack()), ERROR, "AUTH_FAILED");
            reply(wrong.client.request(LOGIN, bytes("ana")), ERROR, "AUTH_REQUIRED");
            reply(boris.client.request(BROADCAST, bytes("Защитен чат")), DATA, "delivered=3;failed=0");
            for (var member : members) { member.expect("boris", "Защитен чат"); member.barrier(); }
            wrong.barrier();
            try (var unexpected = new Participant("unknown", server.port(), keys.clientContext(false), null)) {
                throw new AssertionError("Untrusted server accepted");
            } catch (SSLException expected) { System.out.println("Untrusted TLS handshake rejected"); }
            try (var raw = new Socket("localhost", server.port());
                 var mismatch = (SSLSocket) tls.getSocketFactory().createSocket(raw, "wrong.invalid", server.port(), true)) {
                var policy = mismatch.getSSLParameters();
                policy.setEndpointIdentificationAlgorithm("HTTPS");
                mismatch.setSSLParameters(policy);
                mismatch.setSoTimeout(3000);
                mismatch.startHandshake();
                throw new AssertionError("Wrong server name accepted");
            } catch (SSLHandshakeException expected) { System.out.println("Wrong TLS hostname rejected"); }
        }
    }
    public static void main(String[] args) throws Exception {
        String target = args.length == 0 ? "all" : args[0];
        if (!target.matches("all|[1-7]")) throw new IllegalArgumentException("Demo all or 1..7");
        try (var server = new ChatServer(0, 0)) {
            tls = keys.clientContext(true);
            for (int stage = 1; stage <= 7; stage++) {
                if (!target.equals("all") && stage != Integer.parseInt(target)) continue;
                ConsoleIO.line("\n" + List.of("1. TCP кадри и ECHO", "2. Стая с трима участници", "3. UDP брояч с изгубен ACK",
                        "4. NIO с частични записи по 3 байта", "5. Изходяща опашка за бавен клиент",
                        "6. TLS, AUTH и LOGIN", "7. Три модела в един сървър").get(stage - 1));
                switch (stage) {
                    case 1 -> stage1(server);
                    case 2 -> { server.console("/io thread"); chat(server); awaitCleanup(server); mixedRoom(server); }
                    case 3 -> stage3(server);
                    case 4 -> { server.console("/io nio"); server.console("/chunk 3"); chat(server); server.console("/chunk 65536"); }
                    case 5 -> stage5(server);
                    case 6 -> stage6(server);
                    case 7 -> {
                        for (String model : List.of("thread", "pool", "nio")) {
                            server.console("/io " + model);
                            Benchmark.measure("localhost", server.port(), tls, keys, 3, 10, 30, Path.of("results/chat.csv"));
                            awaitCleanup(server);
                        }
                    }
                }
                awaitCleanup(server);
                System.out.println("PASS lab0" + stage);
            }
        }
    }
    private static void awaitCleanup(ChatServer server) throws Exception {
        long until = System.nanoTime() + TimeUnit.SECONDS.toNanos(4);
        while (!server.console("/stats").startsWith("active=0;loggedIn=0;")) {
            check(System.nanoTime() < until, "Connections did not close");
            Thread.sleep(10);
        }
    }
}
