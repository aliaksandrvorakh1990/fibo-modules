package by.vorakh.dev.fibo.base.model;

import by.vorakh.dev.fibo.base.entity.TaskStatus;
import lombok.Data;

@Data
public class ResultResponse {

    private final TaskStatus status;
    private final String result;
    private final String processingTime;
}
