package by.vorakh.dev.fibo.task_receiver.service;

import by.vorakh.dev.fibo.task_receiver.model.payload.SequenceSize;
import by.vorakh.dev.fibo.task_receiver.model.response.CreatedTaskViewModel;
import by.vorakh.dev.fibo.task_receiver.model.response.SolvedTaskViewModel;

import java.util.concurrent.CompletableFuture;

public interface TaskService {

    CompletableFuture<CreatedTaskViewModel> createTask(SequenceSize sequenceSize);

    CompletableFuture<SolvedTaskViewModel> getTask(long taskId);
}
