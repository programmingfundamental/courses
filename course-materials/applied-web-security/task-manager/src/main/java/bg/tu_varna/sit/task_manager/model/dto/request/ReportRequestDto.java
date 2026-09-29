package bg.tu_varna.sit.task_manager.model.dto.request;

import bg.tu_varna.sit.task_manager.model.validator.AfterMidnight;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.time.LocalTime;

/***
 * Добавено в лабораторно упражнение 7
 */
@NoArgsConstructor
@Getter
public class ReportRequestDto {
    @NotBlank(message = "Content is required") //Добавено в лабораторно упражнение 8
    @Size(min = 10, max = 2500, message = "Content must be at least 10 characters and no more than 2500") //Добавено в лабораторно упражнение 8
    private String content;

    @NotNull(message = "WorkTime is required") //Добавено в лабораторно упражнение 8
    @AfterMidnight //Добавено в лабораторно упражнение 8
    private LocalTime workTime;
}
