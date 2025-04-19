package ru.game.practicum.service;

import org.springframework.stereotype.Service;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;
import ru.game.practicum.entity.PointsCard;
import ru.game.practicum.repository.PlayerRepository;
import ru.game.practicum.repository.TurnRepository;
@Service
public class PointsCardProcessor extends AbstractTurnProcessor<PointsCard> implements CardEffectService<PointsCard> {

    public PointsCardProcessor(PlayerRepository playerRepository, TurnRepository turnRepository) {
        super(playerRepository, turnRepository);
    }

    @Override
    protected void applyCardEffects(GameSession game, Player player, PointsCard card) {
        player.setScore(player.getScore() + card.getValue());
        playerRepository.save(player);
    }

    @Override
    public void applyEffect(GameSession game, Player currentPlayer, PointsCard card) {
        applyCardEffects(game, currentPlayer, card);
    }
}
