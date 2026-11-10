package labnet;

import labnet.blocking.ChatConnection;
import labnet.demo.Benchmark;
import labnet.tls.DemoKeys;
import labnet.udp.Counter;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import static labnet.Frame.*;

/** Each local client owns its TLS socket, UDP session, reader and request IDs. */
public final class ChatClient implements AutoCloseable {
    private record Connection(ChatConnection chat, Counter.Monitor monitor) implements AutoCloseable {
        public void close() { chat.close(); monitor.close(); }
    }
    private final Map<String, Connection> clients = new LinkedHashMap<>();
    private final DemoKeys keys = new DemoKeys();
    private final SSLContext tls;
    private final String host;
    private final int port, udpPort;
    private String current;

    public ChatClient(String host, int port, int udpPort) throws Exception {
        this.host = host; this.port = port; this.udpPort = udpPort;
        tls = keys.clientContext(true);
    }
    private void open(String name) throws Exception {
        prune();
        if (clients.containsKey(name)) { ConsoleIO.line("Already open: " + name); return; }
        byte[] token = keys.token(name);
        var monitor = new Counter.Monitor(host, udpPort, ack -> ConsoleIO.line(name + " UDP " + ack));
        ChatConnection chat = null;
        try {
            chat = new ChatConnection(host, port, tls, event -> {
                ConsoleIO.line(name + " < " + event);
                monitor.displayed();
            });
            requireOk(chat.request(AUTH, new Named(name, token).pack()));
            requireOk(chat.request(LOGIN, bytes(name)));
            clients.put(name, new Connection(chat, monitor));
            chat.ended().thenRun(() -> { ConsoleIO.line("Disconnected " + name); monitor.close(); });
            current = name;
            ConsoleIO.line("Connected " + name);
        } catch (Exception failure) {
            if (chat != null) chat.close();
            monitor.close();
            throw failure;
        }
    }
    private static void requireOk(Frame reply) throws IOException {
        if (reply.type() != OK) throw new IOException(text(reply.body()));
    }
    private void prune() {
        var iterator = clients.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().chat.ended().isDone()) {
                entry.getValue().close(); iterator.remove();
            }
        }
        if (!clients.containsKey(current)) current = clients.keySet().stream().findFirst().orElse(null);
    }
    private void show(Frame reply) throws IOException {
        String type = switch (reply.type()) { case OK -> "OK"; case DATA -> "DATA"; default -> "ERROR"; };
        ConsoleIO.line(current + " " + type + " #" + reply.id() + " " + text(reply.body()));
    }
    private boolean command(String line) throws Exception {
        prune();
        String[] args = line.split(" ", 3);
        String name = args.length > 1 ? args[1] : "";
        switch (args[0]) {
            case "/help" -> { ConsoleIO.line("/new name, /use name, /clients, /login name, /users, /ping, /echo text, /send name text, /stats, /work ms text, /burst, /bench [clients] [messages], /quit, /exit"); return true; }
            case "/new", "/login" -> { open(name); return true; }
            case "/use" -> {
                if (!clients.containsKey(name)) throw new IOException("No local client " + name);
                current = name; ConsoleIO.line("Active " + name); return true;
            }
            case "/clients" -> { ConsoleIO.line("Local: " + clients.keySet() + " | active=" + current); return true; }
            case "/exit" -> { return false; }
            case "/bench" -> {
                int count = args.length > 1 ? Integer.parseInt(name) : 3;
                int messages = args.length > 2 ? Integer.parseInt(args[2]) : 10;
                Benchmark.measure(host, port, tls, keys, count, messages, 30, Path.of("results/chat.csv"));
                return true;
            }
        }
        if (current == null) {
            if (line.equals("/quit")) return false;
            ConsoleIO.line("Open a client with /new ana, /new boris or /new iva");
            return true;
        }
        var chat = clients.get(current).chat;
        switch (args[0]) {
            case "/quit" -> {
                show(chat.request(QUIT, EMPTY));
                clients.remove(current).close();
                current = clients.keySet().stream().findFirst().orElse(null);
                if (current != null) ConsoleIO.line("Active " + current);
                return current != null;
            }
            case "/ping" -> show(chat.request(PING, EMPTY));
            case "/users" -> show(chat.request(USERS, EMPTY));
            case "/stats" -> show(chat.request(STATS, EMPTY));
            case "/echo" -> show(chat.request(ECHO, bytes(line.length() > 5 ? line.substring(6) : "")));
            case "/send" -> {
                if (args.length < 3) throw new IllegalArgumentException("/send name text");
                show(chat.request(SEND, new Named(name, bytes(args[2])).pack()));
            }
            case "/work" -> {
                int ms = Integer.parseInt(name);
                byte[] data = args.length > 2 ? bytes(args[2]) : EMPTY;
                show(chat.request(WORK, ByteBuffer.allocate(4 + data.length).putInt(ms).put(data).array()));
            }
            case "/burst" -> {
                var replies = new ArrayList<CompletableFuture<Frame>>();
                for (int i = 1; i <= 3; i++) replies.add(chat.submit(BROADCAST, bytes("burst-" + i)));
                for (var reply : replies) show(reply.get(8, TimeUnit.SECONDS));
            }
            default -> {
                if (line.startsWith("/")) throw new IllegalArgumentException("Unknown command. Use /help");
                show(chat.request(BROADCAST, bytes(line)));
            }
        }
        return true;
    }
    public void close() { clients.values().forEach(Connection::close); clients.clear(); }
    public static void main(String[] args) throws Exception {
        var opts = new Options(args);
        try (var client = new ChatClient(opts.get("host", "localhost"), opts.number("port", 9000, 1, 65535),
                opts.number("udp-port", 9001, 1, 65535))) {
            ConsoleIO.line("ChatClient | /new ana, /new boris, /new iva | /use name | /help");
            String name = opts.get("name", "");
            if (!name.isEmpty()) client.open(name);
            var input = ConsoleIO.input();
            for (String line; (line = input.readLine()) != null;) {
                try { if (!client.command(line)) return; }
                catch (Exception failure) { ConsoleIO.line("Error: " + failure.getMessage()); }
            }
        }
    }
}
