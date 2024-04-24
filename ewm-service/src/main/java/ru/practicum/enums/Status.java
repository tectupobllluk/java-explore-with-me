package ru.practicum.enums;

import ru.practicum.exceptions.WrongEnumException;

public enum Status {
    PENDING,
    CONFIRMED,
    CANCELED,
    REJECTED;

    public static Status getStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new WrongEnumException("Wrong status value: " + status);
        }
    }
}
