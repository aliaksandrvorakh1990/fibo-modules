package by.vorakh.dev.fibo.web.task.controller.handler;

import by.vorakh.dev.fibo.base.exception.IncorrectFibonacciSequenceSizeException;
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
public class WebTaskExceptionHandler extends ResponseEntityExceptionHandler {

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
}
