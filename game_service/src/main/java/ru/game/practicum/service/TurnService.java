package ru.game.practicum.service;

import ru.game.practicum.entity.TurnResult;

import java.util.UUID;

public interface TurnService {
    TurnResult makeTurn(UUID gameSessionId, UUID userId);
}
