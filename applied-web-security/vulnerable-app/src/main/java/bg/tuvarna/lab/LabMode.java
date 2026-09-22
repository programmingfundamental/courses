package bg.tuvarna.lab;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Set;

/** ONLY for the isolated teaching application. Never deploy this project. */
@Component
public class LabMode {
    private final String mode;
    public LabMode(@Value("${lab.mode}") String mode) {
        if (!Set.of("secure", "lab01", "lab02", "lab03", "lab04", "lab05", "lab06", "lab07", "lab08", "lab09", "lab10").contains(mode))
            throw new IllegalArgumentException("Unknown lab mode");
        this.mode = mode;
    }
    public boolean vulnerable(int lab) {
        return mode.equals("lab%02d".formatted(lab)) || (mode.equals("lab10") && Set.of(3,5,6).contains(lab));
    }
}
