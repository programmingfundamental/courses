package labnet.transport;

import labnet.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/** Each connection has one writer. Completion means the encrypted record was written to the socket. */
public abstract class Session extends Room.Peer {
    public record Write(ByteBuffer bytes, int size, boolean event, CompletableFuture<Boolean> done) {}
    private final ArrayDeque<Write> queue = new ArrayDeque<>();
    private final Room room;
    private final Consumer<Session> removed;
    private final AtomicInteger inFlight = new AtomicInteger();
    private final AtomicBoolean handshaking = new AtomicBoolean(true);
    private final Semaphore handshakes;
    private int queuedBytes;
    private long lastId;
    private boolean slow;
    protected volatile boolean closed, quitting;

    protected Session(Room room, Consumer<Session> removed, Semaphore handshakes) {
        this.room = room; this.removed = removed; this.handshakes = handshakes;
    }
    protected void handshakeDone() { if (handshaking.compareAndSet(true, false)) handshakes.release(); }
    protected abstract void wakeWriter();
    protected abstract void closeTransport() throws IOException;
    public synchronized void slow(boolean hold) { slow = hold; notifyAll(); wakeWriter(); }
    protected synchronized Write head() {
        Write first = queue.peek();
        return slow && first != null && first.event ? null : first;
    }
    protected synchronized int queuedBytes() { return queuedBytes; }
    protected synchronized Write awaitHead() throws InterruptedException {
        while (!closed && head() == null) wait();
        return head();
    }
    public CompletableFuture<Boolean> send(Frame frame) {
        ByteBuffer encoded = frame.encode();
        synchronized (this) {
            if (closed) return CompletableFuture.completedFuture(false);
            if (queuedBytes + encoded.remaining() <= 256 * 1024 && queue.size() < (slow ? 2 : 64)) {
                var item = new Write(encoded, encoded.remaining(), frame.type() == Frame.EVENT, new CompletableFuture<>());
                queue.addLast(item);
                queuedBytes += item.size;
                notifyAll();
                wakeWriter();
                return item.done;
            }
        }
        room.rejected();
        close();
        return CompletableFuture.completedFuture(false);
    }
    protected void written(Write item) {
        synchronized (this) {
            if (closed || queue.peek() != item) return;
            queue.removeFirst();
            queuedBytes -= item.size;
        }
        item.done.complete(true);
    }
    protected void dispatch(Frame frame) {
        if (frame.id() <= lastId || frame.type() == Frame.EVENT) { close(); return; }
        lastId = frame.id();
        int outstanding = inFlight.incrementAndGet();
        if (outstanding > 4) { close(); return; }
        if (frame.type() == Frame.QUIT && frame.body().length == 0) {
            if (outstanding > 1) {
                send(new Frame(Frame.ERROR, frame.id(), Frame.bytes("IN_FLIGHT"))).thenRun(inFlight::decrementAndGet);
                return;
            }
            quitting = true;
        }
        room.handle(this, frame).whenComplete((ok, ex) -> inFlight.decrementAndGet());
    }
    public void close() {
        List<Write> abandoned;
        synchronized (this) {
            if (closed) return;
            closed = true;
            abandoned = List.copyOf(queue);
            queue.clear();
            queuedBytes = 0;
            notifyAll();
        }
        room.leave(this);
        handshakeDone();
        try { closeTransport(); } catch (IOException ignored) {}
        abandoned.forEach(item -> item.done.complete(false));
        removed.accept(this);
        wakeWriter();
    }
}
