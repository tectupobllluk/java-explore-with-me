package ru.practicum.enums;

import ru.practicum.exceptions.WrongEnumException;

public enum State {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static State getState(String state) {
        try {
            return State.valueOf(state.toUpperCase());
        } catch (Exception e) {
            throw new WrongEnumException("Wrong state: " + state);
        }
    }
}
