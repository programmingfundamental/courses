package bg.tu_varna.sit.task_manager.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/***
 * Добавено в лабораторно упражнение 8
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TaskResponseDto {
    private long id;
    private String summary;
    private String description;
    private LocalDateTime deadline;

}
