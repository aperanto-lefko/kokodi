package ru.game.practicum.exception;

public class NoPlayersToStealFromException extends RuntimeException {
    public NoPlayersToStealFromException() {
        super("No players to steal from");
    }
}
