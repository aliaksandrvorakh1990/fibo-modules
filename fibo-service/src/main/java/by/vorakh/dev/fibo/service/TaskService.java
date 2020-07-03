package by.vorakh.dev.fibo.service;

import by.vorakh.dev.fibo.base.model.SequenceSize;
import by.vorakh.dev.fibo.base.model.CreationResponse;
import by.vorakh.dev.fibo.base.model.ResultResponse;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface TaskService {

    CompletableFuture<@NotNull CreationResponse> createTask(@NotNull SequenceSize sequenceSize);

    CompletableFuture<@NotNull ResultResponse> getTaskResult(long taskId);
}
