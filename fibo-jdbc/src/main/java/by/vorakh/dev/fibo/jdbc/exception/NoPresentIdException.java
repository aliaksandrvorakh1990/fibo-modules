package by.vorakh.dev.fibo.jdbc.exception;

public class NoPresentIdException extends RuntimeException {

    public NoPresentIdException() {

    }

    public NoPresentIdException(String message) {

        super(message);
    }

    public NoPresentIdException(String message, Throwable cause) {

        super(message, cause);
    }
}
