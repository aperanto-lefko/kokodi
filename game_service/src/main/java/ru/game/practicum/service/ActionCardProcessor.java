package ru.game.practicum.service;

import ru.game.practicum.card_strategy.CardEffectStrategy;
import ru.game.practicum.entity.ActionCard;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;
import ru.game.practicum.repository.PlayerRepository;
import ru.game.practicum.repository.TurnRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ActionCardProcessor extends AbstractTurnProcessor<ActionCard>
        implements CardEffectService<ActionCard> {

    private final Map<String, CardEffectStrategy> effectStrategies;

    public ActionCardProcessor(PlayerRepository playerRepository,
                               TurnRepository turnRepository,
                               List<CardEffectStrategy> strategies) {
        super(playerRepository, turnRepository);
        this.effectStrategies = strategies.stream()
                .collect(Collectors.toMap(
                        CardEffectStrategy::getSupportedCardType,
                        Function.identity()
                ));
    }

    @Override
    protected void applyCardEffects(GameSession game, Player player, ActionCard card) {
        CardEffectStrategy strategy = effectStrategies.get(card.getName());
        if (strategy != null) {
            strategy.applyEffect(game, player, card);
        }
    }
}
