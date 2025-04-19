package ru.game.practicum.exception;

import java.util.UUID;

public class GameFullException extends RuntimeException {
    public GameFullException(UUID gameId) {
        super("Game " + gameId + " is full (max 4 players)");
    }
}
