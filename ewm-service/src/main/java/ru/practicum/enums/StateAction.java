package ru.practicum.enums;

import ru.practicum.exceptions.WrongEnumException;

public enum StateAction {
    PUBLISH_EVENT,
    REJECT_EVENT,
    CANCEL_REVIEW,
    SEND_TO_REVIEW;

    public static StateAction getStateAction(String stateAction) {
        try {
            return StateAction.valueOf(stateAction.toUpperCase());
        } catch (Exception e) {
            throw new WrongEnumException("Unknown state action: " + stateAction);
        }
    }
}
