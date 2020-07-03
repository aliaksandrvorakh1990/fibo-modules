package by.vorakh.dev.fibo.base.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskEntity {

    private long id;
    private final int number;
    private TaskStatus status;
    private final long creationTime;
    private long endTime;
    private String result;

    public TaskEntity(int number, long creationTime) {

        this.number = number;
        this.creationTime = creationTime;
        this.status = TaskStatus.CREATED;
    }
}
