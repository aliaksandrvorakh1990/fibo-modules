package by.vorakh.dev.fibo.base.repository;

import by.vorakh.dev.fibo.base.entity.ProcessingTime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface ProcessingTimeRepository {

    CompletableFuture<Void> add(@NotNull ProcessingTime newProcessingTime);

    CompletableFuture<@Nullable Long> findProcessingTime(long taskId);
}
