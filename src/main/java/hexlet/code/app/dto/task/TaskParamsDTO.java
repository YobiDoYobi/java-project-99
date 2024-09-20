package hexlet.code.app.dto.task;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskParamsDTO {
    private String titleCont; //название задачи содержит подстроку
    private Long assigneeId; //идентификатор исполнителя задачи
    private String status; //слаг статуса задачи
    private Long labelId; //идентификатор метки задачи
}
