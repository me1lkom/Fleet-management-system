
package ru.mirea.project.user.exception;

public class UserDataAccessException extends RuntimeException {

    public UserDataAccessException(String message) {
        super(message);
    }

    public UserDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
