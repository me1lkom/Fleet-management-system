package ru.mirea.project.user.enums;

public enum UserStatus {
    REGULAR,
    VIP;

    @Override
    public String toString() {
        return switch (this) {
            case REGULAR -> "Обычный";
            case VIP -> "VIP";
        };
    }
}