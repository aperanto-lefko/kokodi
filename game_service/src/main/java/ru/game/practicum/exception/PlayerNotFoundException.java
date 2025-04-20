package ru.game.practicum.exception;

public class PlayerNotFoundException extends RuntimeException {
  public PlayerNotFoundException(String userId) {
    super("Player not found with user id: " + userId);
  }
}
