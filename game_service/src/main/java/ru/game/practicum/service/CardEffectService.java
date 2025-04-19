package ru.game.practicum.service;

import ru.game.practicum.entity.Card;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;

public interface CardEffectService<T extends Card> {
    void applyEffect(GameSession game, Player currentPlayer, T card);
}
