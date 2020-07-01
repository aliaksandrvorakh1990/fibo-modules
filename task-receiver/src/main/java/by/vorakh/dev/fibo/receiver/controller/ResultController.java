package by.vorakh.dev.fibo.receiver.controller;

import by.vorakh.dev.fibo.receiver.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class ResultController {

    private final TaskService service;

    @GetMapping("/result/{id}")
    public @NotNull CompletableFuture<ResponseEntity<?>> getResult(@PathVariable("id") long id) {

        return service.getTaskResult(id)
            .thenApply(result -> new ResponseEntity<>(result, HttpStatus.OK));
    }
}
