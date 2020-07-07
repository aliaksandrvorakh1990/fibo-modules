package by.vorakh.dev.fibo.r2dbc.exception;

public class NotR2dbcConnectionException extends RuntimeException {

    public NotR2dbcConnectionException() {

    }

    public NotR2dbcConnectionException(String message) {

        super(message);
    }

    public NotR2dbcConnectionException(String message, Throwable cause) {

        super(message, cause);
    }
}
