package ru.game.practicum.card_strategy;

import ru.game.practicum.entity.ActionCard;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;

public interface CardEffectStrategy {
    String getSupportedCardType();
    void applyEffect(GameSession game, Player currentPlayer, ActionCard card);
}
