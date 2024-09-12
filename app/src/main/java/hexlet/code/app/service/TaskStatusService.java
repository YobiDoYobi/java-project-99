package hexlet.code.app.service;

import hexlet.code.app.dto.*;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {
    @Autowired
    private TaskStatusRepository repository;
    @Autowired
    private TaskStatusMapper mapper;

    public List<TaskStatusDTO> getAll() {
        var users = repository.findAll();
        return users.stream()
                .map(p -> mapper.map(p))
                .toList();
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
        repository.deleteById(id);
    }
}
