package bg.tu_varna.sit.task_manager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/***
 * Добавено в лабораторно упражнение 9
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail {
    private LocalDateTime time;
    private String message;
}
