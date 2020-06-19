package by.vorakh.dev.fibo.task_receiver.controller;

import by.vorakh.dev.fibo.task_receiver.exception.IncorrectFibonacciSequenceSizeException;
import by.vorakh.dev.fibo.task_receiver.model.payload.SequenceSize;
import by.vorakh.dev.fibo.task_receiver.model.response.SuccessCreatingTaskResponse;
import by.vorakh.dev.fibo.task_receiver.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static by.vorakh.dev.fibo.task_receiver.validation.SequenceSizeValidator.isCorrectSize;

@Log
@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping("/task")
    public @NotNull CompletableFuture<ResponseEntity<?>> createTask(@RequestBody @NotNull SequenceSize sequenceSize) {

        if (!isCorrectSize(sequenceSize)) {
            throw new IncorrectFibonacciSequenceSizeException(
                "A Fibonacci Sequence Size has to be less than or equals 2000 and be greater  than 0"
            );
        }

        return service.createTask(sequenceSize)
            .thenApply(
                createdTask -> new ResponseEntity<>(
                    new SuccessCreatingTaskResponse(
                        createdTask.getId(),
                        createdTask.getStartProcessing().toString()
                    ),
                    HttpStatus.OK
                )
            );
    }

    @GetMapping("/task/{id}")
    public @NotNull CompletableFuture<ResponseEntity<?>> getTask(@PathVariable("id") long id) {

        return service.getTask(id).thenApply(result -> new ResponseEntity<>(result, HttpStatus.OK));
    }
}
