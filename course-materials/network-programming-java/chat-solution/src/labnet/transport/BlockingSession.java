package labnet.transport;

import labnet.*;
import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

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
    }
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
