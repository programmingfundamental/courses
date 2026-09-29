package bg.tu_varna.sit.task_manager.service;

import bg.tu_varna.sit.task_manager.exception.RelatedEntityException;
import bg.tu_varna.sit.task_manager.model.dto.request.TaskRequestDto;
import bg.tu_varna.sit.task_manager.model.dto.response.TaskResponseDto;
import bg.tu_varna.sit.task_manager.model.entity.Task;
import bg.tu_varna.sit.task_manager.repository.TaskRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.lang.reflect.Type;
import java.util.List;

/***
 * Добавено в лабораторно упражнение 9
 */
@Service
public class TaskServiceImp implements TaskService {
    private TaskRepository repository;
    private ModelMapper mapper;

    public TaskServiceImp(TaskRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public TaskResponseDto create(TaskRequestDto dto) {
        Task task = mapper.map(dto, Task.class);
        task = repository.save(task);
        TaskResponseDto result = mapper.map(task, TaskResponseDto.class);
        return result;
    }

    @Override
    public List<TaskResponseDto> getAll() {
        List<Task> tasks = repository.findAll();
        Type listType = new TypeToken<List<TaskResponseDto>>() {}.getType();
        List<TaskResponseDto> result = mapper.map(tasks, listType);
        return result;
    }

    @Override
    public TaskResponseDto findById(long id) {
        Task task = repository.findById(id).orElseThrow(ResolutionException::new);
        TaskResponseDto result = mapper.map(task, TaskResponseDto.class);
        return result;
    }

    @Override
    public TaskResponseDto update(long id, TaskRequestDto dto) {
        Task task = repository.findById(id).orElseThrow(ResolutionException::new);
        mapper.map(dto, task);
        Task updated = repository.save(task);
        return mapper.map(updated, TaskResponseDto.class);
    }

    @Override
    public TaskResponseDto delete(long id) throws RelatedEntityException {
        Task task = repository.findById(id).orElseThrow(ResolutionException::new);
        try {
            repository.delete(task);
        } catch (DataIntegrityViolationException e) {
            throw new RelatedEntityException(id, Task.class);
        }
        TaskResponseDto result = mapper.map(task, TaskResponseDto.class);
        return result;
    }
}
