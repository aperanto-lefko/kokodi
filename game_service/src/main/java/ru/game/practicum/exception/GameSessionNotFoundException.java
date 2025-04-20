package ru.game.practicum.exception;

import java.util.UUID;

public class GameSessionNotFoundException extends RuntimeException {
    public GameSessionNotFoundException(UUID gameSessionId) {
        super("Game session not found with id: " + gameSessionId);
    }
}
