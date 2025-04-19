package ru.game.practicum.exception;

import java.util.UUID;

public class GameNotInProgressException extends RuntimeException {
    public GameNotInProgressException(UUID gameId) {
        super("Game " + gameId + " is not in progress");
    }
}
