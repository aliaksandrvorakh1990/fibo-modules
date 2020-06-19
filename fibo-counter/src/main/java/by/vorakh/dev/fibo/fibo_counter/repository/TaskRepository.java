package by.vorakh.dev.fibo.fibo_counter.repository;

import by.vorakh.dev.fibo.fibo_counter.repository.entity.TaskEntity;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface TaskRepository {

    CompletableFuture<TaskEntity> create(TaskEntity task);

    CompletableFuture<TaskEntity> getBy(long taskId);

    CompletableFuture<Void> update(long taskId, Timestamp finishProcessing);
}
