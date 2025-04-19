package ru.game.practicum.exception;

import java.util.UUID;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String userId, UUID gameId) {
        super("Player with user id " + userId + " not found in game " + gameId);
    }
}
