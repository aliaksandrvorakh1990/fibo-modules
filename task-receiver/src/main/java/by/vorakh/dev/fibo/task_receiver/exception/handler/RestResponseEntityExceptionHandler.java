package by.vorakh.dev.fibo.task_receiver.exception.handler;

import by.vorakh.dev.fibo.task_receiver.exception.IncorrectFibonacciSequenceSizeException;
import by.vorakh.dev.fibo.task_receiver.exception.NoCompletedTaskException;
import by.vorakh.dev.fibo.task_receiver.exception.NoExistTaskException;
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
    protected ResponseEntity<?> handleIncorrectSequenceSize(RuntimeException ex, WebRequest request) {

        String bodyOfResponse = "N should be less than or equals 2000 and be greater than 0";
        return handleExceptionInternal(
            ex,
            bodyOfResponse,
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @ExceptionHandler(value = {NoExistTaskException.class})
    protected ResponseEntity<?> handleNoExistTask(RuntimeException ex, WebRequest request) {

        String bodyOfResponse = "Task with this id is not exist.";
        return handleExceptionInternal(
            ex,
            bodyOfResponse,
            new HttpHeaders(),
            HttpStatus.NOT_FOUND,
            request
        );
    }

    @ExceptionHandler(value = {NoCompletedTaskException.class})
    protected ResponseEntity<?> handleNoCompletedTask(RuntimeException ex, WebRequest request) {

        String bodyOfResponse = "";
        return handleExceptionInternal(
            ex,
            bodyOfResponse,
            new HttpHeaders(),
            HttpStatus.NO_CONTENT,
            request
        );
    }
}
