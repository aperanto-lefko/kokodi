package ru.game.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.game.practicum.entity.ActionCard;
import ru.game.practicum.entity.Card;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.GameState;
import ru.game.practicum.entity.Player;
import ru.game.practicum.entity.PointsCard;
import ru.game.practicum.exception.GameSessionAlreadyStartedException;
import ru.game.practicum.exception.GameSessionFullException;
import ru.game.practicum.exception.GameSessionNotFoundException;
import ru.game.practicum.exception.NotEnoughPlayersException;
import ru.game.practicum.exception.NotGameSessionOwnerException;
import ru.game.practicum.repository.CardRepository;
import ru.game.practicum.repository.GameSessionRepository;
import ru.game.practicum.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameSessionService { //управление игровыми сессиями
    GameSessionRepository gameSessionRepository;
    PlayerRepository playerRepository;
    CardRepository cardRepository;
    AuthServiceClient authServiceClient;

    public GameSession createGameSession(String userId) {
        UserDto user = authServiceClient.getUser(userId);

        GameSession gameSession = GameSession.builder()
                .state(GameState.WAITING_FOR_PLAYERS)
                .players(new ArrayList<>())
                .deck(new ArrayList<>())
                .turns(new ArrayList<>())
                .currentPlayerIndex(0)
                .build();

        Player creator = Player.builder()
                .userId(userId)
                .gameSession(gameSession)
                .score(0)
                .blocked(false)
                .build();

        gameSession.getPlayers().add(creator);
        return gameSessionRepository.save(gameSession);
    }

    public GameSession joinGameSession(UUID gameSessionId, String userId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new GameSessionNotFoundException(gameSessionId));

        if (gameSession.getPlayers().size() >= 4) {
            throw new GameSessionFullException(gameSessionId);
        }

        if (gameSession.getState() != GameState.WAITING_FOR_PLAYERS) {
            throw new GameSessionAlreadyStartedException(gameSessionId);
        }

        UserDto user = authServiceClient.getUser(userId);

        Player player = Player.builder()
                .userId(userId)
                .gameSession(gameSession)
                .score(0)
                .blocked(false)
                .build();

        gameSession.getPlayers().add(player);
        return gameSessionRepository.save(gameSession);
    }

    public GameSession startGameSession(UUID gameSessionId, String userId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new GameSessionNotFoundException(gameSessionId));

        if (!gameSession.getPlayers().get(0).getUserId().equals(userId)) {
            throw new NotGameSessionOwnerException(gameSessionId, userId);
        }

        if (gameSession.getPlayers().size() < 2) {
            throw new NotEnoughPlayersException(gameSessionId);
        }

        gameSession.setState(GameState.IN_PROGRESS);
        initializeDeck(gameSession);
        shuffleDeck(gameSession);

        return gameSessionRepository.save(gameSession);
    }

    private void initializeDeck(GameSession gameSession) {
        List<Card> deck = new ArrayList<>();

        // Points Cards
        for (int i = 0; i < 10; i++) {
            deck.add(PointsCard.builder()
                    .name("Points-" + (i + 1))
                    .value(i + 1)
                    .gameSession(gameSession)
                    .build());
        }

        // Action Cards
        deck.add(ActionCard.builder()
                .name("Block")
                .value(1)
                .gameSession(gameSession)
                .build());

        deck.add(ActionCard.builder()
                .name("Steal-3")
                .value(3)
                .gameSession(gameSession)
                .build());

        deck.add(ActionCard.builder()
                .name("DoubleDown")
                .value(2)
                .gameSession(gameSession)
                .build());

        gameSession.setDeck(deck);
    }

    private void shuffleDeck(GameSession gameSession) {
        Collections.shuffle(gameSession.getDeck());
    }

    public GameSession getGameSessionStatus(UUID gameSessionId) {
        return gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new GameSessionNotFoundException(gameSessionId));
    }
}
