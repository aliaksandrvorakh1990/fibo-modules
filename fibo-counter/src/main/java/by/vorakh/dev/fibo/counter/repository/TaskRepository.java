package by.vorakh.dev.fibo.counter.repository;

import by.vorakh.dev.fibo.counter.repository.entity.TaskEntity;
import by.vorakh.dev.fibo.counter.repository.entity.TaskStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface TaskRepository {

    CompletableFuture<@NotNull TaskEntity> create(@NotNull TaskEntity task);

    CompletableFuture<@Nullable TaskEntity> getBy(long taskId);

    CompletableFuture<Void> update(
        long taskId,
        long finishProcessing,
        @NotNull TaskStatus status,
        @NotNull String result
    );

    CompletableFuture<Void> update(long taskId, @NotNull TaskStatus status);
}
