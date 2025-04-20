package ru.game.practicum.exception;

public class UnknownCardTypeException extends RuntimeException {
    public UnknownCardTypeException(String cardName) {
        super("Unknown card type: " + cardName);
    }
}
