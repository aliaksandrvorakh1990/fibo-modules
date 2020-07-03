package by.vorakh.dev.fibo.base.exception;

public class ImpossibleSolvingTaskException extends RuntimeException {

    public ImpossibleSolvingTaskException() {

    }

    public ImpossibleSolvingTaskException(String message) {

        super(message);
    }

    public ImpossibleSolvingTaskException(String message, Throwable cause) {

        super(message, cause);
    }
}
