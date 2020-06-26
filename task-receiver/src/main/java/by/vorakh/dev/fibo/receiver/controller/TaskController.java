package by.vorakh.dev.fibo.receiver.controller;

import by.vorakh.dev.fibo.receiver.exception.IncorrectFibonacciSequenceSizeException;
import by.vorakh.dev.fibo.receiver.model.payload.SequenceSize;
import by.vorakh.dev.fibo.receiver.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static by.vorakh.dev.fibo.receiver.validation.SequenceSizeValidator.isCorrectSize;

@Log
@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping("/task")
    public @NotNull CompletableFuture<ResponseEntity<?>> createTask(@RequestBody @NotNull SequenceSize sequenceSize) {

        if (!isCorrectSize(sequenceSize)) {
            throw new IncorrectFibonacciSequenceSizeException();
        }

        return service.createTask(sequenceSize)
            .thenApply(createdTask -> new ResponseEntity<>(createdTask, HttpStatus.OK));
    }
}
