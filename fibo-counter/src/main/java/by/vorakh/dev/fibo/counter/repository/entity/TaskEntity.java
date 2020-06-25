package by.vorakh.dev.fibo.counter.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskEntity {

    private long id;
    private final int number;
    private TaskStatus status;
    private final long startProcessing;
    private long finishProcessing;
    private String result;

    public TaskEntity(int number, long startProcessing) {

        this.number = number;
        this.startProcessing = startProcessing;
        this.status = TaskStatus.CREATED;
    }
}
