package by.vorakh.dev.fibo.web.result.controller.handler;

import by.vorakh.dev.fibo.base.exception.NoCompletedTaskException;
import by.vorakh.dev.fibo.base.exception.NoExistTaskException;
import by.vorakh.dev.fibo.base.model.ErrorMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class WebResultExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {NoExistTaskException.class})
    protected @NotNull ResponseEntity<?> handleNoExistTask(@NotNull RuntimeException ex, @NotNull WebRequest request) {

        String bodyOfResponse = "Task with this id is not exist.";
        return handleExceptionInternal(
            ex,
            new ErrorMessage(bodyOfResponse),
            new HttpHeaders(),
            HttpStatus.NOT_FOUND,
            request
        );
    }

    @ExceptionHandler(value = {NoCompletedTaskException.class})
    protected @NotNull ResponseEntity<?> handleNoCompletedTask(
        @NotNull RuntimeException ex,
        @NotNull WebRequest request
    ) {

        return handleExceptionInternal(
            ex,
            null,
            new HttpHeaders(),
            HttpStatus.NO_CONTENT,
            request
        );
    }
}
