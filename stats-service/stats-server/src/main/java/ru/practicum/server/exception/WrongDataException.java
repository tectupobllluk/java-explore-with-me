package ru.practicum.server.exception;

public class WrongDataException extends RuntimeException {

    public WrongDataException(String message) {
        super(message);
    }
}
