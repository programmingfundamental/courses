package bg.tu_varna.sit.task_manager.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/***
 * Добавено в лабораторно упражнение 8
 */
@Builder
@Getter
public class ErrorDetail {
    private LocalDateTime time;
    private String message;
}
