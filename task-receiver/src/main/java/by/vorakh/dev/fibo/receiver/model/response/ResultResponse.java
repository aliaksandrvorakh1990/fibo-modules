package by.vorakh.dev.fibo.receiver.model.response;

import by.vorakh.dev.fibo.counter.repository.entity.TaskStatus;
import lombok.Data;

@Data
public class ResultResponse {

    private final TaskStatus status;
    private final String result;
}
