package labnet.tls;

import labnet.Frame;
import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public final class DemoKeys {
    public static final List<String> NAMES = List.of("ana", "boris", "iva");
    private final Path directory = Path.of(".local", "tls");

    private static String secret() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    // keytool receives the password through its environment, never through its argument list.
    private static void keytool(Path cwd, String password, String... arguments) throws Exception {
        String executable = File.separatorChar == '\\' ? "keytool.exe" : "keytool";
        var command = new ArrayList<String>();
        command.add(Path.of(System.getProperty("java.home"), "bin", executable).toString());
        command.addAll(List.of(arguments));
        command.addAll(List.of("-storepass:env", "LABNET_STORE_PASSWORD", "-storetype", "PKCS12"));
        var process = new ProcessBuilder(command).directory(cwd.toFile()).inheritIO();
        process.environment().put("LABNET_STORE_PASSWORD", password);
        if (process.start().waitFor() != 0) throw new IOException("keytool failed");
    }

    public synchronized void prepare() throws Exception {
        if (Files.isDirectory(directory)) { tokens(); serverContext(); clientContext(true); return; }
        Files.createDirectories(directory.getParent());
        Path staging = Files.createTempDirectory(directory.getParent(), "keys-");
        try {
            String password = secret();
            keytool(staging, password, "-genkeypair", "-alias", "chat", "-keyalg", "RSA", "-keysize", "2048",
                    "-validity", "365", "-keystore", "server.p12", "-dname", "CN=localhost", "-ext", "SAN=dns:localhost,ip:127.0.0.1");
            keytool(staging, password, "-exportcert", "-alias", "chat", "-keystore", "server.p12", "-file", "server.cer");
            keytool(staging, password, "-importcert", "-noprompt", "-alias", "chat", "-file", "server.cer", "-keystore", "trust.p12");
            Files.writeString(staging.resolve("store.pass"), password);
            for (String name : NAMES) Files.writeString(staging.resolve(name + ".token"), secret());
            Files.move(staging, directory);
        } finally {
            if (Files.exists(staging)) {
                try (var files = Files.list(staging)) {
                    for (Path file : files.toList()) Files.delete(file);
                }
                Files.delete(staging);
            }
        }
    }

    public byte[] token(String name) throws IOException {
        checkName(name);
        Path token = directory.resolve(name + ".token");
        if (!Files.isRegularFile(token)) throw new IOException("No demo credential for " + name);
        return Files.readAllBytes(token);
    }
    private void checkName(String name) throws IOException {
        if (!name.matches("[a-z][a-z0-9_-]{0,23}") || name.equals("server")) throw new IOException("Invalid demo name");
    }
    public byte[] provision(String name) throws IOException {
        checkName(name);
        try { Files.writeString(directory.resolve(name + ".token"), secret(), StandardOpenOption.CREATE_NEW); }
        catch (FileAlreadyExistsException existing) { /* Keep the same local identity after restart. */ }
        return token(name);
    }
    public Map<String, byte[]> tokens() throws IOException {
        var result = new HashMap<String, byte[]>();
        try (var files = Files.newDirectoryStream(directory, "*.token")) {
            for (Path file : files) {
                String filename = file.getFileName().toString();
                String name = filename.substring(0, filename.length() - ".token".length());
                result.put(name, token(name));
            }
        }
        return result;
    }
    private KeyStore store(String file, char[] password) throws Exception {
        var store = KeyStore.getInstance("PKCS12");
        try (var in = Files.newInputStream(directory.resolve(file))) { store.load(in, password); }
        return store;
    }
    public SSLContext serverContext() throws Exception {
        char[] password = Files.readString(directory.resolve("store.pass")).toCharArray();
        var managers = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        managers.init(store("server.p12", password), password);
        var context = SSLContext.getInstance("TLSv1.3");
        context.init(managers.getKeyManagers(), null, null);
        return context;
    }
    public SSLContext clientContext(boolean trusted) throws Exception {
        var managers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        if (trusted) managers.init(store("trust.p12", Files.readString(directory.resolve("store.pass")).toCharArray()));
        else {
            var empty = KeyStore.getInstance("PKCS12");
            empty.load(null, null);
            managers.init(empty);
        }
        var context = SSLContext.getInstance("TLSv1.3");
        context.init(null, managers.getTrustManagers(), null);
        return context;
    }
}
