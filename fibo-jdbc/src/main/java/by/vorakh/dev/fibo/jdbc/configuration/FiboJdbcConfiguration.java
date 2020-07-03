package by.vorakh.dev.fibo.jdbc.configuration;

import by.vorakh.dev.fibo.jdbc.repository.TaskRepository;
import by.vorakh.dev.fibo.jdbc.repository.impl.JdbcTaskRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Import({JdbcConfiguration.class, FlyWayConfiguration.class})
@PropertySource("classpath:repository-executor.properties")
public class FiboJdbcConfiguration {

    @Autowired
    private Environment environment;

    @Bean(name = "repositoryExecutor")
    @NotNull Executor repositoryExecutor() {

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
    @NotNull TaskRepository taskRepository(
        @NotNull JdbcTemplate jdbcTemplate,
        @NotNull @Qualifier("repositoryExecutor") Executor repositoryExecutor
    ) {

        return new JdbcTaskRepository(jdbcTemplate, repositoryExecutor);
    }
}
