package by.vorakh.dev.fibo.redis.repository.impl;

import by.vorakh.dev.fibo.redis.entity.ProcessingTime;
import by.vorakh.dev.fibo.redis.repository.ProcessingTimeRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.HashOperations;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ProcessingTimeRepositoryImpl implements ProcessingTimeRepository {

    private final HashOperations<String, Long, Long> operations;

    private final String KEY;

    @Override
    public CompletableFuture<Void> add(@NotNull ProcessingTime newProcessingTime) {

        operations.put(KEY, newProcessingTime.getTaskId(), newProcessingTime.getTimeInMillis());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<@NotNull Long> findProcessingTime(long taskId) {

        return CompletableFuture.completedFuture(operations.get(KEY, taskId));
    }
}
