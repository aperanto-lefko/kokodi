import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.game.practicum.GameServiceApp;
import ru.game.practicum.dto.game_service.GameState;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;
import ru.game.practicum.entity.PointsCard;
import ru.game.practicum.entity.Turn;
import ru.game.practicum.entity.TurnResult;
import ru.game.practicum.exception.EmptyDeckException;
import ru.game.practicum.exception.GameSessionNotInProgressException;
import ru.game.practicum.exception.NotPlayerTurnException;
import ru.game.practicum.repository.GameSessionRepository;
import ru.game.practicum.repository.PlayerRepository;
import ru.game.practicum.repository.TurnRepository;
import ru.game.practicum.service.CardService;
import ru.game.practicum.service.CardServiceImpl;
import ru.game.practicum.service.TurnService;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = GameServiceApp.class)
@Transactional
public class TurnServiceIntegrationTest {
    @Autowired
    private TurnService turnService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TurnRepository turnRepository;

    @MockBean
    private CardServiceImpl cardService;

    @Test
    void makeTurn_ShouldProcessTurnCorrectly() {
        UUID userId = UUID.randomUUID();
        GameSession gameSession = createGameSession(GameState.IN_PROGRESS, 0);
        Player player = createPlayer(gameSession, userId, 0, false);
        PointsCard card = createPointsCard(gameSession, "Points-1", 1);

        when(cardService.applyCardEffect(any(), any(), any())).thenReturn(gameSession);

        TurnResult result = turnService.makeTurn(gameSession.getId(), userId);

        assertNotNull(result);
        assertEquals(userId, result.getCurrentPlayer().getUserId());
        assertEquals(GameState.IN_PROGRESS, result.getGameState());

        List<Turn> turns = turnRepository.findAll();
        assertEquals(1, turns.size());
        assertEquals(card.getId(), turns.getFirst().getPlayedCard().getId());

        verify(cardService).applyCardEffect(gameSession.getId(), userId, card);
    }

    @Test
    void makeTurn_ShouldThrowWhenGameNotInProgress() {
        UUID userId = UUID.randomUUID();
        GameSession gameSession = createGameSession(GameState.WAITING_FOR_PLAYERS, 0);

        assertThrows(GameSessionNotInProgressException.class, () ->
                turnService.makeTurn(gameSession.getId(), userId));
    }

    @Test
    void makeTurn_ShouldThrowWhenNotPlayerTurn() {
        UUID currentPlayerId = UUID.randomUUID();
        UUID otherPlayerId = UUID.randomUUID();
        GameSession gameSession = createGameSession(GameState.IN_PROGRESS, 0);

        Player player1 = createPlayer(gameSession, currentPlayerId, 0, false);
        Player player2 = createPlayer(gameSession, otherPlayerId, 0, false);

        assertThrows(NotPlayerTurnException.class, () ->
                turnService.makeTurn(gameSession.getId(), otherPlayerId));
    }

    @Test
    void makeTurn_ShouldThrowWhenDeckEmpty() {
        UUID userId = UUID.randomUUID();
        GameSession gameSession = createGameSession(GameState.IN_PROGRESS, 0);
        createPlayer(gameSession, userId, 0, false);

        assertThrows(EmptyDeckException.class, () ->
                turnService.makeTurn(gameSession.getId(), userId));
    }

    private GameSession createGameSession(GameState state, int currentPlayerIndex) {
        GameSession gameSession = GameSession.builder()
                .state(state)
                .currentPlayerIndex(currentPlayerIndex)
                .deck(new ArrayList<>())
                .players(new ArrayList<>())
                .build();
        return gameSessionRepository.save(gameSession);
    }

    private Player createPlayer(GameSession gameSession, UUID userId, int score, boolean blocked) {
        Player player = Player.builder()
                .userId(userId)
                .gameSession(gameSession)
                .score(score)
                .blocked(blocked)
                .build();
        Player savedPlayer = playerRepository.save(player);
        gameSession.getPlayers().add(savedPlayer);
        gameSessionRepository.save(gameSession);
        return savedPlayer;
    }

    private PointsCard createPointsCard(GameSession gameSession, String name, int value) {
        PointsCard card = PointsCard.builder()
                .name(name)
                .value(value)
                .gameSession(gameSession)
                .build();
        gameSession.getDeck().add(card);
        gameSessionRepository.save(gameSession);
        return card;
    }
}
