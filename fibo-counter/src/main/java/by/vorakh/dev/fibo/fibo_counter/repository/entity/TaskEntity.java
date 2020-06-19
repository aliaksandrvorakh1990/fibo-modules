package by.vorakh.dev.fibo.fibo_counter.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    private long id;
    private final int number;
    private boolean isCompleted;
    private final Timestamp startProcessing;
    private Timestamp finishProcessing;
}
