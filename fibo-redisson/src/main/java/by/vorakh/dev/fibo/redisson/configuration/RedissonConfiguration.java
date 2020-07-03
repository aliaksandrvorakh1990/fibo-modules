package by.vorakh.dev.fibo.redisson.configuration;

import by.vorakh.dev.fibo.base.entity.ProcessingTime;
import by.vorakh.dev.fibo.redisson.repository.ProcessingTimeRepository;
import by.vorakh.dev.fibo.redisson.repository.impl.AsyncProcessingTimeRepository;
import org.jetbrains.annotations.NotNull;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@PropertySource("classpath:redisson.properties")
public class RedissonConfiguration {

    @Autowired
    Environment environment;

    @Bean
    @NotNull ReactiveRedisConnectionFactory redissonConnectionFactory() {

        String address = new StringBuffer(50)
            .append("redis://")
            .append(environment.getRequiredProperty("redisson.host"))
            .append(':')
            .append(environment.getRequiredProperty("redisson.port", Integer.class))
            .toString();

        Config config = new Config();
        config.useSingleServer().setAddress(address);

        return new RedissonConnectionFactory(config);
    }

    @Bean
    ReactiveRedisTemplate<Long, ProcessingTime> redisTemplate(ReactiveRedisConnectionFactory factory) {

        Jackson2JsonRedisSerializer<ProcessingTime> serializer =
            new Jackson2JsonRedisSerializer<>(ProcessingTime.class);

        RedisSerializationContext.RedisSerializationContextBuilder<Long, ProcessingTime> builder =
            RedisSerializationContext.newSerializationContext(new GenericJackson2JsonRedisSerializer());

        RedisSerializationContext<Long, ProcessingTime> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    ProcessingTimeRepository processingTimeRepository(ReactiveRedisTemplate<Long, ProcessingTime> redisTemplate) {

        return new AsyncProcessingTimeRepository(redisTemplate);
    }
}
