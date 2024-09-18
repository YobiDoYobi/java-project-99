package hexlet.code.app.mapper;

import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.model.BaseEntity;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.persistence.EntityManager;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
//@AllArgsConstructor
public abstract class ReferenceMapper {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;

    public <T extends BaseEntity> T toEntity(Long id, @TargetType Class<T> entityClass) {
        return id != null ? entityManager.find(entityClass, id) : null;
    }

    public TaskStatus toTaskStatus(String slug) {
        if (slug == null || slug.isEmpty()) {
            throw new ResourceNotFoundException("Slug not found");
        }
        return taskStatusRepository.findBySlug(slug).orElseThrow(
                () -> new ResourceNotFoundException("Status with slug " + slug + " not found"));
    }

    public Set<Label> toLabels(Set<Long> listID) {
        return listID.stream()
                .map(id -> labelRepository.findById(id).get())
                .collect(Collectors.toSet());
    }

    public Set<Long> toListLabelsID(Set<Label> labels) {
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }
}
