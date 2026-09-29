package bg.tu_varna.sit.task_manager.service;

import bg.tu_varna.sit.task_manager.exception.RelatedEntityException;
import bg.tu_varna.sit.task_manager.exception.ResourceNotFoundException;
import bg.tu_varna.sit.task_manager.model.dto.request.FilterReportDto;
import bg.tu_varna.sit.task_manager.model.dto.request.ReportRequestDto;
import bg.tu_varna.sit.task_manager.model.dto.request.TaskRequestDto;
import bg.tu_varna.sit.task_manager.model.dto.response.ReportResponseDto;
import bg.tu_varna.sit.task_manager.model.dto.response.TaskReportResponse;
import bg.tu_varna.sit.task_manager.model.dto.response.TaskResponseDto;
import bg.tu_varna.sit.task_manager.model.entity.Report;
import bg.tu_varna.sit.task_manager.model.entity.Task;
import bg.tu_varna.sit.task_manager.repository.ReportRepository;
import bg.tu_varna.sit.task_manager.repository.TaskRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReportServiceImp {
    private ReportRepository repository;
    private TaskRepository taskRepository;
    private ModelMapper mapper;

    public ReportServiceImp(ReportRepository repository, TaskRepository taskRepository, ModelMapper mapper) {
        this.repository = repository;
        this.taskRepository = taskRepository;
        this.mapper = mapper;
    }

    public ReportResponseDto create(long taskId, ReportRequestDto dto) throws ResourceNotFoundException {
        Report report = mapper.map(dto, Report.class);
        report.setTask(
                taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException(taskId, Task.class))
        );
        report = repository.save(report);
        ReportResponseDto result = mapper.map(report, ReportResponseDto.class);
        return result;
    }

    public List<ReportResponseDto> getAll(long taskId) {
        List<Report> reports = repository.findByTaskId( taskId);
        return mapToReportResponseDtoList(reports);
    }

    public ReportResponseDto getById(Long id) throws ResourceNotFoundException {
        Report report = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id, Report.class));
        ReportResponseDto result = mapper.map(report, ReportResponseDto.class);
        return result;
    }

    public List<ReportResponseDto> getReportsByWorkedTimeAndDateRange(FilterReportDto dto) {
        List<Report> reports = repository.findByWorkedTimeAndDateCreatedBetween(dto.getTime(), dto.getFrom(), dto.getTo());
        return mapToReportResponseDtoList(reports);
    }

    private List<ReportResponseDto> mapToReportResponseDtoList(List<Report> reports) {
        Type listType = new TypeToken<List<ReportResponseDto>>() {}.getType();
        List<ReportResponseDto> result = mapper.map(reports, listType);
        return result;
    }

    public ReportResponseDto update(long id, ReportRequestDto dto) {
        Report report = repository.findById(id).orElseThrow(ResolutionException::new);
        mapper.map(dto, report);
        Report updated = repository.save(report);
        return mapper.map(updated, ReportResponseDto.class);
    }

    public ReportResponseDto delete(long id) throws RelatedEntityException {
        Report report = repository.findById(id).orElseThrow(ResolutionException::new);
        try {
            repository.delete(report);
        } catch (DataIntegrityViolationException e) {
            throw new RelatedEntityException(id, Task.class);
        }
        ReportResponseDto result = mapper.map(report, ReportResponseDto.class);
        return result;
    }

    public ReportResponseDto getReportWithMostWorkedTime(Long taskId) throws ResourceNotFoundException {
        Report report = repository.findTop1ByTaskIdOrderByWorkedTimeDesc(taskId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(taskId, Report.class));
        ReportResponseDto result = mapper.map(report, ReportResponseDto.class);
        return result;
    }

    public TaskReportResponse getTaskWithTotalWorkedTime(Long taskId) throws ResourceNotFoundException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(taskId, Task.class));

        java.time.Duration totalWorkedTime = java.time.Duration.ofSeconds(repository.findByTaskId(taskId).stream()
                .mapToLong(r -> r.getWorkedTime().toSecondOfDay()).sum());

        TaskResponseDto taskResponseDto = mapper.map(task, TaskResponseDto.class);
        TaskReportResponse result = new TaskReportResponse(taskResponseDto, totalWorkedTime);
        return result;
    }

}
