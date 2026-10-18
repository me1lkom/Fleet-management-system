package ru.mirea.project.request.exception;

public class RequestDataAccessException extends RuntimeException {

    public RequestDataAccessException(String message) {
        super(message);
    }

    public RequestDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
