package by.vorakh.dev.fibo.task_receiver.model.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FailureCreatingTaskResponse extends BaseCreatingTaskResponse {

    private String reason;

    public FailureCreatingTaskResponse(String reason) {

        super(false);
        this.reason = reason;
    }
}