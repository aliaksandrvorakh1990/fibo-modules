package by.vorakh.dev.fibo.r2dbc.repository;

import by.vorakh.dev.fibo.base.entity.TaskEntity;
import by.vorakh.dev.fibo.base.entity.TaskStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskRepository {

    Flux<TaskEntity> getAll();

    Mono<@NotNull TaskEntity> create(@NotNull TaskEntity task);

    Mono<@Nullable TaskEntity> getBy(long taskId);

    Mono<Integer> update(
        long taskId,
        long endTime,
        @NotNull TaskStatus status,
        @NotNull String result
    );

    Mono<Integer> update(long taskId, @NotNull TaskStatus status);

    Mono<Integer> delete(long taskId);
}
