package by.vorakh.dev.fibo.receiver.exception;

public class NoCompletedTaskException extends RuntimeException {

    public NoCompletedTaskException() {

    }

    public NoCompletedTaskException(String message) {

        super(message);
    }

    public NoCompletedTaskException(String message, Throwable cause) {

        super(message, cause);
    }
}
