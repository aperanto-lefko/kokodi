package ru.game.practicum.exception;

import java.util.UUID;

public class PlayerNotFoundException extends RuntimeException {
  public PlayerNotFoundException(UUID userId) {
    super("Player not found with user id: " + userId);
  }
}
