package bg.tu_varna.sit.task_manager.service;

import bg.tu_varna.sit.task_manager.exception.RelatedEntityException;
import bg.tu_varna.sit.task_manager.model.dto.request.TaskRequestDto;
import bg.tu_varna.sit.task_manager.model.dto.response.TaskResponseDto;

import java.util.List;

/***
 * Добавено в лабораторно упражнение 9
 */
public interface TaskService {
    TaskResponseDto create(TaskRequestDto dto);
    List<TaskResponseDto> getAll();
    TaskResponseDto findById(long id);
    TaskResponseDto update(long id, TaskRequestDto dto);
    TaskResponseDto delete(long id) throws RelatedEntityException;
}
