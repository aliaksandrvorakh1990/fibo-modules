package by.vorakh.dev.fibo.task_receiver.configuration;

import by.vorakh.dev.fibo.fibo_counter.configuration.FiboCounterConfiguration;
import by.vorakh.dev.fibo.fibo_counter.repository.TaskRepository;
import by.vorakh.dev.fibo.fibo_counter.service.TaskSolvingService;
import by.vorakh.dev.fibo.task_receiver.service.TaskService;
import by.vorakh.dev.fibo.task_receiver.service.impl.TaskServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@Import(FiboCounterConfiguration.class)
public class AppConfiguration {

    @Bean
    public TaskService taskService(TaskRepository taskRepository, TaskSolvingService taskSolvingService) {

        return new TaskServiceImpl(taskRepository, taskSolvingService);
    }
}
