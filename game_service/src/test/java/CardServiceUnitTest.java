import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.game.practicum.dto.game_service.GameState;
import ru.game.practicum.entity.ActionCard;
import ru.game.practicum.entity.Card;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.entity.Player;
import ru.game.practicum.entity.PointsCard;
import ru.game.practicum.exception.GameSessionNotFoundException;
import ru.game.practicum.exception.PlayerNotFoundException;
import ru.game.practicum.exception.UnknownCardTypeException;
import ru.game.practicum.repository.GameSessionRepository;
import ru.game.practicum.repository.PlayerRepository;
import ru.game.practicum.service.CardService;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardServiceUnitTest {

    @Mock
    private GameSessionRepository gameSessionRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    void applyPointsCardEffect_ShouldIncreasePlayerScore() {

        Player player = Player.builder()
                .id(UUID.randomUUID())
                .score(10)
                .build();
        PointsCard card = PointsCard.builder()
                .value(5)
                .build();

        cardService.applyPointsCardEffect(player, card);


        assertEquals(15, player.getScore());
        verify(playerRepository).save(player);
    }

    @Test
    void applyBlockEffect_ShouldBlockNextPlayer() {

        GameSession gameSession = GameSession.builder()
                .currentPlayerIndex(0)
                .build();
        Player player1 = Player.builder().id(UUID.randomUUID()).blocked(false).build();
        Player player2 = Player.builder().id(UUID.randomUUID()).blocked(false).build();
        gameSession.setPlayers(List.of(player1, player2));

        cardService.applyBlockEffect(gameSession);


        assertTrue(player2.isBlocked());
        verify(playerRepository).save(player2);
    }

    @Test
    void applyStealEffect_ShouldTransferPointsFromHighestScorer() {

        GameSession gameSession = new GameSession();
        Player currentPlayer = Player.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .score(10)
                .build();
        Player targetPlayer = Player.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .score(20)
                .build();
        gameSession.setPlayers(List.of(currentPlayer, targetPlayer));

        ActionCard card = ActionCard.builder()
                .value(3)
                .build();


        cardService.applyStealEffect(gameSession, currentPlayer, card.getValue());

        assertEquals(13, currentPlayer.getScore());
        assertEquals(17, targetPlayer.getScore());
        verify(playerRepository, times(2)).save(any(Player.class));
    }

    @Test
    void applyDoubleDownEffect_ShouldDoublePlayerScore() {
        Player player = Player.builder()
                .id(UUID.randomUUID())
                .score(10)
                .build();
        cardService.applyDoubleDownEffect(player);

        assertEquals(20, player.getScore());
        verify(playerRepository).save(player);
    }

    @Test
    void applyDoubleDownEffect_ShouldNotExceed30Points() {

        Player player = Player.builder()
                .id(UUID.randomUUID())
                .score(20)
                .build();

        cardService.applyDoubleDownEffect(player);

        assertEquals(30, player.getScore());
        verify(playerRepository).save(player);
    }

    @Test
    void moveToNextPlayer_ShouldSkipBlockedPlayer() {

        GameSession gameSession = GameSession.builder()
                .currentPlayerIndex(0)
                .build();
        Player player1 = Player.builder().id(UUID.randomUUID()).blocked(false).build();
        Player player2 = Player.builder().id(UUID.randomUUID()).blocked(true).build();
        Player player3 = Player.builder().id(UUID.randomUUID()).blocked(false).build();
        gameSession.setPlayers(List.of(player1, player2, player3));


        cardService.moveToNextPlayer(gameSession);


        assertEquals(2, gameSession.getCurrentPlayerIndex());
        assertFalse(player2.isBlocked());
        verify(playerRepository).save(player2);
    }

    @Test
    void checkWinCondition_ShouldFinishGameWhenPlayerHas30OrMorePoints() {

        GameSession gameSession = GameSession.builder()
                .state(GameState.IN_PROGRESS)
                .build();
        Player player = Player.builder()
                .id(UUID.randomUUID())
                .score(30)
                .build();
        gameSession.setPlayers(List.of(player));


        cardService.checkWinCondition(gameSession);


        assertEquals(GameState.FINISHED, gameSession.getState());
    }

    @Test
    void applyCardEffect_ShouldThrowExceptionWhenGameSessionNotFound() {

        UUID gameSessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Card card = PointsCard.builder().build();

        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.empty());


        assertThrows(GameSessionNotFoundException.class,
                () -> cardService.applyCardEffect(gameSessionId, userId, card));
    }

    @Test
    void applyCardEffect_ShouldThrowExceptionWhenPlayerNotFound() {

        UUID gameSessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Card card = PointsCard.builder().build();

        GameSession gameSession = GameSession.builder()
                .players(List.of())
                .build();

        when(gameSessionRepository.findById(gameSessionId)).thenReturn(Optional.of(gameSession));


        assertThrows(PlayerNotFoundException.class,
                () -> cardService.applyCardEffect(gameSessionId, userId, card));
    }

    @Test
    void applyActionCardEffect_ShouldThrowExceptionForUnknownCardType() {

        GameSession gameSession = new GameSession();
        Player player = new Player();
        ActionCard card = ActionCard.builder()
                .name("Unknown")
                .build();


        assertThrows(UnknownCardTypeException.class,
                () -> cardService.applyActionCardEffect(gameSession, player, card));
    }
}

