package labnet.demo;

import labnet.*;
import labnet.blocking.*;
import javax.net.ssl.SSLContext;
import labnet.tls.DemoKeys;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.LockSupport;
import static labnet.Frame.*;

public final class Benchmark {
    private static final String HEADER = "mode,clients,messages_each,published,expected_events,received,missing,duplicates,unexpected,rejected,timeouts,errors,publications_s,events_s,own_samples,p95_own_ms";

    private static void run(String host, int port, SSLContext tls, DemoKeys keys, int count, int messages, int intervalMs, Path csv) throws Exception {
        List<String> names = new ArrayList<>();
        for (int n = 1; n <= count; n++) names.add("bench" + n);
        int publications = count * messages, expected = count * publications;
        var seen = new ArrayList<Set<ChatConnection.Message>>();
        var corpus = new HashSet<ChatConnection.Message>();
        List<String> texts = new ArrayList<>();
        for (int n = 1; n <= messages; n++) {
            String content = String.format(Locale.ROOT, "post-%05d", n);
            texts.add(content);
            for (int user = 0; user < count; user++) corpus.add(new ChatConnection.Message(names.get(user), content));
        }
        var sentAt = new ConcurrentHashMap<ChatConnection.Message, Long>();
        var latencies = new ConcurrentLinkedQueue<Long>();
        var received = new CountDownLatch(expected);
        var duplicates = new AtomicInteger();
        var unexpected = new AtomicInteger();
        var rejected = new AtomicInteger();
        var timeouts = new AtomicInteger();
        var errors = new AtomicInteger();
        var completed = new AtomicInteger();
        var clients = new ArrayList<ChatConnection>();
        try (var writers = Executors.newFixedThreadPool(count)) {
            for (int i = 0; i < count; i++) {
                String name = names.get(i);
                Set<ChatConnection.Message> inbox = ConcurrentHashMap.newKeySet();
                seen.add(inbox);
                var client = new ChatConnection(host, port, tls, message -> {
                    if (!names.contains(message.author())) return;
                    if (!corpus.contains(message)) { unexpected.incrementAndGet(); return; }
                    if (!inbox.add(message)) { duplicates.incrementAndGet(); return; }
                    if (message.author().equals(name)) latencies.add(System.nanoTime() - sentAt.get(message));
                    received.countDown();
                });
                clients.add(client);
                if (client.request(AUTH, new Named(name, keys.token(name)).pack()).type() != OK)
                    throw new IllegalStateException("AUTH " + name);
                if (client.request(LOGIN, bytes(name)).type() != OK) throw new IllegalStateException("LOGIN " + name);
            }
            String stats = text(clients.getFirst().request(STATS, EMPTY).body());
            String mode = stats.substring(stats.lastIndexOf("model=") + 6);
            int audience = text(clients.getFirst().request(USERS, EMPTY).body()).split("\n").length;
            var jobs = new ArrayList<Future<?>>();
            long start = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(100);
            for (int i = 0; i < count; i++) {
                ChatConnection client = clients.get(i);
                String name = names.get(i);
                jobs.add(writers.submit(() -> {
                    for (int n = 0; n < messages; n++) {
                        long scheduled = start + TimeUnit.MILLISECONDS.toNanos((long) n * intervalMs);
                        while (scheduled > System.nanoTime()) LockSupport.parkNanos(scheduled - System.nanoTime());
                        String content = texts.get(n);
                        sentAt.put(new ChatConnection.Message(name, content), System.nanoTime());
                        try {
                            var reply = client.request(BROADCAST, bytes(content));
                            if (reply.type() == DATA && text(reply.body()).equals("delivered=" + audience + ";failed=0"))
                                completed.incrementAndGet();
                            else rejected.incrementAndGet();
                        } catch (TimeoutException timeout) { timeouts.incrementAndGet(); }
                        catch (Exception failure) { errors.incrementAndGet(); }
                    }
                }));
            }
            for (var job : jobs) job.get();
            received.await(3, TimeUnit.SECONDS);
            double seconds = (System.nanoTime() - start) / 1e9;
            // PING is a per-connection barrier after all publication writes and replies.
            for (var client : clients) {
                try { if (client.request(PING, EMPTY).type() != OK) errors.incrementAndGet(); }
                catch (TimeoutException timeout) { timeouts.incrementAndGet(); }
                catch (Exception failure) { errors.incrementAndGet(); }
            }
            int unique = seen.stream().mapToInt(Set::size).sum(), missing = expected - unique;
            int actual = unique + duplicates.get() + unexpected.get();
            var sorted = latencies.stream().sorted().toList();
            String p95 = sorted.isEmpty() ? "NA" : String.format(Locale.ROOT, "%.3f",
                    sorted.get((int) Math.ceil(0.95 * sorted.size()) - 1) / 1e6);
            String row = String.format(Locale.ROOT, "%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%.2f,%.2f,%d,%s",
                    mode, count, messages, completed.get(), expected, actual, missing, duplicates.get(), unexpected.get(),
                    rejected.get(), timeouts.get(), errors.get(), completed.get() / seconds, actual / seconds, sorted.size(), p95);
            System.out.println(row);
            append(csv, row);
            if (completed.get() != publications || missing != 0 || actual != unique || sorted.size() != publications
                    || errors.get() != 0 || timeouts.get() != 0)
                throw new IllegalStateException("Delivery check failed: " + row);
        } finally { clients.forEach(ChatConnection::close); }
    }
    private static synchronized void append(Path csv, String row) throws Exception {
        try (var file = FileChannel.open(csv, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             var lock = file.lock()) {
            long size = file.size();
            file.position(size);
            String content = (size == 0 ? HEADER + "\n" : "") + row + "\n";
            ByteBuffer data = StandardCharsets.UTF_8.encode(content);
            while (data.hasRemaining()) file.write(data);
        }
    }

    public static void measure(String host, int port, SSLContext tls, DemoKeys keys, int clients, int messages, int intervalMs, Path csv) throws Exception {
        if (clients < 1 || clients > 16 || messages < 1 || messages > 100 || intervalMs < 1 || intervalMs > 1000)
            throw new IllegalArgumentException("Benchmark: 1..16 clients, 1..100 messages, 1..1000 ms");
        Files.createDirectories(csv.toAbsolutePath().getParent());
        System.out.println(HEADER);
        run(host, port, tls, keys, clients, messages, intervalMs, csv);
    }
}
