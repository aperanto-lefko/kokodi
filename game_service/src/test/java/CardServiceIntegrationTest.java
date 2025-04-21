import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import ru.game.practicum.dto.game_service.GameState;
import ru.game.practicum.entity.ActionCard;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;
import ru.game.practicum.entity.PointsCard;
import ru.game.practicum.repository.GameSessionRepository;
import ru.game.practicum.repository.PlayerRepository;
import ru.game.practicum.service.CardService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class CardServiceIntegrationTest {
    @Autowired
    private CardService cardService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void applyPointsCardEffect_ShouldIncreasePlayerScore() {

        GameSession gameSession = GameSession.builder()
                .state(GameState.IN_PROGRESS)
                .currentPlayerIndex(0)
                .build();
        gameSession = gameSessionRepository.save(gameSession);

        Player player = Player.builder()
                .userId(UUID.randomUUID())
                .gameSession(gameSession)
                .score(10)
                .blocked(false)
                .build();
        player = playerRepository.save(player);

        gameSession.setPlayers(List.of(player));
        gameSessionRepository.save(gameSession);

        PointsCard card = PointsCard.builder()
                .name("Points")
                .value(5)
                .gameSession(gameSession)
                .build();
        entityManager.persist(card);


        GameSession result = cardService.applyCardEffect(gameSession.getId(), player.getUserId(), card);


        Player updatedPlayer = playerRepository.findById(player.getId()).get();
        assertEquals(15, updatedPlayer.getScore());
        assertFalse(result.getDeck().contains(card));
    }

    @Test
    void applyBlockEffect_ShouldBlockNextPlayer() {

        GameSession gameSession = GameSession.builder()
                .state(GameState.IN_PROGRESS)
                .currentPlayerIndex(0)
                .build();
        gameSession = gameSessionRepository.save(gameSession);

        Player player1 = Player.builder()
                .userId(UUID.randomUUID())
                .gameSession(gameSession)
                .score(10)
                .blocked(false)
                .build();
        Player player2 = Player.builder()
                .userId(UUID.randomUUID())
                .gameSession(gameSession)
                .score(15)
                .blocked(false)
                .build();
        player1 = playerRepository.save(player1);
        player2 = playerRepository.save(player2);

        gameSession.setPlayers(List.of(player1, player2));
        gameSessionRepository.save(gameSession);

        ActionCard card = ActionCard.builder()
                .name("Block")
                .gameSession(gameSession)
                .build();
        entityManager.persist(card);


        GameSession result = cardService.applyCardEffect(gameSession.getId(), player1.getUserId(), card);


        Player nextPlayer = playerRepository.findById(player2.getId()).get();
        assertTrue(nextPlayer.isBlocked());
        assertEquals(0, result.getCurrentPlayerIndex()); // Should stay on current player as next is blocked
    }

    @Test
    void applyStealEffect_ShouldTransferPoints() {

        GameSession gameSession = GameSession.builder()
                .state(GameState.IN_PROGRESS)
                .currentPlayerIndex(0)
                .build();
        gameSession = gameSessionRepository.save(gameSession);

        Player player1 = Player.builder()
                .userId(UUID.randomUUID())
                .gameSession(gameSession)
                .score(10)
                .blocked(false)
                .build();
        Player player2 = Player.builder()
                .userId(UUID.randomUUID())
                .gameSession(gameSession)
                .score(20)
                .blocked(false)
                .build();
        player1 = playerRepository.save(player1);
        player2 = playerRepository.save(player2);

        gameSession.setPlayers(List.of(player1, player2));
        gameSessionRepository.save(gameSession);

        ActionCard card = ActionCard.builder()
                .name("Steal-3")
                .value(3)
                .gameSession(gameSession)
                .build();
        entityManager.persist(card);


        GameSession result = cardService.applyCardEffect(gameSession.getId(), player1.getUserId(), card);


        Player updatedPlayer1 = playerRepository.findById(player1.getId()).get();
        Player updatedPlayer2 = playerRepository.findById(player2.getId()).get();
        assertEquals(13, updatedPlayer1.getScore());
        assertEquals(17, updatedPlayer2.getScore());
    }

    @Test
    void checkWinCondition_ShouldFinishGameWhenPlayerReaches30Points() {

        GameSession gameSession = GameSession.builder()
                .state(GameState.IN_PROGRESS)
                .currentPlayerIndex(0)
                .build();
        gameSession = gameSessionRepository.save(gameSession);

        Player player = Player.builder()
                .userId(UUID.randomUUID())
                .gameSession(gameSession)
                .score(29)
                .blocked(false)
                .build();
        player = playerRepository.save(player);

        gameSession.setPlayers(List.of(player));
        gameSessionRepository.save(gameSession);

        PointsCard card = PointsCard.builder()
                .name("Points")
                .value(1)
                .gameSession(gameSession)
                .build();
        entityManager.persist(card);


        GameSession result = cardService.applyCardEffect(gameSession.getId(), player.getUserId(), card);


        assertEquals(GameState.FINISHED, result.getState());
    }
}
