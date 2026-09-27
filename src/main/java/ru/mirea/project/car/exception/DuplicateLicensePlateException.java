package ru.mirea.project.car.exception;

public class DuplicateLicensePlateException extends IllegalArgumentException{

    public DuplicateLicensePlateException(String message) {
        super(message);
    }
}
