package labnet.blocking;

import labnet.Frame;
import static labnet.Frame.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public final class ChatConnection implements AutoCloseable {
    public record Message(String author, String text) {
        public String toString() { return "[" + author + "] " + text; }
    }
    private final Socket socket;
    private final DataInputStream input;
    private final OutputStream output;
    private final Consumer<Message> events;
    private final ConcurrentMap<Long, CompletableFuture<Frame>> pending = new ConcurrentHashMap<>();
    private final CompletableFuture<Void> ended = new CompletableFuture<>();
    private long lastId;

    public ChatConnection(String host, int port, SSLContext tls, Consumer<Message> events) throws IOException {
        this.events = events;
        Socket plain = new Socket();
        Socket connected = plain;
        try {
            plain.connect(new InetSocketAddress(host, port), 3000);
            plain.setTcpNoDelay(true);
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
            socket = connected;
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            output = socket.getOutputStream();
        } catch (IOException | RuntimeException failure) {
            connected.close();
            throw failure;
        }
        Thread.ofVirtual().name("chat-receiver").start(this::receive);
    }
    private void receive() {
        try {
            while (true) {
                Frame frame = Frame.read(input);
                if (frame.type() == EVENT && frame.id() == 0) {
                    Named named = Named.unpack(frame.body());
                    events.accept(new Message(named.name(), text(named.value())));
                } else {
                    if (frame.type() != DATA && frame.type() != OK && frame.type() != ERROR)
                        throw new IOException("Unexpected response type");
                    var request = pending.remove(frame.id());
                    if (request == null) throw new IOException("Unexpected response ID");
                    request.complete(frame);
                }
            }
        } catch (Exception end) {
            synchronized (this) {
                close();
                pending.values().forEach(future -> future.completeExceptionally(end));
                pending.clear();
            }
        } finally { ended.complete(null); }
    }
    public synchronized CompletableFuture<Frame> submit(int type, byte[] body) throws IOException {
        if (socket.isClosed()) throw new EOFException("Connection closed");
        if (pending.size() >= 4) throw new IOException("Four requests already pending");
        long id = ++lastId;
        byte[] bytes = new Frame(type, id, body).encode().array();
        var reply = new CompletableFuture<Frame>();
        pending.put(id, reply);
        try { output.write(bytes); }
        catch (IOException failure) { close(); throw failure; }
        return reply;
    }
    public Frame request(int type, byte[] body) throws Exception {
        return submit(type, body).get(8, TimeUnit.SECONDS);
    }
    public CompletableFuture<Void> ended() { return ended; }
    public void close() {
        try { socket.close(); } catch (IOException ignored) {}
    }
}
