package by.vorakh.dev.fibo.receiver.service.impl;

import by.vorakh.dev.fibo.counter.repository.TaskRepository;
import by.vorakh.dev.fibo.counter.repository.entity.TaskEntity;
import by.vorakh.dev.fibo.counter.repository.entity.TaskStatus;
import by.vorakh.dev.fibo.receiver.exception.ImpossibleSolvingTaskException;
import by.vorakh.dev.fibo.receiver.exception.NoCompletedTaskException;
import by.vorakh.dev.fibo.receiver.exception.NoExistTaskException;
import by.vorakh.dev.fibo.receiver.model.payload.SequenceSize;
import by.vorakh.dev.fibo.receiver.model.response.CreatedTask;
import by.vorakh.dev.fibo.receiver.model.response.TaskResponse;
import by.vorakh.dev.fibo.receiver.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Log
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final Executor serviceExecutor;

    @Override
    public CompletableFuture<@NotNull CreatedTask> createTask(@NotNull SequenceSize sequenceSize) {

        long startProcessing = System.currentTimeMillis();
        return taskRepository.create(new TaskEntity(sequenceSize.getSize(), startProcessing))
            .thenApply(task -> {
                solveTask(task);
                return new CreatedTask(task.getId(), new Timestamp(task.getStartProcessing()));
            });
    }

    private void solveTask(@NotNull TaskEntity task) {

        long taskId = task.getId();

        supplyAsync(() -> createFibonacciLine(task.getNumber()), serviceExecutor)
            .thenCombine(taskRepository.update(taskId, TaskStatus.PROCESSING), (result, avoid) -> result)
            .handle((result, throwable) -> {
                if ((throwable != null) || (result == null)) {
                    taskRepository.update(taskId, TaskStatus.FAILED);
                    log.info("Task " + taskId + "was failed");
                    throw new ImpossibleSolvingTaskException();
                }
                return result;
            })
            .thenAccept(result -> {
                long finishProcessing = System.currentTimeMillis();
                taskRepository.update(taskId, finishProcessing, TaskStatus.COMPLETED, result)
                    .handle((aVoid, throwable) -> {
                        if (throwable != null) {
                            log.info(throwable.getMessage());
                        }
                        log.info("Task is solved in " + new Timestamp(finishProcessing));
                        return null;
                    });
            });
    }

    @Override
    public CompletableFuture<@NotNull TaskResponse> getTaskResult(long taskId) {

        return taskRepository.getBy(taskId)
            .thenApply(task -> {
                if (task == null) {
                    throw new NoExistTaskException();
                }
                if (task.getStatus() == TaskStatus.PROCESSING) {
                    throw new NoCompletedTaskException();
                }
                return new TaskResponse(task.getStatus(), task.getResult());
            });
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
