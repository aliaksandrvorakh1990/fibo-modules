package by.vorakh.dev.fibo.redisson.repository.impl;

import by.vorakh.dev.fibo.base.entity.ProcessingTime;
import by.vorakh.dev.fibo.redisson.repository.ProcessingTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AsyncProcessingTimeRepository implements ProcessingTimeRepository {

    private final ReactiveRedisTemplate<Long, ProcessingTime> redisTemplate;

    @Override
    public Mono<ProcessingTime> getBy(Long taskId) {

        return redisTemplate.opsForValue().get(taskId);
    }

    @Override
    public Mono<Boolean> add(ProcessingTime newTime) {

        return redisTemplate.opsForValue().set(newTime.getId(), newTime);
    }
}
