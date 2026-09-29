package bg.tu_varna.sit.task_manager.exception;

import bg.tu_varna.sit.task_manager.model.ErrorDetail;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

/***
 * Добавено в лабораторно упражнение 8
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * Добавено в лабораторно упражнение 9
     */
    @ExceptionHandler(RelatedEntityException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(RelatedEntityException exception) {
        return getErrorDetailResponseEntity(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return getErrorDetailResponseEntity(exception, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return getErrorDetailResponseEntity(exception, HttpStatus.BAD_REQUEST);
    }

    private static ResponseEntity<Object> getErrorDetailResponseEntity(Exception exception, HttpStatus status) {
        ErrorDetail errorDetails = ErrorDetail
                .builder()
                .time(LocalDateTime.now())
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(status).body(errorDetails);
    }
}
