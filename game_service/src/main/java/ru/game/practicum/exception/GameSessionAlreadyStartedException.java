package ru.game.practicum.exception;

import java.util.UUID;

public class GameSessionAlreadyStartedException extends RuntimeException {
    public GameSessionAlreadyStartedException(UUID gameSessionId) {
        super("Game session already started: " + gameSessionId);
    }
}
