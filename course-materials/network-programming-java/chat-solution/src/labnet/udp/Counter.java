package labnet.udp;

import labnet.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public final class Counter {
    private static final int HEADER = 19, DATA = 1, ACK = 2, ERROR = 3;
    private record Packet(long session, long seq, int type, byte[] body) {
        byte[] encode() {
            return ByteBuffer.allocate(HEADER + body.length).putLong(session).putLong(seq)
                    .put((byte) type).putShort((short) body.length).put(body).array();
        }
        static Packet decode(DatagramPacket datagram) {
            if (datagram.getLength() < HEADER || datagram.getLength() > 1200) return null;
            var buffer = ByteBuffer.wrap(datagram.getData(), datagram.getOffset(), datagram.getLength());
            long session = buffer.getLong(), seq = buffer.getLong();
            int type = buffer.get() & 255, size = Short.toUnsignedInt(buffer.getShort());
            if (session < 1 || seq < 1 || size != buffer.remaining()) return null;
            byte[] body = new byte[size];
            buffer.get(body);
            return new Packet(session, seq, type, body);
        }
    }
    private static byte[] number(long value) { return ByteBuffer.allocate(8).putLong(value).array(); }

    public static final class Receiver implements AutoCloseable {
        private record Key(SocketAddress source, long session) {}
        private record State(long seq, long total, long lastSeen) {}
        private final Map<Key, State> sessions = new HashMap<>();
        private final DatagramSocket socket;
        private final Thread thread;
        private volatile int dropAck;
        public void dropAck(int seq) { dropAck = seq; }
        public Receiver(int port, int dropAck) throws IOException {
            this.dropAck = dropAck;
            socket = new DatagramSocket(new InetSocketAddress(InetAddress.getLoopbackAddress(), port));
            thread = Thread.ofPlatform().name("udp-counter").start(this::run);
        }
        public int port() { return socket.getLocalPort(); }
        private void run() {
            byte[] buffer = new byte[1201];
            try {
                while (!socket.isClosed()) {
                    var datagram = new DatagramPacket(buffer, buffer.length);
                    socket.receive(datagram);
                    var packet = Packet.decode(datagram);
                    if (packet == null || packet.type != DATA || packet.body.length != 8) continue;
                    long delta = ByteBuffer.wrap(packet.body).getLong();
                    if (delta < -100 || delta > 100) continue;
                    long now = System.nanoTime();
                    sessions.values().removeIf(state -> now - state.lastSeen > TimeUnit.SECONDS.toNanos(60));
                    var key = new Key(datagram.getSocketAddress(), packet.session);
                    State previous = sessions.get(key);
                    String error = null;
                    if (previous == null && packet.seq != 1) error = "SESSION_EXPIRED";
                    else if (previous == null && sessions.size() >= 32) error = "SERVER_BUSY";
                    else if (previous != null && packet.seq < previous.seq) continue;
                    else if (previous != null && packet.seq > previous.seq + 1) error = "BAD_SEQUENCE";
                    Packet reply;
                    if (error != null) reply = new Packet(packet.session, packet.seq, ERROR, Frame.bytes(error));
                    else {
                        boolean fresh = previous == null || packet.seq != previous.seq;
                        long total = previous == null ? 0 : previous.total;
                        if (fresh) total = Math.addExact(total, delta);
                        sessions.put(key, new State(packet.seq, total, now));
                        if (fresh && packet.seq == dropAck) continue;
                        reply = new Packet(packet.session, packet.seq, ACK, number(total));
                    }
                    byte[] data = reply.encode();
                    socket.send(new DatagramPacket(data, data.length, key.source));
                }
            } catch (IOException ex) { if (!socket.isClosed()) throw new UncheckedIOException(ex); }
        }
        public void close() throws InterruptedException { socket.close(); thread.join(); }
    }

    public record Receipt(long seq, long total, int attempts) {}
    public static final class Sender implements AutoCloseable {
        private final DatagramSocket socket;
        private final long session = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        private long seq;
        public Sender(String host, int port) throws IOException {
            socket = new DatagramSocket();
            socket.connect(InetAddress.getByName(host), port);
        }
        public synchronized Receipt increment() throws IOException {
            byte[] data = new Packet(session, ++seq, DATA, number(1)).encode();
            for (int attempt = 1; attempt <= 5; attempt++) {
                socket.send(new DatagramPacket(data, data.length));
                int delay = Math.min(1600, 200 << (attempt - 1));
                long until = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(delay);
                for (long remaining; (remaining = until - System.nanoTime()) > 0;) {
                    socket.setSoTimeout((int) TimeUnit.NANOSECONDS.toMillis(remaining) + 1);
                    var datagram = new DatagramPacket(new byte[1201], 1201);
                    try { socket.receive(datagram); } catch (SocketTimeoutException timeout) { break; }
                    var packet = Packet.decode(datagram);
                    if (packet == null || packet.session != session || packet.seq != seq) continue;
                    if (packet.type == ERROR) throw new IOException(Frame.text(packet.body));
                    if (packet.type == ACK && packet.body.length == 8)
                        return new Receipt(seq, ByteBuffer.wrap(packet.body).getLong(), attempt);
                }
            }
            close();
            throw new IOException("UNKNOWN_OUTCOME seq=" + seq);
        }
        public void close() { socket.close(); }
    }

    // One worker keeps UDP retry delays off the chat reader thread.
    public static final class Monitor implements AutoCloseable {
        private final Sender sender;
        private final Consumer<Receipt> report;
        private final ThreadPoolExecutor worker = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(64));
        public Monitor(String host, int port, Consumer<Receipt> report) throws IOException {
            sender = new Sender(host, port);
            this.report = report;
        }
        public void displayed() {
            try {
                worker.execute(() -> {
                    try { report.accept(sender.increment()); }
                    catch (IOException ex) { System.err.println("UDP: " + ex.getMessage()); }
                });
            } catch (RejectedExecutionException full) { System.err.println("UDP sample rejected"); }
        }
        public void close() { worker.close(); sender.close(); }
    }

}
