package by.vorakh.dev.fibo.fibo_counter.service.impl;

import by.vorakh.dev.fibo.fibo_counter.repository.TaskResultRepository;
import by.vorakh.dev.fibo.fibo_counter.repository.entity.TaskResultEntity;
import by.vorakh.dev.fibo.fibo_counter.service.TaskSolvingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
@RequiredArgsConstructor
public class TaskSolvingServiceImpl implements TaskSolvingService {

    private final TaskResultRepository taskResultRepository;
    private final Executor serviceExecutor;

    @Override
    public @NotNull CompletableFuture<Void> initializeTask(long taskId, int length) {

        return CompletableFuture.supplyAsync(() -> createFibonacciLine(length), serviceExecutor)
            .thenAccept(result -> taskResultRepository.create(new TaskResultEntity(taskId, result)));
    }

    @Override
    public CompletableFuture<String> getResult(long taskId) {

        return taskResultRepository.getResultBy(taskId);
    }

    private @NotNull String createFibonacciLine(int length) {

        return Stream.iterate(new BigInteger[]{BigInteger.valueOf(0), BigInteger.valueOf(1)},
            fibonaccis -> {
                try {
                    Thread.sleep(2500L);
                } catch (InterruptedException e) {
                    log.info(e.getMessage());
                }
                return new BigInteger[]{fibonaccis[1], fibonaccis[0].add(fibonaccis[1])};
            })
            .limit(length)
            .map(t -> t[0])
            .map(BigInteger::toString)
            .collect(Collectors.joining(", "));
    }
}
