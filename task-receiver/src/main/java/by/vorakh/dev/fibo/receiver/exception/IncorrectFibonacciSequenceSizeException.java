package by.vorakh.dev.fibo.receiver.exception;

public class IncorrectFibonacciSequenceSizeException extends RuntimeException {

    public IncorrectFibonacciSequenceSizeException() {

    }

    public IncorrectFibonacciSequenceSizeException(String message) {

        super(message);
    }

    public IncorrectFibonacciSequenceSizeException(String message, Throwable cause) {

        super(message, cause);
    }
}
