package ru.game.practicum.exception;

import java.util.UUID;

public class NotPlayerTurnException extends RuntimeException {
    public NotPlayerTurnException(UUID userId, int currentPlayerIndex) {
        super("It's not user " + userId + " turn. Current player index: " + currentPlayerIndex);
    }
}
