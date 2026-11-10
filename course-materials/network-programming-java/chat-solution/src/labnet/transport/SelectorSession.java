package labnet.transport;

import labnet.*;
import javax.net.ssl.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/** All engine and buffer operations belong to the selector thread, except delegated handshake tasks. */
public final class SelectorSession extends Session {
    private static final ByteBuffer EMPTY = ByteBuffer.allocate(0);
    private final SocketChannel channel;
    private final SelectionKey key;
    private final SSLEngine engine;
    private final Executor handshakeTasks;
    private final ByteBuffer netIn, netOut, plain;
    private volatile boolean doingTask;
    private boolean established;
    private final long handshakeDeadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
    private long frameStarted;
    private Write flushed;
    private boolean readPaused;

    public SelectorSession(SocketChannel channel, Selector selector, SSLContext tls, Room room,
                           Consumer<Session> removed, Executor handshakeTasks, Semaphore handshakes) throws IOException {
        super(room, removed, handshakes);
        this.channel = channel;
        this.handshakeTasks = handshakeTasks;
        engine = tls.createSSLEngine();
        engine.setUseClientMode(false);
        engine.setEnabledProtocols(new String[]{"TLSv1.3"});
        engine.beginHandshake();
        int packet = engine.getSession().getPacketBufferSize();
        netIn = ByteBuffer.allocate(2 * packet);
        netOut = ByteBuffer.allocate(2 * packet);
        netOut.flip();
        plain = ByteBuffer.allocate(Frame.HEADER + Frame.MAX_PAYLOAD + engine.getSession().getApplicationBufferSize());
        key = channel.register(selector, SelectionKey.OP_READ, this);
    }
    public void read() throws IOException {
        if (!doingTask && !quitting && !readPaused && channel.read(netIn) < 0) close();
    }
    private void decode() throws IOException {
        plain.flip();
        while (!closed && !quitting && plain.remaining() >= Frame.HEADER) {
            plain.mark();
            int version = plain.get() & 255, type = plain.get() & 255;
            int size = Frame.length(version, plain.getInt());
            if (plain.remaining() < size) { plain.reset(); break; }
            long id = plain.getLong();
            byte[] body = new byte[size - Long.BYTES];
            plain.get(body);
            dispatch(new Frame(type, id, body));
            frameStarted = 0;
        }
        plain.compact();
        if (plain.position() > 0 && frameStarted == 0) frameStarted = System.nanoTime();
    }
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
    public void pump(int chunk) throws IOException {
        if (closed) return;
        if (queuedBytes() >= 128 * 1024) readPaused = true;
        else if (queuedBytes() <= 64 * 1024) readPaused = false;
        long now = System.nanoTime();
        if ((!established && now > handshakeDeadline) ||
                (frameStarted != 0 && now - frameStarted > TimeUnit.SECONDS.toNanos(2))) { close(); return; }
        for (int turn = 0; turn < 64 && !closed && !doingTask; turn++) {
            if (netOut.hasRemaining()) {
                int limit = netOut.limit();
                if (established) netOut.limit(netOut.position() + Math.min(chunk, netOut.remaining()));
                channel.write(netOut);
                netOut.limit(limit);
                if (netOut.hasRemaining()) break;
            }
            if (flushed != null) {
                Write complete = flushed;
                flushed = null;
                written(complete);
                if (closed) break;
            }
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
    }
    private void interests() {
        if (closed || !key.isValid()) return;
        int ops = !doingTask && !quitting && !readPaused ? SelectionKey.OP_READ : 0;
        if (!doingTask && (netOut.hasRemaining() || (established && head() != null))) ops |= SelectionKey.OP_WRITE;
        key.interestOps(ops);
    }
    protected void wakeWriter() { key.selector().wakeup(); }
    protected void closeTransport() throws IOException { key.cancel(); channel.close(); }
}
