package by.vorakh.dev.fibo.fibo_counter.repository;

import by.vorakh.dev.fibo.fibo_counter.repository.entity.TaskResultEntity;

import java.util.concurrent.CompletableFuture;

public interface TaskResultRepository {

    CompletableFuture<TaskResultEntity> create(TaskResultEntity result);

    CompletableFuture<String> getResultBy(long taskId);
}
