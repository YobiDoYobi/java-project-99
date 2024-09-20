package hexlet.code.service;

import hexlet.code.component.TaskSpecification;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {
    private TaskRepository repository;
    private TaskMapper mapper;
    private TaskSpecification specification;

    public List<TaskDTO> getAll() {
        var tasks = repository.findAll();
        return tasks.stream()
                .map(p -> mapper.map(p))
                .toList();
    }

    public List<TaskDTO> getAllbyParams(TaskParamsDTO params) {
        var spec = specification.build(params);
        var tasks = repository.findAll(spec);
        return tasks.stream()
                .map(p -> mapper.map(p))
                .toList();
    }

    public TaskDTO getById(long id) {
        var task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return mapper.map(task);
    }

    public TaskDTO create(TaskCreateDTO dto) {
        var task = mapper.map(dto);
        repository.save(task);
        task.getAssignee().addTask(task);
        task.getTaskStatus().addTask(task);
        task.getLabels().stream()
                .peek(l -> l.addTask(task));
        return mapper.map(task);
    }

    public TaskDTO update(long id, TaskUpdateDTO dto) {
        var task = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Task with id " + id + " not found"));
        final var oldLabels = task.getLabels();
        final var oldStatus = task.getTaskStatus();
        final var oldAssignee = task.getAssignee();
        mapper.update(dto, task);
        repository.save(task);

        if (!oldAssignee.equals(task.getAssignee())) {
            oldAssignee.removeTask(task);
            if (task.getAssignee() != null) {
                task.getAssignee().addTask(task);
            }
        }
        if (!oldStatus.equals(task.getTaskStatus())) {
            oldStatus.removeTask(task);
            if (task.getTaskStatus() != null) {
                task.getTaskStatus().addTask(task);
            }
        }
        oldLabels.stream() //старые отвязываем
                .filter(l -> !task.getLabels().contains(l))
                .peek(l -> l.removeTask(task));
        task.getLabels().stream() //новые привязываем
                .filter(l -> !oldLabels.contains(l))
                .peek(l -> l.addTask(task));
        return mapper.map(task);
    }


    public void delete(long id) {
        var task = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Task with id " + id + " not found"));
        task.getLabels().stream()
                .peek(l -> l.removeTask(task));
        if (task.getAssignee() != null) {
            task.getAssignee().removeTask(task);
        }
        if (task.getTaskStatus() != null) {
            task.getTaskStatus().removeTask(task);
        }
        repository.deleteById(id);
    }
}
