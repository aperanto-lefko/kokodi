package ru.game.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.game.practicum.entity.Card;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.dto.game_service.GameState;
import ru.game.practicum.entity.Player;
import ru.game.practicum.entity.Turn;
import ru.game.practicum.entity.TurnResult;
import ru.game.practicum.exception.EmptyDeckException;
import ru.game.practicum.exception.GameSessionNotFoundException;
import ru.game.practicum.exception.GameSessionNotInProgressException;
import ru.game.practicum.exception.NotPlayerTurnException;
import ru.game.practicum.repository.GameSessionRepository;
import ru.game.practicum.repository.TurnRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class TurnServiceImpl implements TurnService { //обработка ходов игроков
    GameSessionRepository gameSessionRepository;
    TurnRepository turnRepository;
    CardService cardService;

    @Override
    @Transactional
    public TurnResult makeTurn(UUID gameSessionId, UUID userId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new GameSessionNotFoundException(gameSessionId));

        if (gameSession.getState() != GameState.IN_PROGRESS) { //проверка статуса игры
            throw new GameSessionNotInProgressException(gameSessionId);
        }

        Player currentPlayer = gameSession.getPlayers().get(gameSession.getCurrentPlayerIndex());
        if (!currentPlayer.getUserId().equals(userId)) { //проверка - ход этого игрока или нет
            throw new NotPlayerTurnException(userId, gameSession.getCurrentPlayerIndex());
        }

        if (gameSession.getDeck().isEmpty()) { //проверка пустая ли колодда
            throw new EmptyDeckException(gameSessionId);
        }

        Card card = gameSession.getDeck().getFirst(); //берем следующую карту

        Turn turn = Turn.builder() //фиксируем ход
                .gameSession(gameSession)
                .player(currentPlayer)
                .playedCard(card)
                .build();
        turnRepository.save(turn);
        // добавляем эффект карты
        cardService.applyCardEffect(gameSessionId, userId, card);

        return TurnResult.builder()
                .turn(turn)
                .currentPlayer(currentPlayer)
                .gameState(gameSession.getState())
                .nextPlayerId(gameSession.getPlayers()
                        .get(gameSession.getCurrentPlayerIndex())
                        .getUserId())
                .build();
    }
}
