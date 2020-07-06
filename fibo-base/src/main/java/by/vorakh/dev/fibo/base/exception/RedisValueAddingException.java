package by.vorakh.dev.fibo.base.exception;

public class RedisValueAddingException extends RuntimeException {

    public RedisValueAddingException() {

    }

    public RedisValueAddingException(String message) {

        super(message);
    }

    public RedisValueAddingException(String message, Throwable cause) {

        super(message, cause);
    }
}
