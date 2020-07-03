package by.vorakh.dev.fibo.base.exception;

public class IncorrectTimeException extends RuntimeException {

    public IncorrectTimeException() {

    }

    public IncorrectTimeException(String message) {

        super(message);
    }

    public IncorrectTimeException(String message, Throwable cause) {

        super(message, cause);
    }
}
