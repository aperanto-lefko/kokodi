package ru.game.practicum.card_strategy;

import org.springframework.stereotype.Service;
import ru.game.practicum.entity.ActionCard;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class StealCardStrategy implements CardEffectStrategy{
    @Override
    public String getSupportedCardType() {
        return "Steal";
    }

    @Override
    public void applyEffect(GameSession game, Player currentPlayer, ActionCard card) {
        List<Player> opponents = game.getPlayers().stream()
                .filter(p -> !p.getId().equals(currentPlayer.getId()))
                .toList();

        if (!opponents.isEmpty()) {
            Player target = opponents.get(new Random().nextInt(opponents.size()));
            int stealAmount = Math.min(card.getValue(), target.getScore());
            target.setScore(target.getScore() - stealAmount);
            currentPlayer.setScore(currentPlayer.getScore() + stealAmount);
        }
    }
}
