package ru.mirea.project.car.enums;

public enum CarStatus {
    AVAILABLE,
    IN_USE,
    MAINTENANCE;

    @Override
    public String toString() {
        return switch (this) {
            case AVAILABLE -> "Доступен";
            case IN_USE -> "В эксплуатации";
            case MAINTENANCE -> "На обслуживании";
        };
    }
}
