package by.vorakh.dev.fibo.fibo_counter.service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TaskSolvingService {

    CompletableFuture<Void> initializeTask(long taskId, int length);

    CompletableFuture<String> getResult(long taskId);

}
