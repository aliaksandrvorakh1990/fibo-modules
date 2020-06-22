package by.vorakh.dev.fibo.fibo_counter.configuration;

import by.vorakh.dev.fibo.fibo_counter.repository.TaskRepository;
import by.vorakh.dev.fibo.fibo_counter.repository.TaskResultRepository;
import by.vorakh.dev.fibo.fibo_counter.repository.impl.JdbcTaskRepository;
import by.vorakh.dev.fibo.fibo_counter.repository.impl.JdbcTaskResultRepository;
import by.vorakh.dev.fibo.fibo_counter.service.TaskSolvingService;
import by.vorakh.dev.fibo.fibo_counter.service.impl.TaskSolvingServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Import({JdbcConfiguration.class, FlyWayConfiguration.class})
public class FiboCounterConfiguration {

    @Bean(name = "repositoryExecutor")
    public Executor repositoryExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("RepositoryTask-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "computeExecutor")
    public Executor computeExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("ComputeTask-");
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskRepository taskRepository(JdbcTemplate jdbcTemplate) {

        return new JdbcTaskRepository(jdbcTemplate, repositoryExecutor());
    }

    @Bean
    public TaskResultRepository taskResultRepository(JdbcTemplate jdbcTemplate) {

        return new JdbcTaskResultRepository(jdbcTemplate, repositoryExecutor());
    }

    @Bean
    public TaskSolvingService taskSolvingService(TaskResultRepository taskResultRepository) {

        return new TaskSolvingServiceImpl(taskResultRepository, computeExecutor());
    }
}
