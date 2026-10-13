package ru.mirea.project.car.enums;

public enum Transmission {
    AUTOMATIC,
    MANUAL,
    CVT;

    @Override
    public String toString() {
        return switch (this) {
            case AUTOMATIC -> "Автоматическая";
            case MANUAL -> "Механическая";
            case CVT -> "Вариатор";
        };
    }
}
