package bg.tuvarna.lab;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class Errors {
    @ExceptionHandler({IllegalArgumentException.class,DataAccessException.class})
    ResponseEntity<Map<String,String>> invalid(Exception ignored) { return ResponseEntity.badRequest().body(Map.of("error","Invalid request")); }
}
