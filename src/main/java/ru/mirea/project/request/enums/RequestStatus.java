package ru.mirea.project.request.enums;

public enum RequestStatus {
    NEW,
    APPROVED,
    ACTIVE,
    COMPLETED,
    CANCELLED;

    @Override
    public String toString() {
        return switch (this) {
            case NEW -> "Новая";
            case APPROVED -> "Одобрена";
            case ACTIVE -> "Активна";
            case COMPLETED -> "Завершена";
            case CANCELLED -> "Отменена";
        };
    }
}
