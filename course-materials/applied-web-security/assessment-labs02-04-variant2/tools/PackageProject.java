import java.nio.file.*;
import java.util.List;
import java.util.zip.*;

public class PackageProject {
    public static void main(String[] args) throws Exception {
        Path root = Path.of("").toAbsolutePath().normalize();
        Path output = root.resolve("target/web-security-assessment-variant2-project.zip");
        Files.createDirectories(output.getParent());
        List<String> entries = List.of("pom.xml", "mvnw", "mvnw.cmd", ".mvn", ".gitignore", ".dockerignore", "Dockerfile", "compose.yml", "README.md", "hint.md", "src", "content", "tools");
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(output))) {
            for (String name : entries) {
                try (var files = Files.walk(root.resolve(name))) {
                    for (Path file : files.filter(Files::isRegularFile).sorted().toList()) {
                        zip.putNextEntry(new ZipEntry("web-security-assessment-variant2/" + root.relativize(file).toString().replace('\\','/')));
                        Files.copy(file, zip); zip.closeEntry();
                    }
                }
            }
        }
        System.out.println("Created source project with instructor notes: " + output);
    }
}
