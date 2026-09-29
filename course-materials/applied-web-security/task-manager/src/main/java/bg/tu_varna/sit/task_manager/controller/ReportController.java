package bg.tu_varna.sit.task_manager.controller;

import bg.tu_varna.sit.task_manager.exception.RelatedEntityException;
import bg.tu_varna.sit.task_manager.exception.ResourceNotFoundException;
import bg.tu_varna.sit.task_manager.model.dto.request.FilterReportDto;
import bg.tu_varna.sit.task_manager.model.dto.request.ReportRequestDto;
import bg.tu_varna.sit.task_manager.model.dto.response.ReportResponseDto;
import bg.tu_varna.sit.task_manager.model.dto.response.TaskReportResponse;
import bg.tu_varna.sit.task_manager.model.dto.response.TaskResponseDto;
import bg.tu_varna.sit.task_manager.service.ReportServiceImp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/***
 * Добавено в лабораторно упражнение 7
 */
@Validated //Добавено в лабораторно упражнение 8
@RestController
@RequestMapping("/reports")
public class ReportController {
    private ReportServiceImp service;

    public ReportController(ReportServiceImp service) {
        this.service = service;
    }

    @PostMapping("/task/{id}")
    public ResponseEntity<ReportResponseDto> create(@PathVariable(name = "id") long taskId, @Valid @RequestBody ReportRequestDto dto) throws ResourceNotFoundException {
        ReportResponseDto result = service.create(taskId, dto);//Добавено в лабораторно упражнение 10
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    @GetMapping("/task/{id}")
    public ResponseEntity<List<ReportResponseDto>> getAllByTask(@PathVariable(name = "id") long taskId) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.getAll(taskId));//Добавено в лабораторно упражнение 10
    }
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponseDto> get(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.getById(id));//Добавено в лабораторно упражнение 10
    }
    @GetMapping()
    public ResponseEntity<List<ReportResponseDto>> getByWorkTimeInDateInterval(
            @Valid @ModelAttribute FilterReportDto filter //Добавено в лабораторно упражнение 8
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.getReportsByWorkedTimeAndDateRange(filter));//Добавено в лабораторно упражнение 10
    }

    @GetMapping("/task/{id}/max-hours-worked")
    public ResponseEntity<ReportResponseDto> getByTaskWithMaxHoursWorked(@PathVariable(name = "id") long taskId) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.getReportWithMostWorkedTime(taskId));//Добавено в лабораторно упражнение 10
    }

    @GetMapping("/task/{taskId}/summary")
    public ResponseEntity<TaskReportResponse> getTaskSummaryWithWorkedTime(@PathVariable Long taskId) throws ResourceNotFoundException {
        TaskReportResponse response = service.getTaskWithTotalWorkedTime(taskId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<ReportResponseDto> update(@PathVariable Long id, @Valid @RequestBody ReportRequestDto dto) throws ResourceNotFoundException {
        ReportResponseDto result = service.update(id, dto); // Добавено в лабораторно упражнение 9
        return ResponseEntity.ok(result);  //Добавено в лабораторно упражнение 9
    }
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ReportResponseDto> delete(@PathVariable Long id) throws ResourceNotFoundException, RelatedEntityException {
        ReportResponseDto result = service.delete(id); // Добавено в лабораторно упражнение 10
        return ResponseEntity.ok(result);  //Добавено в лабораторно упражнение 10
    }
}
