package ru.game.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.GameState;
import ru.game.practicum.entity.Player;
import ru.game.practicum.exception.GameFullException;
import ru.game.practicum.exception.GameNotFoundException;
import ru.game.practicum.exception.NotEnoughPlayersException;
import ru.game.practicum.repository.GameSessionRepository;
import ru.game.practicum.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameManagementService {
    private final GameSessionRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final DeckGenerator deckGenerator;

    @Override
    public GameSession createGame(String creatorUserId) {
        GameSession game = GameSession.builder()
                .state(GameState.WAITING_FOR_PLAYERS)
                .players(new ArrayList<>())
                .build();

        game = gameRepository.save(game);
        addPlayerInternal(game, creatorUserId);
        return game;
    }

    @Override
    public Player addPlayer(UUID gameId, String userId) throws GameFullException, GameNotFoundException {
        GameSession game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        if (game.getPlayers().size() >= 4) {
            throw new GameFullException(gameId);
        }

        return addPlayerInternal(game, userId);
    }

    @Override
    public GameSession startGame(UUID gameId) throws NotEnoughPlayersException, GameNotFoundException {
        GameSession game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        if (game.getPlayers().size() < 2) {
            throw new NotEnoughPlayersException(gameId);
        }

        game.setDeck(deckGenerator.generateDeck(game));
        game.setState(GameState.IN_PROGRESS);
        game.setCurrentPlayerIndex(0);
        return gameRepository.save(game);
    }

    private Player addPlayerInternal(GameSession game, String userId) {
        Player player = Player.builder()
                .userId(userId)
                .gameSession(game)
                .score(0)
                .blocked(false)
                .build();

        return playerRepository.save(player);
    }
}
