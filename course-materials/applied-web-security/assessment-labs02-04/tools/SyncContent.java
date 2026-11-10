import java.nio.file.*;
import java.util.Arrays;

public class SyncContent {
    public static void main(String[] args) throws Exception {
        Path source = Path.of("content/assignment.json");
        Path output = Path.of("src/main/resources/static/assignment.json");
        byte[] expected = Files.readAllBytes(source);
        if (Arrays.asList(args).contains("--check")) {
            if (!Files.exists(output) || !Arrays.equals(expected, Files.readAllBytes(output)))
                throw new IllegalStateException("Student content is outdated. Run: java tools/SyncContent.java");
            System.out.println("PASS: student content matches its source.");
        } else {
            Files.createDirectories(output.getParent()); Files.write(output, expected);
            System.out.println("Synchronized student assignment.");
        }
    }
}
