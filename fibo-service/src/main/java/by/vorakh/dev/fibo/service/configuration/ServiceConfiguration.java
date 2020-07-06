package by.vorakh.dev.fibo.service.configuration;

import by.vorakh.dev.fibo.base.repository.ProcessingTimeRepository;
import by.vorakh.dev.fibo.jdbc.configuration.FiboJdbcConfiguration;
import by.vorakh.dev.fibo.jdbc.repository.TaskRepository;
import by.vorakh.dev.fibo.service.adapter.AsyncProcessingTimeRepositoryAdapter;
import by.vorakh.dev.fibo.redisson.configuration.RedissonConfiguration;
import by.vorakh.dev.fibo.redisson.repository.ReactiveProcessingTimeRepository;
import by.vorakh.dev.fibo.service.TaskService;
import by.vorakh.dev.fibo.service.impl.TaskServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@PropertySource("classpath:service-executor.properties")
@Import({FiboJdbcConfiguration.class, RedissonConfiguration.class})
public class ServiceConfiguration {

    @Autowired
    private Environment environment;

    @Bean(name = "computeExecutor")
    public @NotNull Executor computeExecutor() {

        int corePoolSize = environment.getRequiredProperty("executor.service.corePoolSize", Integer.class);
        int maxPoolSize = environment.getRequiredProperty("executor.service.maxPoolSize", Integer.class);
        int queueCapacity = environment.getRequiredProperty("executor.service.queueCapacity", Integer.class);
        String threadNamePrefix = environment.getRequiredProperty("executor.service.threadNamePrefix");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();

        return executor;
    }

    @Bean
    @NotNull ProcessingTimeRepository processingTimeRepository(
        @NotNull @Qualifier("redisonProcessingTimeRepository") ReactiveProcessingTimeRepository processingTimeRepository
    ) {
        return new AsyncProcessingTimeRepositoryAdapter(processingTimeRepository);
    }

    @Bean
    public @NotNull TaskService taskService(
        @NotNull TaskRepository taskRepository,
        @NotNull @Qualifier("computeExecutor") Executor computeExecutor,
        @NotNull ProcessingTimeRepository processingTimeRepository
    ) {

        return new TaskServiceImpl(taskRepository, computeExecutor, processingTimeRepository);
    }
}
