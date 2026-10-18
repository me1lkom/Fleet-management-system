package ru.mirea.project.request.exception;

public class RequestNotFoundException extends RuntimeException {

    public RequestNotFoundException(Long id) {
        super("Заявка с ID " + id + " не найдена!");
    }

    public RequestNotFoundException(String message) {
        super(message);
    }
}
