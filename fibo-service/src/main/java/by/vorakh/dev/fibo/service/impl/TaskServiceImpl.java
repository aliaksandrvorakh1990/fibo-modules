package by.vorakh.dev.fibo.service.impl;

import by.vorakh.dev.fibo.base.entity.ProcessingTime;
import by.vorakh.dev.fibo.base.entity.TaskEntity;
import by.vorakh.dev.fibo.base.entity.TaskStatus;
import by.vorakh.dev.fibo.base.exception.ImpossibleSolvingTaskException;
import by.vorakh.dev.fibo.base.exception.NoCompletedTaskException;
import by.vorakh.dev.fibo.base.exception.NoExistTaskException;
import by.vorakh.dev.fibo.base.model.CreationResponse;
import by.vorakh.dev.fibo.base.model.ResultResponse;
import by.vorakh.dev.fibo.base.model.SequenceSize;
import by.vorakh.dev.fibo.base.repository.ProcessingTimeRepository;
import by.vorakh.dev.fibo.jdbc.repository.TaskRepository;
import by.vorakh.dev.fibo.service.TaskService;
import by.vorakh.dev.fibo.service.converter.MillisToTimeFormatConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static by.vorakh.dev.fibo.service.converter.TimeInMillisToUtcDateTimeConverter.convertUtcDateTimeFormat;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@Log4j2
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final Executor serviceExecutor;
    private final ProcessingTimeRepository processingTimeRepository;

    @Override
    public CompletableFuture<@NotNull CreationResponse> createTask(@NotNull SequenceSize sequenceSize) {

        long creationTime = System.currentTimeMillis();
        return taskRepository.create(new TaskEntity(sequenceSize.getSize(), creationTime))
            .thenApply(task -> {
                solveTask(task);
                return new CreationResponse(task.getId(), convertUtcDateTimeFormat(creationTime));
            });
    }

    private void solveTask(@NotNull TaskEntity task) {

        long taskId = task.getId();

        supplyAsync(() -> createFibonacciLine(task.getNumber()), serviceExecutor)
            .thenCombine(taskRepository.update(taskId, TaskStatus.PROCESSING), (result, avoid) -> result)
            .handle((result, throwable) -> {
                if ((throwable != null) || (result == null)) {
                log.error("Task {} was failed", taskId);
                    taskRepository.update(taskId, TaskStatus.FAILED);
                    throw new ImpossibleSolvingTaskException();
                }
                return result;
            })
            .thenAccept(result -> {
                long endTime = System.currentTimeMillis();
                long time = endTime - task.getCreationTime();

                CompletableFuture.allOf(
                    taskRepository.update(taskId, endTime, TaskStatus.COMPLETED, result),
                    processingTimeRepository.add(new ProcessingTime(taskId, time))
                ).handle((aVoid, throwable) -> {
                    if (throwable != null) {
                        log.error(throwable.getMessage());
                    }
                    log.info("The '{}' task Task is solved at {}", taskId, convertUtcDateTimeFormat(endTime));
                    return null;
                });
            });
    }

    @Override
    public CompletableFuture<@NotNull ResultResponse> getTaskResult(long taskId) {

        return taskRepository.getBy(taskId).thenApply(task -> {
            if (task == null) {
                throw new NoExistTaskException();
            }
            if ((task.getStatus() == TaskStatus.CREATED) || (task.getStatus() == TaskStatus.PROCESSING)) {
                throw new NoCompletedTaskException();
            }

            Long time = processingTimeRepository.findProcessingTime(taskId)
                .exceptionally(throwable -> {
                    log.error("Failed to get execution time from redis for task {}", taskId);
                    return null;
                }).join();

            String processingTime = Optional.ofNullable(time)
                .filter(aTime -> (aTime > 0L))
                .map(MillisToTimeFormatConverter::convert)
                .orElse("No data");
            String result = Optional.ofNullable(task.getResult()).orElse("No data");
            return new ResultResponse(task.getStatus(), result, processingTime);
        });
    }

    private @NotNull String createFibonacciLine(int length) {

        return Stream.iterate(new BigInteger[]{BigInteger.valueOf(0), BigInteger.valueOf(1)},
            fibonaccis -> {
                try {
                    Thread.sleep(2500L);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
                return new BigInteger[]{fibonaccis[1], fibonaccis[0].add(fibonaccis[1])};
            })
            .limit(length)
            .map(t -> t[0])
            .map(BigInteger::toString)
            .collect(Collectors.joining(", "));
    }
}
