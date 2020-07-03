package by.vorakh.dev.fibo.base.exception;

public class NotProcessingTimeException extends RuntimeException {

    public NotProcessingTimeException() {

    }

    public NotProcessingTimeException(String message) {

        super(message);
    }

    public NotProcessingTimeException(String message, Throwable cause) {

        super(message, cause);
    }
}
