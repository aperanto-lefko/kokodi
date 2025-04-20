package ru.game.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.game.practicum.entity.ActionCard;
import ru.game.practicum.entity.Card;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.dto.game_service.GameState;
import ru.game.practicum.entity.Player;
import ru.game.practicum.entity.PointsCard;
import ru.game.practicum.exception.GameSessionNotFoundException;
import ru.game.practicum.exception.NoPlayersToStealFromException;
import ru.game.practicum.exception.PlayerNotFoundException;
import ru.game.practicum.exception.UnknownCardTypeException;
import ru.game.practicum.repository.GameSessionRepository;
import ru.game.practicum.repository.PlayerRepository;

import java.util.Comparator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class CardService { //работа с картами и их эффектами
    GameSessionRepository gameSessionRepository;
    PlayerRepository playerRepository;

    @Transactional
    public GameSession applyCardEffect(UUID gameSessionId, UUID userId, Card card) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId) //проверка сессии игры
                .orElseThrow(() -> new GameSessionNotFoundException(gameSessionId));

        Player currentPlayer = gameSession.getPlayers().stream() //проверка игрока
                .filter(p -> p.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException(userId));

        if (card instanceof PointsCard) { //проверка какая карта //переделать на фабрику
            applyPointsCardEffect(currentPlayer, (PointsCard) card);
        } else if (card instanceof ActionCard) {
            applyActionCardEffect(gameSession, currentPlayer, (ActionCard) card);
        }

        // удаляем карту из колоды
        gameSession.getDeck().remove(card);

        // проверка закончить ли игру
        checkWinCondition(gameSession);

        // переход к следующему игроку
        moveToNextPlayer(gameSession);

        return gameSessionRepository.save(gameSession);
    }

    //подсчет очков за карту
    @Transactional
    private void applyPointsCardEffect(Player player, PointsCard card) {
        player.setScore(player.getScore() + card.getValue());
        playerRepository.save(player);
    }

    //реализация действия карты action в зависимости от назначения
    private void applyActionCardEffect(GameSession gameSession, Player currentPlayer, ActionCard card) {
        switch (card.getName()) {
            case "Block":
                applyBlockEffect(gameSession);
                break;
            case "Steal-3":
                applyStealEffect(gameSession, currentPlayer, card.getValue());
                break;
            case "DoubleDown":
                applyDoubleDownEffect(currentPlayer);
                break;
            default:
                throw new UnknownCardTypeException(card.getName());
        }
    }

    private void applyBlockEffect(GameSession gameSession) {
        int nextPlayerIndex = (gameSession.getCurrentPlayerIndex() + 1) % gameSession.getPlayers().size();
        Player nextPlayer = gameSession.getPlayers().get(nextPlayerIndex);
        nextPlayer.setBlocked(true);
        playerRepository.save(nextPlayer);
    }

    private void applyStealEffect(GameSession gameSession, Player currentPlayer, int value) {
        Player targetPlayer = gameSession.getPlayers().stream()
                .filter(p -> !p.getUserId().equals(currentPlayer.getUserId()))
                .max(Comparator.comparingInt(Player::getScore))
                .orElseThrow(NoPlayersToStealFromException::new);

        int stolenAmount = Math.min(value, targetPlayer.getScore());
        targetPlayer.setScore(targetPlayer.getScore() - stolenAmount);
        currentPlayer.setScore(currentPlayer.getScore() + stolenAmount);

        playerRepository.save(targetPlayer);
        playerRepository.save(currentPlayer);
    }

    private void applyDoubleDownEffect(Player player) {
        int newScore = Math.min(player.getScore() * 2, 30);
        player.setScore(newScore);
        playerRepository.save(player);
    }

    private void checkWinCondition(GameSession gameSession) {
        boolean hasWinner = gameSession.getPlayers().stream()
                .anyMatch(p -> p.getScore() >= 30);

        if (hasWinner) {
            gameSession.setState(GameState.FINISHED);
        }
    }

    private void moveToNextPlayer(GameSession gameSession) {
        int nextIndex = gameSession.getCurrentPlayerIndex();
        int playersCount = gameSession.getPlayers().size();

        do {
            nextIndex = (nextIndex + 1) % playersCount;
            Player nextPlayer = gameSession.getPlayers().get(nextIndex);

            if (!nextPlayer.isBlocked()) {
                gameSession.setCurrentPlayerIndex(nextIndex);
                return;
            }

            nextPlayer.setBlocked(false);
            playerRepository.save(nextPlayer);
        } while (true);
    }
}
