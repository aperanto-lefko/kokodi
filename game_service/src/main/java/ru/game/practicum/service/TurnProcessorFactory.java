package ru.game.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.game.practicum.entity.ActionCard;
import ru.game.practicum.entity.Card;
import ru.game.practicum.entity.PointsCard;

@Service
@RequiredArgsConstructor
public class TurnProcessorFactory {
    private final PointsCardProcessor pointsCardProcessor;
    private final ActionCardProcessor actionCardProcessor;

    public TurnProcessingService<? extends Card> getProcessor(Card card) {
        if (card instanceof PointsCard) {
            return pointsCardProcessor;
        } else if (card instanceof ActionCard) {
            return actionCardProcessor;
        }
        throw new IllegalArgumentException("Unsupported card type: " + card.getClass());
    }
}
