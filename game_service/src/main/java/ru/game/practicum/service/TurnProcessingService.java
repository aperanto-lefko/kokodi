package ru.game.practicum.service;

import ru.game.practicum.entity.Card;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;
import ru.game.practicum.entity.Turn;
import ru.game.practicum.exception.GameException;

public interface TurnProcessingService<T extends Card> {
    Turn processTurn(GameSession game, Player player, T card) throws GameException;
    boolean checkWinCondition(Player player);
}
