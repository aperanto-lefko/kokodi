package ru.game.practicum.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID uuid) {
        super("ГUser not found, id "+ uuid);
    }
}
