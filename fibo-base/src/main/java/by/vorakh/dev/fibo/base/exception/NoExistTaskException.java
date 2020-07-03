package by.vorakh.dev.fibo.base.exception;

public class NoExistTaskException extends RuntimeException {

    public NoExistTaskException() {

    }

    public NoExistTaskException(String message) {

        super(message);
    }

    public NoExistTaskException(String message, Throwable cause) {

        super(message, cause);
    }
}
