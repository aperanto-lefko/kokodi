package ru.game.practicum.service;

import ru.game.practicum.entity.Card;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;
import ru.game.practicum.entity.Turn;
import ru.game.practicum.exception.GameException;
import ru.game.practicum.repository.PlayerRepository;
import ru.game.practicum.repository.TurnRepository;

import java.time.Instant;

public abstract class AbstractTurnProcessor<T extends Card> implements TurnProcessingService<T> {
    protected final PlayerRepository playerRepository;
    protected final TurnRepository turnRepository;

    public AbstractTurnProcessor(PlayerRepository playerRepository, TurnRepository turnRepository) {
        this.playerRepository = playerRepository;
        this.turnRepository = turnRepository;
    }

    @Override
    public Turn processTurn(GameSession game, Player player, T card) throws GameException {
        Turn turn = createTurn(game, player, card);
        applyCardEffects(game, player, card);
        return turnRepository.save(turn);
    }

    protected abstract void applyCardEffects(GameSession game, Player player, T card);

    private Turn createTurn(GameSession game, Player player, Card card) {
        return Turn.builder()
                .gameSession(game)
                .player(player)
                .playedCard(card)
                .timestamp(Instant.now())
                .build();
    }
}
