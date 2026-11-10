import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;
import java.util.jar.JarFile;

public class PackageStudent {
    public static void main(String[] args) throws Exception {
        Path jar = Path.of("target/web-security-assessment-variant2.jar");
        if (!Files.exists(jar)) throw new IllegalStateException("Run the Maven verify command first.");
        try (JarFile archive = new JarFile(jar.toFile())) {
            if (archive.stream().anyMatch(e -> e.getName().endsWith("hint.md") || e.getName().contains("AssessmentIntegrationTest")))
                throw new IllegalStateException("Instructor content found in the application archive.");
        }
        Path output = Path.of("target/web-security-assessment-variant2-student.zip");
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(output))) {
            add(zip, "web-security-assessment-variant2.jar", Files.readAllBytes(jar));
            add(zip, "README.md", Files.readAllBytes(Path.of("content/START.md")));
            add(zip, "start.cmd", "@echo off\r\ncd /d \"%~dp0\"\r\njava -jar web-security-assessment-variant2.jar\r\nif errorlevel 1 pause\r\n".getBytes(StandardCharsets.UTF_8));
            add(zip, "start.sh", "#!/bin/sh\ncd -- \"$(dirname -- \"$0\")\" || exit 1\nexec java -jar web-security-assessment-variant2.jar\n".getBytes(StandardCharsets.UTF_8));
        }
        System.out.println("Created " + output.toAbsolutePath());
    }
    private static void add(ZipOutputStream zip, String name, byte[] bytes) throws Exception {
        zip.putNextEntry(new ZipEntry(name)); zip.write(bytes); zip.closeEntry();
    }
}
