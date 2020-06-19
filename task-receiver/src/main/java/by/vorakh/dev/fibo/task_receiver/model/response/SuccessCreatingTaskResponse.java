package by.vorakh.dev.fibo.task_receiver.model.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SuccessCreatingTaskResponse extends BaseCreatingTaskResponse {

    private Long taskId;
    private final String timestamp;

    public SuccessCreatingTaskResponse(Long taskId, String timestamp) {

        super(true);
        this.taskId = taskId;
        this.timestamp = timestamp;
    }
}
