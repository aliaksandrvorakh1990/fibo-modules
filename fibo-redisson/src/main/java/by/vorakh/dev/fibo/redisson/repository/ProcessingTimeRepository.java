package by.vorakh.dev.fibo.redisson.repository;

import by.vorakh.dev.fibo.base.entity.ProcessingTime;
import reactor.core.publisher.Mono;

public interface ProcessingTimeRepository {

    Mono<ProcessingTime> getBy(Long taskId);

    Mono<Boolean> add(ProcessingTime newTime);
}
