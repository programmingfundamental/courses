package labnet;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class ConsoleIO {
    private static final PrintWriter UTF8_OUTPUT = new PrintWriter(System.out, true, StandardCharsets.UTF_8);
    private ConsoleIO() {}
    public static BufferedReader input() {
        return new BufferedReader(System.console() == null
                ? new InputStreamReader(System.in, StandardCharsets.UTF_8) : System.console().reader());
    }
    public static void line(String value) {
        if (System.console() != null) System.console().writer().println(value);
        else UTF8_OUTPUT.println(value);
    }
}
