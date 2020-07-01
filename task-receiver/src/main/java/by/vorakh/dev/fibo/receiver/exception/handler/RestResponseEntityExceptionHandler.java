package by.vorakh.dev.fibo.receiver.exception.handler;

import by.vorakh.dev.fibo.receiver.exception.IncorrectFibonacciSequenceSizeException;
import by.vorakh.dev.fibo.receiver.exception.NoCompletedTaskException;
import by.vorakh.dev.fibo.receiver.exception.NoExistTaskException;
import by.vorakh.dev.fibo.receiver.model.response.ErrorMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IncorrectFibonacciSequenceSizeException.class})
    protected @NotNull ResponseEntity<?> handleIncorrectSequenceSize(
        @NotNull RuntimeException ex,
        @NotNull WebRequest request
    ) {

        String bodyOfResponse = "N should be less than or equals 2000 and be greater than 0";
        return handleExceptionInternal(
            ex,
            new ErrorMessage(bodyOfResponse),
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        );
    }

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
