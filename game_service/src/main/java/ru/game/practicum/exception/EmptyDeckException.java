package ru.game.practicum.exception;

import java.util.UUID;

public class EmptyDeckException extends RuntimeException {
    public EmptyDeckException(UUID gameSessionId) {
        super("Deck is empty in game session: " + gameSessionId);
    }
}
