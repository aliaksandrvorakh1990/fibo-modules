package by.vorakh.dev.fibo.redis.configuration;

import by.vorakh.dev.fibo.redis.repository.ProcessingTimeRepository;
import by.vorakh.dev.fibo.redis.repository.impl.ProcessingTimeRepositoryImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@PropertySource("classpath:redis.properties")
public class RedisConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    @NotNull JedisConnectionFactory jedisConnectionFactory() {

        String hostName = environment.getProperty("redis.hostName");
        int port = environment.getProperty("redis.port", Integer.class);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(hostName, port);
        JedisConnectionFactory jedisConFactory = new JedisConnectionFactory(config);
        return jedisConFactory;
    }

    @Bean
    public @NotNull RedisTemplate<String, Long> redisTemplate(@NotNull JedisConnectionFactory jedisConnectionFactory) {

        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }

    @Bean
    @NotNull HashOperations<String, Long, Long> operations(@NotNull RedisTemplate<String, Long> redisTemplate) {

        HashOperations<String, Long, Long> ops = redisTemplate.opsForHash();
        return ops;
    }

    @Bean
    @NotNull ProcessingTimeRepository processingTimeRepository(@NotNull HashOperations<String, Long, Long> operations) {

        String key = environment.getProperty("redis.key");
        return new ProcessingTimeRepositoryImpl(operations, key);
    }
}
