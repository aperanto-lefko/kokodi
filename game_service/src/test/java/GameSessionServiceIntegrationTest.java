import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import ru.game.practicum.dto.auth_service.UserDto;
import ru.game.practicum.dto.game_service.GameState;
import ru.game.practicum.entity.ActionCard;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.PointsCard;
import ru.game.practicum.exception.GameSessionAlreadyStartedException;
import ru.game.practicum.exception.GameSessionFullException;
import ru.game.practicum.exception.GameSessionNotFoundException;
import ru.game.practicum.exception.NotEnoughPlayersException;
import ru.game.practicum.exception.NotGameSessionOwnerException;
import ru.game.practicum.feign.AuthServiceClient;
import ru.game.practicum.repository.GameSessionRepository;
import ru.game.practicum.repository.PlayerRepository;
import ru.game.practicum.service.GameSessionService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class GameSessionServiceIntegrationTest {
    @Autowired
    private GameSessionService gameSessionService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @MockBean
    private AuthServiceClient authServiceClient;

    @BeforeEach
    void setUp() {
        UserDto testUser = new UserDto(UUID.randomUUID(), "testUser", "testLogin");
        when(authServiceClient.getUser(any(UUID.class))).thenReturn(ResponseEntity.ok(testUser));
    }

    @Test
    void createGameSession_ShouldCreateNewSessionWithCreatorAsPlayer() {

        UUID userId = UUID.randomUUID();
        GameSession result = gameSessionService.createGameSession(userId);
        assertNotNull(result.getId());
        assertEquals(GameState.WAITING_FOR_PLAYERS, result.getState());
        assertEquals(1, result.getPlayers().size());
        assertEquals(userId, result.getPlayers().get(0).getUserId());
        verify(authServiceClient).getUser(userId);
    }

    @Test
    void joinGameSession_ShouldAddPlayerToSession() {
        UUID creatorId = UUID.randomUUID();
        UUID joinerId = UUID.randomUUID();
        GameSession gameSession = gameSessionService.createGameSession(creatorId);
        GameSession result = gameSessionService.joinGameSession(gameSession.getId(), joinerId);
        assertEquals(2, result.getPlayers().size());
        assertTrue(result.getPlayers().stream().anyMatch(p -> p.getUserId().equals(joinerId)));
        verify(authServiceClient, times(2)).getUser(any(UUID.class));
    }

    @Test
    void joinGameSession_ShouldThrowWhenSessionFull() {

        UUID creatorId = UUID.randomUUID();
        GameSession gameSession = gameSessionService.createGameSession(creatorId);

        for (int i = 0; i < 3; i++) {
            gameSessionService.joinGameSession(gameSession.getId(), UUID.randomUUID());
        }

        assertThrows(GameSessionFullException.class, () ->
                gameSessionService.joinGameSession(gameSession.getId(), UUID.randomUUID()));
    }

    @Test
    void joinGameSession_ShouldThrowWhenGameAlreadyStarted() {

        UUID creatorId = UUID.randomUUID();
        UUID joinerId = UUID.randomUUID();

        GameSession gameSession = gameSessionService.createGameSession(creatorId);
        gameSessionService.joinGameSession(gameSession.getId(), UUID.randomUUID());
        gameSessionService.startGameSession(gameSession.getId(), creatorId);


        assertThrows(GameSessionAlreadyStartedException.class, () ->
                gameSessionService.joinGameSession(gameSession.getId(), joinerId));
    }

    @Test
    void startGameSession_ShouldInitializeAndShuffleDeck() {

        UUID creatorId = UUID.randomUUID();
        GameSession gameSession = gameSessionService.createGameSession(creatorId);
        gameSessionService.joinGameSession(gameSession.getId(), UUID.randomUUID());
        GameSession result = gameSessionService.startGameSession(gameSession.getId(), creatorId);

        assertEquals(GameState.IN_PROGRESS, result.getState());
        assertNotNull(result.getDeck());
        assertEquals(13, result.getDeck().size());
        assertTrue(result.getDeck().stream().anyMatch(c -> c instanceof PointsCard));
        assertTrue(result.getDeck().stream().anyMatch(c -> c instanceof ActionCard));
    }

    @Test
    void startGameSession_ShouldThrowWhenNotEnoughPlayers() {
        UUID creatorId = UUID.randomUUID();
        GameSession gameSession = gameSessionService.createGameSession(creatorId);

        assertThrows(NotEnoughPlayersException.class, () ->
                gameSessionService.startGameSession(gameSession.getId(), creatorId));
    }

    @Test
    void startGameSession_ShouldThrowWhenNotOwner() {

        UUID creatorId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        GameSession gameSession = gameSessionService.createGameSession(creatorId);
        gameSessionService.joinGameSession(gameSession.getId(), otherUserId);

        assertThrows(NotGameSessionOwnerException.class, () ->
                gameSessionService.startGameSession(gameSession.getId(), otherUserId));
    }

    @Test
    void getGameSessionStatus_ShouldReturnSession() {

        UUID creatorId = UUID.randomUUID();
        GameSession expected = gameSessionService.createGameSession(creatorId);
        GameSession result = gameSessionService.getGameSessionStatus(expected.getId());
        assertEquals(expected.getId(), result.getId());
    }

    @Test
    void getGameSessionStatus_ShouldThrowWhenNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(GameSessionNotFoundException.class, () ->
                gameSessionService.getGameSessionStatus(nonExistentId));
    }
}
