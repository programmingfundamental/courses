package labnet;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;
import java.nio.ByteBuffer;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

import static labnet.Frame.*;

public final class Room implements AutoCloseable {
    public abstract static class Peer implements AutoCloseable {
        public volatile String name;
        public String model;
        private String principal;
        private int authFailures;
        private boolean retired;
        public abstract CompletableFuture<Boolean> send(Frame frame);
        public abstract void close();
    }

    private final Map<String, Peer> members = new TreeMap<>();
    private final Map<String, byte[]> tokens;
    private final Set<Peer> connected = new HashSet<>();
    private final LongAdder requests = new LongAdder(), errors = new LongAdder(), rejected = new LongAdder();
    private final ThreadPoolExecutor workers = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(64));

    public Room(Map<String, byte[]> tokens) {
        this.tokens = new ConcurrentHashMap<>(tokens);
    }
    public void provision(String name, byte[] token) { tokens.put(name, token); }
    public synchronized void enter(Peer peer) { connected.add(peer); }
    public void rejected() { rejected.increment(); }
    public synchronized String stats() {
        return "active=" + connected.size() + ";loggedIn=" + members.size() + ";requests=" + requests.sum()
                + ";errors=" + errors.sum() + ";rejected=" + rejected.sum() + ";workQueued=" + workers.getQueue().size();
    }
    public void close() { workers.shutdownNow(); }

    public synchronized void leave(Peer peer) {
        connected.remove(peer);
        peer.retired = true;
        if (peer.name != null) members.remove(peer.name, peer);
    }
    private synchronized String login(Peer peer, String name) {
        if (peer.retired) return "SESSION_CLOSED";
        if (!validName(name)) return "BAD_PAYLOAD";
        if (peer.name != null) return "ALREADY_LOGGED_IN";
        if (peer.principal != null && !peer.principal.equals(name)) return "FORBIDDEN";
        if ("server".equals(name) || members.containsKey(name)) return "NAME_IN_USE";
        members.put(name, peer);
        peer.name = name;
        return null;
    }
    private synchronized String users() { return String.join("\n", members.keySet()); }
    private synchronized List<Peer> snapshot() { return List.copyOf(members.values()); }
    private synchronized Peer lookup(String name) { return members.get(name); }

    public static boolean validMessage(byte[] message) {
        if (message.length == 0 || message.length > 4096) return false;
        try { text(message); return true; }
        catch (IOException invalid) { return false; }
    }

    public CompletableFuture<String> publish(String author, byte[] message) {
        if (!validMessage(message)) return CompletableFuture.completedFuture("BAD_PAYLOAD");
        Frame event = new Frame(EVENT, 0, new Named(author, message).pack());
        List<CompletableFuture<Boolean>> writes = new ArrayList<>();
        for (Peer member : snapshot()) writes.add(member.send(event));
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new)).thenApply(done -> {
            long delivered = writes.stream().filter(write -> write.join()).count();
            return "delivered=" + delivered + ";failed=" + (writes.size() - delivered);
        });
    }

    // Each transport dispatches requests serially per peer. NIO completes writes on its selector thread.
    public CompletableFuture<Boolean> handle(Peer peer, Frame request) {
        requests.increment();
        long id = request.id();
        int type = request.type();
        byte[] body = request.body();
        try {
            if (type == QUIT && body.length == 0) {
                leave(peer);
                return peer.send(new Frame(OK, id, EMPTY)).whenComplete((ok, ex) -> peer.close());
            }
            if (type == PING && body.length == 0) return peer.send(new Frame(OK, id, EMPTY));
            if (type == QUIT || type == PING) return error(peer, id, "BAD_PAYLOAD");
            if (type == AUTH) return authenticate(peer, id, body);
            if (peer.principal == null) return error(peer, id, "AUTH_REQUIRED");
            if (type == ECHO) return peer.send(new Frame(DATA, id, body));
            if (type == LOGIN) {
                String problem = login(peer, text(body));
                return problem == null ? peer.send(new Frame(OK, id, EMPTY)) : error(peer, id, problem);
            }
            if (type != USERS && type != BROADCAST && type != SEND && type != STATS && type != WORK)
                return error(peer, id, "BAD_TYPE");
            if (peer.name == null) return error(peer, id, "NOT_LOGGED_IN");
            if (type == USERS)
                return body.length == 0 ? peer.send(new Frame(DATA, id, bytes(users()))) : error(peer, id, "BAD_PAYLOAD");
            if (type == STATS)
                return body.length == 0 ? peer.send(new Frame(DATA, id, bytes(stats() + ";model=" + peer.model))) : error(peer, id, "BAD_PAYLOAD");
            if (type == WORK) return work(peer, id, body);
            if (type == SEND) {
                Named to = Named.unpack(body);
                if (!validMessage(to.value())) return error(peer, id, "BAD_PAYLOAD");
                Peer target = lookup(to.name());
                if (target == null) return error(peer, id, "NO_SUCH_USER");
                return target.send(new Frame(EVENT, 0, new Named(peer.name, to.value()).pack())).thenCompose(ok ->
                        ok ? peer.send(new Frame(OK, id, EMPTY)) : error(peer, id, "SLOW_RECIPIENT"));
            }
            if (!validMessage(body)) return error(peer, id, "BAD_PAYLOAD");
            return publish(peer.name, body).thenCompose(counts -> peer.send(new Frame(DATA, id, bytes(counts))));
        } catch (IOException invalid) {
            return error(peer, id, "BAD_PAYLOAD");
        }
    }

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
    private CompletableFuture<Boolean> work(Peer peer, long id, byte[] body) {
        if (body.length < 4 || body.length > 4100) return error(peer, id, "BAD_PAYLOAD");
        int millis = ByteBuffer.wrap(body).getInt();
        if (millis < 0 || millis > 50) return error(peer, id, "BAD_PAYLOAD");
        try {
            return CompletableFuture.supplyAsync(() -> {
                try { Thread.sleep(millis); } catch (InterruptedException end) { Thread.currentThread().interrupt(); }
                return new Frame(DATA, id, Arrays.copyOfRange(body, 4, body.length));
            }, workers).thenCompose(peer::send);
        } catch (RejectedExecutionException full) {
            rejected();
            return error(peer, id, "BUSY");
        }
    }
    private CompletableFuture<Boolean> error(Peer peer, long id, String code) {
        errors.increment();
        return peer.send(new Frame(ERROR, id, bytes(code)));
    }
}
