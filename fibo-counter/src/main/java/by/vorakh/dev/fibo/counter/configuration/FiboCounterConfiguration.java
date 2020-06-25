package by.vorakh.dev.fibo.counter.configuration;

import by.vorakh.dev.fibo.counter.repository.TaskRepository;
import by.vorakh.dev.fibo.counter.repository.impl.JdbcTaskRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@Import({JdbcConfiguration.class, FlyWayConfiguration.class})
@PropertySource("classpath:executor.properties")
public class FiboCounterConfiguration {

    @Autowired
    private Environment environment;

    @Bean(name = "repositoryExecutor")
    public Executor repositoryExecutor() {

        int corePoolSize = environment.getProperty("executor.repository.corePoolSize", Integer.class);
        int maxPoolSize = environment.getProperty("executor.repository.maxPoolSize", Integer.class);
        int queueCapacity = environment.getProperty("executor.repository.queueCapacity", Integer.class);
        String threadNamePrefix = environment.getProperty("executor.repository.threadNamePrefix");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();

        return executor;
    }

    @Bean
    public TaskRepository taskRepository(@NotNull JdbcTemplate jdbcTemplate) {

        return new JdbcTaskRepository(jdbcTemplate, repositoryExecutor());
    }
}
