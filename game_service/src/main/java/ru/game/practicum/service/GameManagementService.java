package ru.game.practicum.service;

import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;

import java.util.UUID;

public interface GameManagementService {
    GameSession createGame(String creatorUserId);
    Player addPlayer(UUID gameId, String userId) throws GameFullException, GameNotFoundException;
    GameSession startGame(UUID gameId) throws NotEnoughPlayersException, GameNotFoundException;
    GameSession getGameStatus(UUID gameId) throws GameNotFoundException;
}
