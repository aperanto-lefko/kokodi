import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.game.practicum.dto.game_service.GameState;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;
import ru.game.practicum.entity.PointsCard;
import ru.game.practicum.entity.Turn;
import ru.game.practicum.entity.TurnResult;
import ru.game.practicum.exception.EmptyDeckException;
import ru.game.practicum.exception.GameSessionNotFoundException;
import ru.game.practicum.exception.GameSessionNotInProgressException;
import ru.game.practicum.exception.NotPlayerTurnException;
import ru.game.practicum.repository.GameSessionRepository;
import ru.game.practicum.repository.TurnRepository;
import ru.game.practicum.service.CardService;
import ru.game.practicum.service.TurnService;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TurnServiceUnitTest {
    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private TurnRepository turnRepository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private TurnService turnService;

    @Test
    void makeTurn_ShouldProcessTurnSuccessfully() {

        UUID gameSessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        GameSession gameSession = GameSession.builder()
                .state(GameState.IN_PROGRESS)
                .currentPlayerIndex(0)
                .deck(new ArrayList<>())
                .players(new ArrayList<>())
                .build();

        Player player = Player.builder()
                .userId(userId)
                .build();
        gameSession.getPlayers().add(player);

        PointsCard card = PointsCard.builder()
                .name("Points-1")
                .value(1)
                .build();
        gameSession.getDeck().add(card);

        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.of(gameSession));
        when(cardService.applyCardEffect(any(), any(), any())).thenReturn(gameSession);


        TurnResult result = turnService.makeTurn(gameSessionId, userId);


        assertNotNull(result);
        assertEquals(userId, result.getCurrentPlayer().getUserId());
        assertEquals(GameState.IN_PROGRESS, result.getGameState());

        verify(turnRepository).save(any(Turn.class));
        verify(cardService).applyCardEffect(gameSessionId, userId, card);
    }

    @Test
    void makeTurn_ShouldThrowWhenGameSessionNotFound() {

        UUID gameSessionId = UUID.randomUUID();
        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.empty());


        assertThrows(GameSessionNotFoundException.class, () ->
                turnService.makeTurn(gameSessionId, UUID.randomUUID()));
    }

    @Test
    void makeTurn_ShouldThrowWhenGameNotInProgress() {

        UUID gameSessionId = UUID.randomUUID();
        GameSession gameSession = GameSession.builder()
                .state(GameState.WAITING_FOR_PLAYERS)
                .build();
        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.of(gameSession));


        assertThrows(GameSessionNotInProgressException.class, () ->
                turnService.makeTurn(gameSessionId, UUID.randomUUID()));
    }

    @Test
    void makeTurn_ShouldThrowWhenNotPlayerTurn() {

        UUID gameSessionId = UUID.randomUUID();
        UUID currentPlayerId = UUID.randomUUID();
        UUID otherPlayerId = UUID.randomUUID();

        GameSession gameSession = GameSession.builder()
                .state(GameState.IN_PROGRESS)
                .currentPlayerIndex(0)
                .players(new ArrayList<>())
                .build();

        Player player1 = Player.builder().userId(currentPlayerId).build();
        Player player2 = Player.builder().userId(otherPlayerId).build();
        gameSession.getPlayers().addAll(List.of(player1, player2));

        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.of(gameSession));


        assertThrows(NotPlayerTurnException.class, () ->
                turnService.makeTurn(gameSessionId, otherPlayerId));
    }

    @Test
    void makeTurn_ShouldThrowWhenDeckEmpty() {

        UUID gameSessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        GameSession gameSession = GameSession.builder()
                .state(GameState.IN_PROGRESS)
                .currentPlayerIndex(0)
                .deck(new ArrayList<>())
                .players(new ArrayList<>())
                .build();

        Player player = Player.builder().userId(userId).build();
        gameSession.getPlayers().add(player);

        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.of(gameSession));


        assertThrows(EmptyDeckException.class, () ->
                turnService.makeTurn(gameSessionId, userId));
    }

    @Test
    void makeTurn_ShouldReturnCorrectNextPlayerId() {

        UUID gameSessionId = UUID.randomUUID();
        UUID currentPlayerId = UUID.randomUUID();
        UUID nextPlayerId = UUID.randomUUID();

        GameSession gameSession = GameSession.builder()
                .state(GameState.IN_PROGRESS)
                .currentPlayerIndex(0)
                .deck(new ArrayList<>())
                .players(new ArrayList<>())
                .build();

        PointsCard card = PointsCard.builder().build();
        gameSession.getDeck().add(card);

        Player player1 = Player.builder().userId(currentPlayerId).build();
        Player player2 = Player.builder().userId(nextPlayerId).build();
        gameSession.getPlayers().addAll(List.of(player1, player2));

        GameSession updatedGameSession = GameSession.builder()
                .currentPlayerIndex(1)
                .build();

        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.of(gameSession));
        when(cardService.applyCardEffect(any(), any(), any())).thenReturn(updatedGameSession);


        TurnResult result = turnService.makeTurn(gameSessionId, currentPlayerId);


        assertEquals(nextPlayerId, result.getNextPlayerId());
    }
}
