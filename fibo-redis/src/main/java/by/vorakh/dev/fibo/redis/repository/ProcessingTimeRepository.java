package by.vorakh.dev.fibo.redis.repository;

import by.vorakh.dev.fibo.redis.entity.ProcessingTime;

import java.util.concurrent.CompletableFuture;

public interface ProcessingTimeRepository {

    CompletableFuture<Void> add(ProcessingTime newProcessingTime);

    CompletableFuture<Long> findProcessingTime(long taskId);
}
