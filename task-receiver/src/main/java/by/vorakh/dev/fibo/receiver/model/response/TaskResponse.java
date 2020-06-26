package by.vorakh.dev.fibo.receiver.model.response;

import by.vorakh.dev.fibo.counter.repository.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class TaskResponse {

    private final TaskStatus status;
    private final String result;
}
