package hexlet.code.app.service;

import hexlet.code.app.dto.label.LabelCreateDTO;
import hexlet.code.app.dto.label.LabelDTO;
import hexlet.code.app.dto.label.LabelUpdateDTO;
import hexlet.code.app.exception.MethodNotAllowedException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LabelService {
    private LabelRepository repository;
    private LabelMapper mapper;
    private TaskRepository taskRepository;

    public List<LabelDTO> getAll() {
        var labels = repository.findAll();
        return labels.stream()
                .map(p -> mapper.map(p))
                .toList();
    }

    public LabelDTO getById(long id) {
        var label = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return mapper.map(label);
    }

    public LabelDTO create(LabelCreateDTO dto) {
        var label = mapper.map(dto);
        repository.save(label);
        return mapper.map(label);
    }

    public LabelDTO update(long id, LabelUpdateDTO dto) {
        var label = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + id + " not found"));
        mapper.update(dto, label);
        repository.save(label);
        return mapper.map(label);
    }

    public void delete(long id) {
        if (taskRepository.existsTasksByLabels_Id(id)) {
            throw new MethodNotAllowedException("There are tasks associated with this label");
        }
        repository.deleteById(id);
    }
}
