package ru.game.practicum.exception;

import java.util.UUID;

public class GameSessionNotInProgressException extends RuntimeException {
    public GameSessionNotInProgressException(UUID gameSessionId) {
        super("Game session not in progress: " + gameSessionId);
    }
}
