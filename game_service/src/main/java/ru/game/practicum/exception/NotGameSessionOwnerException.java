package ru.game.practicum.exception;

import java.util.UUID;

public class NotGameSessionOwnerException extends RuntimeException {
    public NotGameSessionOwnerException(UUID gameSessionId, String userId) {
        super("User " + userId + " is not owner of game session " + gameSessionId);
    }
}
