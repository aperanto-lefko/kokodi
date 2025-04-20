package ru.game.practicum.exception;

public class NotPlayerTurnException extends RuntimeException {
    public NotPlayerTurnException(String userId, int currentPlayerIndex) {
        super("It's not user " + userId + " turn. Current player index: " + currentPlayerIndex);
    }
}
