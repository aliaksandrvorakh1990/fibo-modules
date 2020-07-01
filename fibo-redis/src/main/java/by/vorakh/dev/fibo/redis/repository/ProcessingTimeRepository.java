package by.vorakh.dev.fibo.redis.repository;

import by.vorakh.dev.fibo.redis.entity.ProcessingTime;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface ProcessingTimeRepository {

    CompletableFuture<Void> add(@NotNull ProcessingTime newProcessingTime);

    CompletableFuture<@NotNull Long> findProcessingTime(long taskId);
}
