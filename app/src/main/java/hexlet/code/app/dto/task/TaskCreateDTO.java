package hexlet.code.app.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {
    private int index;
    @JsonProperty("assignee_id")
    private long assigneeId;
    private String title;
    private String content;
    private String status;
    private Set<Long> taskLabelIds;
}
