package ru.practicum.exceptions;

public class WrongEnumException extends RuntimeException {
    public WrongEnumException(String message) {
        super(message);
    }
}
