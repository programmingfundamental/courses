package labnet;

import java.util.*;

public final class Options {
    private final Map<String, String> values = new HashMap<>();
    public Options(String[] args) {
        if (args.length % 2 != 0) throw new IllegalArgumentException("Expected --option value pairs");
        for (int i = 0; i < args.length; i += 2) {
            if (!args[i].startsWith("--")) throw new IllegalArgumentException(args[i]);
            values.put(args[i].substring(2), args[i + 1]);
        }
    }
    public String get(String name, String fallback) { return values.getOrDefault(name, fallback); }
    public int number(String name, int fallback, int min, int max) {
        int value = Integer.parseInt(get(name, Integer.toString(fallback)));
        if (value < min || value > max) throw new IllegalArgumentException("--" + name + " outside " + min + ".." + max);
        return value;
    }
}
