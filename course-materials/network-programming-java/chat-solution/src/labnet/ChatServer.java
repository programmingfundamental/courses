package labnet;

import labnet.tls.DemoKeys;
import labnet.transport.*;
import labnet.udp.Counter;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

public final class ChatServer implements AutoCloseable {
    public enum Model { NIO, THREAD, POOL }

    private final DemoKeys keys = new DemoKeys();
    private final Room room;
    private final SSLContext tls;
    private final Selector selector;
    private final ServerSocketChannel listener;
    private final Counter.Receiver counter;
    private final Set<Session> sessions = ConcurrentHashMap.newKeySet();
    private final ExecutorService threads = Executors.newThreadPerTaskExecutor(Thread.ofPlatform().factory());
    private final Semaphore handshakes = new Semaphore(4);
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(16, 16, 0, TimeUnit.SECONDS, new SynchronousQueue<>());
    private final ThreadPoolExecutor handshakeTasks = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(16));
    private final ScheduledThreadPoolExecutor timers = new ScheduledThreadPoolExecutor(1);
    private final Thread loop;
    private volatile Model model = Model.NIO;
    private volatile int chunk = 65536;
    private volatile boolean running = true;

    public ChatServer(int port, int udpPort) throws Exception {
        keys.prepare();
        for (int n = 1; n <= 16; n++) keys.provision("bench" + n);
        tls = keys.serverContext();
        room = new Room(keys.tokens());
        selector = Selector.open();
        listener = ServerSocketChannel.open();
        try {
            counter = new Counter.Receiver(udpPort, 0);
        } catch (IOException failure) {
            listener.close(); selector.close(); room.close();
            throw failure;
        }
        try {
            listener.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), port));
            listener.configureBlocking(false);
            listener.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception failure) {
            counter.close(); listener.close(); selector.close(); room.close();
            throw failure;
        }
        timers.setRemoveOnCancelPolicy(true);
        loop = Thread.ofPlatform().name("chat-selector").start(this::run);
    }
    public int port() throws IOException { return ((InetSocketAddress) listener.getLocalAddress()).getPort(); }
    public int udpPort() { return counter.port(); }

    private void accept() throws IOException {
        SocketChannel channel = listener.accept();
        if (channel == null) return;
        if (sessions.size() >= 64 || !handshakes.tryAcquire()) { room.rejected(); channel.close(); return; }
        Session session = null;
        try {
            channel.socket().setTcpNoDelay(true);
            Model selected = model;
            if (selected == Model.NIO) {
                channel.configureBlocking(false);
                session = new SelectorSession(channel, selector, tls, room, sessions::remove, handshakeTasks, handshakes);
            } else {
                channel.configureBlocking(true);
                session = new BlockingSession(channel.socket(), tls, room, sessions::remove, timers, handshakes);
            }
            session.model = selected.name();
            room.enter(session);
            sessions.add(session);
            if (session instanceof BlockingSession blocking)
                (selected == Model.POOL ? pool : threads).execute(blocking);
        } catch (IOException | RuntimeException failure) {
            room.rejected();
            if (session != null) session.close(); else handshakes.release();
            channel.close();
        }
    }
    private void run() {
        try {
            while (running) {
                selector.select(150);
                var ready = selector.selectedKeys().iterator();
                while (ready.hasNext()) {
                    var key = ready.next();
                    ready.remove();
                    if (!key.isValid()) continue;
                    if (key.isAcceptable()) accept();
                    else if (key.isReadable()) {
                        var session = (SelectorSession) key.attachment();
                        try { session.read(); } catch (IOException end) { session.close(); }
                    }
                }
                for (Session peer : sessions) {
                    if (peer instanceof SelectorSession session) {
                        try { session.pump(chunk); } catch (IOException | CancelledKeyException end) { session.close(); }
                    }
                }
            }
        } catch (IOException failure) {
            if (running) throw new UncheckedIOException(failure);
        } finally { sessions.forEach(Session::close); }
    }

    public String console(String line) throws Exception {
        String[] words = line.split(" ", 2);
        if (line.equals("/stats")) return room.stats() + ";newConnections=" + model;
        if (line.equals("/help")) return "/stats, /user name, /io nio|thread|pool, /chunk 1..65536, /slow name|off, /drop-ack seq, /quit";
        if (words.length == 2) {
            switch (words[0]) {
                case "/user" -> { room.provision(words[1], keys.provision(words[1])); return "User ready: " + words[1]; }
                case "/io" -> { model = Model.valueOf(words[1].toUpperCase(Locale.ROOT)); return "New connections: " + model; }
                case "/chunk" -> {
                    int value = Integer.parseInt(words[1]);
                    if (value < 1 || value > 65536) throw new IllegalArgumentException("Chunk must be 1..65536");
                    chunk = value; return "NIO socket write limit=" + chunk;
                }
                case "/slow" -> {
                    boolean found = false;
                    for (Session peer : sessions) {
                        boolean match = words[1].equals(peer.name);
                        peer.slow(match);
                        found |= match;
                    }
                    return found || words[1].equals("off") ? "Slow: " + words[1] : "NO_SUCH_USER";
                }
                case "/drop-ack" -> {
                    int seq = Integer.parseInt(words[1]);
                    if (seq < 0) throw new IllegalArgumentException("SEQ must be >= 0");
                    counter.dropAck(seq); return "Drop first UDP ACK at seq=" + seq;
                }
            }
        }
        if (line.startsWith("/")) return "Unknown command. Use /help";
        return room.publish("server", Frame.bytes(line)).get(8, TimeUnit.SECONDS);
    }
    public void close() throws Exception {
        running = false; selector.wakeup(); loop.join();
        listener.close(); selector.close(); counter.close();
        threads.close(); pool.close(); handshakeTasks.close(); timers.shutdownNow(); room.close();
    }
    public static void main(String[] args) throws Exception {
        var opts = new Options(args);
        try (var server = new ChatServer(opts.number("port", 9000, 0, 65535), opts.number("udp-port", 9001, 0, 65535))) {
            ConsoleIO.line("ChatServer ready | TLS localhost:" + server.port() + " | UDP " + server.udpPort() + " | /help");
            var in = ConsoleIO.input();
            for (String line; (line = in.readLine()) != null && !line.equals("/quit");) {
                try { ConsoleIO.line(server.console(line)); }
                catch (Exception failure) { ConsoleIO.line("Error: " + failure.getMessage()); }
            }
        }
    }
}
