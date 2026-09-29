package bg.tu_varna.sit.task_manager.controller;

import bg.tu_varna.sit.task_manager.exception.RelatedEntityException;
import bg.tu_varna.sit.task_manager.exception.ResourceNotFoundException;
import bg.tu_varna.sit.task_manager.model.dto.request.TaskRequestDto;
import bg.tu_varna.sit.task_manager.model.dto.response.TaskResponseDto;
import bg.tu_varna.sit.task_manager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/***
 * Добавено в лабораторно упражнение 7
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    /**
     * Добавено в лабораторно упражнение 9
     */
    private TaskService service;

    /**
     * Добавено в лабораторно упражнение 9
     */
    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping()
    public ResponseEntity<TaskResponseDto> create(@Valid @RequestBody TaskRequestDto dto) {
        TaskResponseDto result = service.create(dto); // Добавено в лабораторно упражнение 9
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    @GetMapping()
    public ResponseEntity<List<TaskResponseDto>> getAll() {
        List<TaskResponseDto> result = service.getAll();
        return ResponseEntity.ok(result);  //Добавено в лабораторно упражнение 9
    }
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> get(@Valid @PathVariable Long id) {
        TaskResponseDto result = service.findById(id); // Добавено в лабораторно упражнение 9
        return ResponseEntity.ok(result);  //Добавено в лабораторно упражнение 9
    }
    @PatchMapping("/{id}/update")
    public ResponseEntity<TaskResponseDto> update(@PathVariable Long id,@Valid  @RequestBody TaskRequestDto dto) throws ResourceNotFoundException {
        TaskResponseDto result = service.update(id, dto); // Добавено в лабораторно упражнение 9
        return ResponseEntity.ok(result);  //Добавено в лабораторно упражнение 9
    }
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<TaskResponseDto> delete(@Valid @PathVariable Long id) throws RelatedEntityException {
        TaskResponseDto result = service.delete(id); // Добавено в лабораторно упражнение 9
        return ResponseEntity.ok(result);  //Добавено в лабораторно упражнение 9
    }
}
