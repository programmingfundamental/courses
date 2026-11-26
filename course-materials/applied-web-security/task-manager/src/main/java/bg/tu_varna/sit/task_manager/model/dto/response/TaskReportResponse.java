package bg.tu_varna.sit.task_manager.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/***
 * Добавено в лабораторно упражнение 8
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TaskReportResponse {
    private TaskResponseDto task;
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING)
    private java.time.Duration totalWorkedTime;
}
