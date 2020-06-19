package by.vorakh.dev.fibo.task_receiver.service.impl;

import by.vorakh.dev.fibo.fibo_counter.repository.TaskRepository;
import by.vorakh.dev.fibo.fibo_counter.repository.entity.TaskEntity;
import by.vorakh.dev.fibo.fibo_counter.service.TaskSolvingService;
import by.vorakh.dev.fibo.task_receiver.exception.IncorrectTimeException;
import by.vorakh.dev.fibo.task_receiver.exception.NoExistTaskException;
import by.vorakh.dev.fibo.task_receiver.exception.NoCompletedTaskException;
import by.vorakh.dev.fibo.task_receiver.model.payload.SequenceSize;
import by.vorakh.dev.fibo.task_receiver.model.response.CreatedTaskViewModel;
import by.vorakh.dev.fibo.task_receiver.model.response.SolvedTaskViewModel;
import by.vorakh.dev.fibo.task_receiver.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Log
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskSolvingService taskSolvingService;

    @Override
    public @NotNull CompletableFuture<CreatedTaskViewModel> createTask(SequenceSize sequenceSize) {

        Timestamp startProcessing = new Timestamp(System.currentTimeMillis());
        return taskRepository.create(new TaskEntity(sequenceSize.getSize(), startProcessing))
            .thenApply(task -> {
                solveTask(task);
                return new CreatedTaskViewModel(task.getId(), task.getStartProcessing());
            });
    }

    private void solveTask(TaskEntity task) {

        taskSolvingService.initializeTask(task.getId(), task.getNumber())
            .thenAccept(aVoid -> {
                Timestamp finishProcessing = new Timestamp(System.currentTimeMillis());
                taskRepository.update(task.getId(), finishProcessing)
                    .thenAccept(aVoid1 -> {
                        long period = getTaskPeriod(finishProcessing, task.getStartProcessing());
                        String endTaskMessage = String.format("Task is solved ID '%s' : Number [%s]: Period [%s]",
                            task.getId(), task.getNumber(), period);
                        log.info(endTaskMessage);
                    });
            });
    }

    @Override
    public CompletableFuture<SolvedTaskViewModel> getTask(long taskId) {

        return taskRepository.getBy(taskId)
            .thenApply(task -> Optional.ofNullable(task).orElseThrow(NoExistTaskException::new))
            .thenApply(task -> Optional.of(task).filter(TaskEntity::isCompleted)
                    .orElseThrow(NoCompletedTaskException::new))
            .thenCombine(taskSolvingService.getResult(taskId), (task, result) -> {
                log.info(task.toString());
                return new SolvedTaskViewModel(result);
            });
    }

    private long getTaskPeriod(Timestamp finishTime, Timestamp startTime) {

        if ((finishTime == null) || (startTime== null)) {
            log.info("startTime:"+startTime);
            log.info("finishTime:"+finishTime);
            throw new IncorrectTimeException("One or two times are empty.");
        }
        long diff = finishTime.getTime() - startTime.getTime();

        return diff / 1000;
    }
}
