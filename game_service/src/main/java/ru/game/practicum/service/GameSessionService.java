package ru.game.practicum.service;

import ru.game.practicum.entity.GameSession;

import java.util.UUID;

public interface GameSessionService {
    GameSession createGameSession(UUID userId);
    GameSession joinGameSession(UUID gameSessionId, UUID userId);
    GameSession startGameSession(UUID gameSessionId, UUID userId);
    void initializeDeck(GameSession gameSession);
    void shuffleDeck(GameSession gameSession);
    GameSession getGameSessionStatus(UUID gameSessionId);
}
