package ru.mirea.project.car.enums;

public enum BodyType {
    HATCHBACK,
    SEDAN,
    COUPE,
    LIFTBACK,
    CROSSOVER,
    VAN,
    CONVERTIBLE,
    PICKUP;

    @Override
    public String toString() {
        return switch (this) {
            case HATCHBACK -> "Хэтчбек";
            case SEDAN -> "Седан";
            case COUPE -> "Купе";
            case LIFTBACK -> "Лифтбек";
            case CROSSOVER -> "Кроссовер";
            case VAN -> "Фургон";
            case CONVERTIBLE -> "Кабриолет";
            case PICKUP -> "Пикап";
        };
    }
}

