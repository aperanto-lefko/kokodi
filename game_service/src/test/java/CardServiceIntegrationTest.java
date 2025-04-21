import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.game.practicum.GameServiceApp;
import ru.game.practicum.dto.game_service.GameState;
import ru.game.practicum.entity.ActionCard;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;
import ru.game.practicum.entity.PointsCard;
import ru.game.practicum.repository.GameSessionRepository;
import ru.game.practicum.repository.PlayerRepository;
import ru.game.practicum.service.CardService;


import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = GameServiceApp.class)
@Transactional
public class CardServiceIntegrationTest {
    @Autowired
    private CardService cardService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void applyPointsCardEffect_ShouldIncreasePlayerScore() {

        GameSession gameSession = createGameSession(GameState.IN_PROGRESS, 0);
        Player player = createPlayer(gameSession, 10, false);
        PointsCard card = createPointsCard(gameSession, "Points", 5);

        GameSession result = cardService.applyCardEffect(gameSession.getId(), player.getUserId(), card);


        Player updatedPlayer = playerRepository.findById(player.getId()).orElseThrow();
        assertEquals(15, updatedPlayer.getScore());
        assertFalse(result.getDeck().contains(card));
    }

    @Test
    void applyBlockEffect_ShouldBlockNextPlayer() {

        GameSession gameSession = createGameSession(GameState.IN_PROGRESS, 0);
        Player player1 = createPlayer(gameSession, 10, false);
        Player player2 = createPlayer(gameSession, 15, false);
        ActionCard card = createActionCard(gameSession, "Block");


        GameSession result = cardService.applyCardEffect(gameSession.getId(), player1.getUserId(), card);


        Player updatedPlayer2 = playerRepository.findById(player2.getId()).orElseThrow();
        assertTrue(updatedPlayer2.isBlocked());
        assertEquals(0, result.getCurrentPlayerIndex());
    }

    @Test
    void applyStealEffect_ShouldTransferPoints() {

        GameSession gameSession = createGameSession(GameState.IN_PROGRESS, 0);
        Player player1 = createPlayer(gameSession, 10, false);
        Player player2 = createPlayer(gameSession, 20, false);
        ActionCard card = createActionCard(gameSession, "Steal-3", 3);


        cardService.applyCardEffect(gameSession.getId(), player1.getUserId(), card);

        Player updatedPlayer1 = playerRepository.findById(player1.getId()).orElseThrow();
        Player updatedPlayer2 = playerRepository.findById(player2.getId()).orElseThrow();
        assertEquals(13, updatedPlayer1.getScore());
        assertEquals(17, updatedPlayer2.getScore());
    }

    @Test
    void checkWinCondition_ShouldFinishGameWhenPlayerReaches30Points() {

        GameSession gameSession = createGameSession(GameState.IN_PROGRESS, 0);
        Player player = createPlayer(gameSession, 29, false);
        PointsCard card = createPointsCard(gameSession, "Points", 1);

        GameSession result = cardService.applyCardEffect(gameSession.getId(), player.getUserId(), card);

        assertEquals(GameState.FINISHED, result.getState());
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

    private Player createPlayer(GameSession gameSession, int score, boolean blocked) {
        Player player = Player.builder()
                .userId(UUID.randomUUID())
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

    private ActionCard createActionCard(GameSession gameSession, String name) {
        return createActionCard(gameSession, name, 0);
    }

    private ActionCard createActionCard(GameSession gameSession, String name, int value) {
        ActionCard card = ActionCard.builder()
                .name(name)
                .value(value)
                .gameSession(gameSession)
                .build();
        gameSession.getDeck().add(card);
        gameSessionRepository.save(gameSession);
        return card;
    }
}
