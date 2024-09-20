package hexlet.code.component;

import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskParamsDTO params) {
        return withTitleContains(params.getTitleCont())
                .and(withAssigneeId(params.getAssigneeId()))
                .and(withStatus(params.getStatus()))
                .and(withLabelId(params.getLabelId()));
    }

    public Specification<Task> withTitleContains(String title) {
        return ((root, query, cb) -> title == null ? cb.conjunction()
                : cb.like(root.get("name"), "%" + title + "%"));
    }

    public Specification<Task> withAssigneeId(Long assigneeId) {
        return ((root, query, cb) -> assigneeId == null ? cb.conjunction()
                : cb.equal(root.get("assignee").get("id"), assigneeId));
    }

    public Specification<Task> withStatus(String status) {
        return ((root, query, cb) -> status == null ? cb.conjunction()
                : cb.equal(root.get("taskStatus").get("slug"), status));
    }

    public Specification<Task> withLabelId(Long labelId) {
        return ((root, query, cb) -> labelId == null ? cb.conjunction()
                : cb.equal(root.get("labels").get("id"), labelId));
    }
}
