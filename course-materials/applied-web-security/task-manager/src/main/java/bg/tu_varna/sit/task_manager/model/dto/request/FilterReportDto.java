package bg.tu_varna.sit.task_manager.model.dto.request;

import bg.tu_varna.sit.task_manager.model.validator.AfterMidnight;
import bg.tu_varna.sit.task_manager.model.validator.ValidDateRange;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Добавено в лабораторно упражнение 8
 */
@NoArgsConstructor
@Getter
@Setter
@ValidDateRange
public class FilterReportDto {
    @NotNull(message = "Time is required")
    @AfterMidnight
    private LocalTime time;

    @NotNull(message = "Date from is required")
    private LocalDateTime from;

    @NotNull(message = "Date to is required")
    private LocalDateTime to;
}
