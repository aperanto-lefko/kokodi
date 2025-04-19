package ru.game.practicum.card_strategy;

import org.springframework.stereotype.Service;
import ru.game.practicum.entity.ActionCard;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;

@Service
public class BlockCardStrategy implements CardEffectStrategy {
    @Override
    public String getSupportedCardType() {
        return "Block";
    }

    @Override
    public void applyEffect(GameSession game, Player currentPlayer, ActionCard card) {
        int nextPlayerIndex = (game.getCurrentPlayerIndex() + 1) % game.getPlayers().size();
        Player nextPlayer = game.getPlayers().get(nextPlayerIndex);
        nextPlayer.setBlocked(true);
    }
}
