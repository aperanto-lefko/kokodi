package ru.game.practicum.exception;

import java.util.UUID;

public class DeckEmptyException extends RuntimeException {
    public DeckEmptyException(UUID gameId) {
        super("Deck is empty in game " + gameId);
    }
}
