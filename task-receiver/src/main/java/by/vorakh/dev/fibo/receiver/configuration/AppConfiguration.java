package by.vorakh.dev.fibo.receiver.configuration;

import by.vorakh.dev.fibo.counter.configuration.FiboCounterConfiguration;
import by.vorakh.dev.fibo.counter.repository.TaskRepository;
import by.vorakh.dev.fibo.receiver.service.TaskService;
import by.vorakh.dev.fibo.receiver.service.impl.TaskServiceImpl;
import by.vorakh.dev.fibo.redis.repository.ProcessingTimeRepository;
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
@Import(FiboCounterConfiguration.class)
public class AppConfiguration {

    @Autowired
    private Environment environment;

    @Bean(name = "computeExecutor")
    public @NotNull Executor computeExecutor() {

        int corePoolSize = environment.getProperty("executor.service.corePoolSize", Integer.class);
        int maxPoolSize = environment.getProperty("executor.service.maxPoolSize", Integer.class);
        int queueCapacity = environment.getProperty("executor.service.queueCapacity", Integer.class);
        String threadNamePrefix = environment.getProperty("executor.service.threadNamePrefix");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();

        return executor;
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
