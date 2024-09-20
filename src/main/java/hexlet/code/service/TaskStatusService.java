package hexlet.code.service;

import hexlet.code.component.TaskSpecification;
import hexlet.code.dto.taskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.dto.taskStatus.TaskStatusUpdateDTO;
import hexlet.code.exception.MethodNotAllowedException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static hexlet.code.utils.Utils.urlNormalize;

@Service
@AllArgsConstructor
public class TaskStatusService {
    private TaskStatusRepository repository;
    private TaskStatusMapper mapper;
    private TaskRepository taskRepository;
    private TaskSpecification taskSpecification;

    public List<TaskStatusDTO> getAll(Map<String, String> allParams) {
        Map<String, Object> params = urlNormalize(allParams);
        var taskStatuses = repository.findAll(PageRequest.of((Integer) params.get("page"),
                (Integer) params.get("perPage")));
        return taskStatuses.map(mapper::map).toList();
    }

    public long count() {
        return repository.count();
    }

    public TaskStatusDTO getById(long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status with id " + id + " not found"));
        return mapper.map(user);
    }

    public TaskStatusDTO create(TaskStatusCreateDTO dto) {
        var taskStatus = mapper.map(dto);
        repository.save(taskStatus);
        return mapper.map(taskStatus);
    }

    public TaskStatusDTO update(long id, TaskStatusUpdateDTO dto) {
        var taskStatus = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + id + " not found"));
        mapper.update(dto, taskStatus);
        repository.save(taskStatus);
        return mapper.map(taskStatus);
    }

    public void delete(long id) {
        var taskStatus = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Status with id " + id + " not found"));
        if (taskRepository.exists(taskSpecification.withStatus(taskStatus.getSlug()))) {
            throw new MethodNotAllowedException("There are tasks wit status id " + id);
        }
        repository.deleteById(id);
    }
}
