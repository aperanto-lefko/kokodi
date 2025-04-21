package ru.game.practicum.service;

import ru.game.practicum.entity.ActionCard;
import ru.game.practicum.entity.Card;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;
import ru.game.practicum.entity.PointsCard;

import java.util.UUID;

public interface CardService {

    GameSession applyCardEffect(UUID gameSessionId, UUID userId, Card card);
    void applyPointsCardEffect(Player player, PointsCard card);
    void applyActionCardEffect(GameSession gameSession, Player currentPlayer, ActionCard card);
    void applyBlockEffect(GameSession gameSession);
    void applyStealEffect(GameSession gameSession, Player currentPlayer, int value);
    void applyDoubleDownEffect(Player player);
    void checkWinCondition(GameSession gameSession);
    void moveToNextPlayer(GameSession gameSession);
}
