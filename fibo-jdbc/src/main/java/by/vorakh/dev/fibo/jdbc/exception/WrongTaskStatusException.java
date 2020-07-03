package by.vorakh.dev.fibo.jdbc.exception;

public class WrongTaskStatusException extends RuntimeException {

    public WrongTaskStatusException() {

    }

    public WrongTaskStatusException(String message) {

        super(message);
    }

    public WrongTaskStatusException(String message, Throwable cause) {

        super(message, cause);
    }
}
