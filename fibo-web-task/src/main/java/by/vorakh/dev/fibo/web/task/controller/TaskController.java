package by.vorakh.dev.fibo.web.task.controller;

import by.vorakh.dev.fibo.base.exception.IncorrectFibonacciSequenceSizeException;
import by.vorakh.dev.fibo.base.model.SequenceSize;
import by.vorakh.dev.fibo.service.TaskService;
import by.vorakh.dev.fibo.service.validation.SequenceSizeValidator;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping("/task")
    public @NotNull CompletableFuture<ResponseEntity<?>> createTask(@RequestBody @NotNull SequenceSize sequenceSize) {

        if (!SequenceSizeValidator.isCorrectSize(sequenceSize)) {
            throw new IncorrectFibonacciSequenceSizeException();
        }

        return service.createTask(sequenceSize)
            .thenApply(createdTask -> new ResponseEntity<>(createdTask, HttpStatus.OK));
    }
}
