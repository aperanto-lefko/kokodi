package ru.game.practicum.exception;

import java.util.UUID;

public class NotEnoughPlayersException extends RuntimeException {
  public NotEnoughPlayersException(UUID gameSessionId) {
    super("Not enough players in game session: " + gameSessionId);
  }
}
