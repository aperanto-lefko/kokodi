import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@SpringBootTest
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

    @Autowired
    private TestEntityManager entityManager;

    @MockBean
    private CardService cardService;

    @Test
    void makeTurn_ShouldProcessTurnCorrectly() {

        UUID userId = UUID.randomUUID();
        GameSession gameSession = GameSession.builder()
                .state(GameState.IN_PROGRESS)
                .currentPlayerIndex(0)
                .deck(new ArrayList<>())
                .build();
        gameSession = gameSessionRepository.save(gameSession);

        Player player = Player.builder()
                .userId(userId)
                .gameSession(gameSession)
                .score(0)
                .blocked(false)
                .build();
        player = playerRepository.save(player);

        gameSession.setPlayers(List.of(player));

        PointsCard card = PointsCard.builder()
                .name("Points-1")
                .value(1)
                .gameSession(gameSession)
                .build();
        entityManager.persist(card);
        gameSession.getDeck().add(card);
        gameSessionRepository.save(gameSession);

        when(cardService.applyCardEffect(any(), any(), any())).thenReturn(gameSession);


        UUID gameSessionId = gameSession.getId();
        TurnResult result = turnService.makeTurn(gameSessionId, userId);

        assertNotNull(result);
        assertEquals(userId, result.getCurrentPlayer().getUserId());
        assertEquals(GameState.IN_PROGRESS, result.getGameState());

        List<Turn> turns = turnRepository.findAll();
        assertEquals(1, turns.size());
        assertEquals(card.getId(), turns.getFirst().getPlayedCard().getId());

        verify(cardService).applyCardEffect(gameSessionId, userId, card);
    }

    @Test
    void makeTurn_ShouldThrowWhenGameNotInProgress() {
        UUID userId = UUID.randomUUID();
        GameSession gameSession = GameSession.builder()
                .state(GameState.WAITING_FOR_PLAYERS)
                .build();
        gameSession = gameSessionRepository.save(gameSession);

        UUID gameSessionId = gameSession.getId();
        assertThrows(GameSessionNotInProgressException.class, () ->
                turnService.makeTurn(gameSessionId, userId));
    }

    @Test
    void makeTurn_ShouldThrowWhenNotPlayerTurn() {

        UUID currentPlayerId = UUID.randomUUID();
        UUID otherPlayerId = UUID.randomUUID();

        GameSession gameSession = GameSession.builder()
                .state(GameState.IN_PROGRESS)
                .currentPlayerIndex(0)
                .deck(new ArrayList<>())
                .build();
        gameSession = gameSessionRepository.save(gameSession);

        Player player1 = Player.builder()
                .userId(currentPlayerId)
                .gameSession(gameSession)
                .build();
        Player player2 = Player.builder()
                .userId(otherPlayerId)
                .gameSession(gameSession)
                .build();
        playerRepository.saveAll(List.of(player1, player2));
        gameSession.setPlayers(List.of(player1, player2));
        gameSessionRepository.save(gameSession);

        UUID gameSessionId = gameSession.getId();
        assertThrows(NotPlayerTurnException.class, () ->
                turnService.makeTurn(gameSessionId, otherPlayerId));
    }

    @Test
    void makeTurn_ShouldThrowWhenDeckEmpty() {
        UUID userId = UUID.randomUUID();
        GameSession gameSession = GameSession.builder()
                .state(GameState.IN_PROGRESS)
                .currentPlayerIndex(0)
                .deck(new ArrayList<>())
                .build();
        gameSession = gameSessionRepository.save(gameSession);

        Player player = Player.builder()
                .userId(userId)
                .gameSession(gameSession)
                .build();
        playerRepository.save(player);
        gameSession.setPlayers(List.of(player));
        gameSessionRepository.save(gameSession);

        // Сохраняем ID в отдельную final переменную
        UUID gameSessionId = gameSession.getId();
        assertThrows(EmptyDeckException.class, () ->
                turnService.makeTurn(gameSessionId, userId));
    }
}
