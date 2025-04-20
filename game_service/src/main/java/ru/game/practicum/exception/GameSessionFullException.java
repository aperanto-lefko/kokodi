package ru.game.practicum.exception;

import java.util.UUID;

public class GameSessionFullException extends RuntimeException {
    public GameSessionFullException(UUID gameSessionId) {
        super("Game session is full: " + gameSessionId);
    }
    }

