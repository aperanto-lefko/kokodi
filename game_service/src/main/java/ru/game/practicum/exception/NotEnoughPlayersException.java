package ru.game.practicum.exception;

import java.util.UUID;

public class NotEnoughPlayersException extends RuntimeException {
    public NotEnoughPlayersException(UUID gameId) {
        super("Game " + gameId + " needs at least 2 players to start");
    }
}
