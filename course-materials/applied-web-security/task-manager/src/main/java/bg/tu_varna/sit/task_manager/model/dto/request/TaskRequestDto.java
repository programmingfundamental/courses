package bg.tu_varna.sit.task_manager.model.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/***
 * Добавено в лабораторно упражнение 8
 */
@NoArgsConstructor
@Getter
public class TaskRequestDto {
    @NotBlank(message = "Summary is required") //Добавено в лабораторно упражнение 9
    @Size(min = 10, max = 255, message = "The summary cannot be more than 255 characters and less than 10") //Добавено в лабораторно упражнение 9
    private String summary;

    @NotNull(message = "Description is required") //Добавено в лабораторно упражнение 9
    @Size(min = 10, max = 2500, message = "The description cannot be more than 2000 characters and less than 10") //Добавено в лабораторно упражнение 9
    private String description;

    @NotNull(message = "Deadline is required") //Добавено в лабораторно упражнение 9
    @Future(message = "The deadline must be in the future") //Добавено в лабораторно упражнение 9
    private LocalDateTime deadline;
}
