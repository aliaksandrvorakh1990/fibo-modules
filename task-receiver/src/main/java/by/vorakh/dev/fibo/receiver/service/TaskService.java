package by.vorakh.dev.fibo.receiver.service;

import by.vorakh.dev.fibo.receiver.model.payload.SequenceSize;
import by.vorakh.dev.fibo.receiver.model.response.CreatedTask;
import by.vorakh.dev.fibo.receiver.model.response.TaskResponse;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface TaskService {

    CompletableFuture<@NotNull CreatedTask> createTask(@NotNull SequenceSize sequenceSize);

    CompletableFuture<@NotNull TaskResponse> getTaskResult(long taskId);
}
