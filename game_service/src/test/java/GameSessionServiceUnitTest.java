import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.game.practicum.dto.auth_service.UserDto;
import ru.game.practicum.dto.game_service.GameState;
import ru.game.practicum.entity.ActionCard;
import ru.game.practicum.entity.Card;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;
import ru.game.practicum.entity.PointsCard;
import ru.game.practicum.exception.GameSessionNotFoundException;
import ru.game.practicum.feign.AuthServiceClient;
import ru.game.practicum.repository.GameSessionRepository;
import ru.game.practicum.service.GameSessionService;
import ru.game.practicum.service.GameSessionServiceImpl;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameSessionServiceUnitTest {

    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private GameSessionServiceImpl gameSessionService;

    @Test
    void createGameSession_ShouldCreateNewSession() {
               UUID userId = UUID.randomUUID();
        UserDto user = new UserDto(userId, "testUser", "login");
        when(authServiceClient.getUser(userId)).thenReturn(ResponseEntity.ok(user));


        GameSession result = gameSessionService.createGameSession(userId);

        assertNotNull(result);
        assertEquals(GameState.WAITING_FOR_PLAYERS, result.getState());
        assertEquals(1, result.getPlayers().size());
        assertEquals(userId, result.getPlayers().get(0).getUserId());
        verify(gameSessionRepository).save(result);
    }

    @Test
    void joinGameSession_ShouldAddPlayer() {

        UUID gameSessionId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        UUID joinerId = UUID.randomUUID();

        GameSession gameSession = GameSession.builder()
                .id(gameSessionId)
                .state(GameState.WAITING_FOR_PLAYERS)
                .players(new ArrayList<>())
                .build();

        Player creator = Player.builder().userId(creatorId).build();
        gameSession.getPlayers().add(creator);

        UserDto user = new UserDto(joinerId, "joiner", "loginJoiner");
        when(authServiceClient.getUser(joinerId)).thenReturn(ResponseEntity.ok(user));
        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.of(gameSession));

        GameSession result = gameSessionService.joinGameSession(gameSessionId, joinerId);


        assertEquals(2, result.getPlayers().size());
        assertTrue(result.getPlayers().stream().anyMatch(p -> p.getUserId().equals(joinerId)));
        verify(gameSessionRepository).save(gameSession);
    }

    @Test
    void startGameSession_ShouldInitializeDeck() {

        UUID gameSessionId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();

        GameSession gameSession = GameSession.builder()
                .id(gameSessionId)
                .state(GameState.WAITING_FOR_PLAYERS)
                .players(new ArrayList<>())
                .deck(new ArrayList<>())
                .build();

        Player creator = Player.builder().userId(creatorId).build();
        Player player2 = Player.builder().userId(UUID.randomUUID()).build();
        gameSession.getPlayers().addAll(List.of(creator, player2));

        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.of(gameSession));


        GameSession result = gameSessionService.startGameSession(gameSessionId, creatorId);


        assertEquals(GameState.IN_PROGRESS, result.getState());
        assertFalse(result.getDeck().isEmpty());
        verify(gameSessionRepository).save(gameSession);
    }

    @Test
    void initializeDeck_ShouldCreateCorrectNumberOfCards() {

        GameSession gameSession = new GameSession();

        gameSessionService.initializeDeck(gameSession);


        assertEquals(13, gameSession.getDeck().size());
        long pointsCards = gameSession.getDeck().stream()
                .filter(c -> c instanceof PointsCard)
                .count();
        assertEquals(10, pointsCards);

        long actionCards = gameSession.getDeck().stream()
                .filter(c -> c instanceof ActionCard)
                .count();
        assertEquals(3, actionCards);
    }

    @Test
    void shuffleDeck_ShouldChangeCardOrder() {

        GameSession gameSession = new GameSession();
        gameSessionService.initializeDeck(gameSession);
        List<Card> originalOrder = new ArrayList<>(gameSession.getDeck());


        gameSessionService.shuffleDeck(gameSession);


        assertNotEquals(originalOrder, gameSession.getDeck());
        assertEquals(originalOrder.size(), gameSession.getDeck().size());
        assertTrue(gameSession.getDeck().containsAll(originalOrder));
    }

    @Test
    void getGameSessionStatus_ShouldReturnSession() {

        UUID gameSessionId = UUID.randomUUID();
        GameSession expected = new GameSession();
        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.of(expected));


        GameSession result = gameSessionService.getGameSessionStatus(gameSessionId);

        assertEquals(expected, result);
    }

    @Test
    void getGameSessionStatus_ShouldThrowWhenNotFound() {

        UUID gameSessionId = UUID.randomUUID();
        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.empty());


        assertThrows(GameSessionNotFoundException.class, () ->
                gameSessionService.getGameSessionStatus(gameSessionId));
    }
}
