package by.vorakh.dev.fibo.service.adapter;

import by.vorakh.dev.fibo.base.entity.ProcessingTime;
import by.vorakh.dev.fibo.base.exception.RedisValueAddingException;
import by.vorakh.dev.fibo.base.repository.ProcessingTimeRepository;
import by.vorakh.dev.fibo.redisson.repository.ReactiveProcessingTimeRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class AsyncProcessingTimeRepositoryAdapter implements ProcessingTimeRepository {

    private final ReactiveProcessingTimeRepository reactiveRepository;

    @Override
    public CompletableFuture<Void> add(@NotNull ProcessingTime newProcessingTime) {

        return reactiveRepository.add(newProcessingTime).toFuture()
            .thenAccept(isAdded -> {
                if (!isAdded) {
                    throw new RedisValueAddingException("This value cannot be added in Redis");
                }
            });
    }

    @Override
    public CompletableFuture<@Nullable Long> findProcessingTime(long taskId) {

        return reactiveRepository.getBy(taskId)
            .map(processingTime -> Optional.ofNullable(processingTime)
                .filter(Objects::nonNull)
                .map(ProcessingTime::getTime)
                .orElse(null))
            .toFuture();
    }
}
