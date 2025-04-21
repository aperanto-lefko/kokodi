package ru.game.practicum.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.game.practicum.exception.EmptyDeckException;
import ru.game.practicum.exception.GameSessionAlreadyStartedException;
import ru.game.practicum.exception.GameSessionFullException;
import ru.game.practicum.exception.GameSessionNotFoundException;
import ru.game.practicum.exception.GameSessionNotInProgressException;
import ru.game.practicum.exception.NoPlayersToStealFromException;
import ru.game.practicum.exception.NotEnoughPlayersException;
import ru.game.practicum.exception.NotGameSessionOwnerException;
import ru.game.practicum.exception.NotPlayerTurnException;
import ru.game.practicum.exception.PlayerNotFoundException;
import ru.game.practicum.exception.UnknownCardTypeException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends BaseErrorHandler {
    // 400 Bad Request
    @ExceptionHandler({
            EmptyDeckException.class,
            GameSessionAlreadyStartedException.class,
            GameSessionFullException.class,
            GameSessionNotInProgressException.class,
            NotEnoughPlayersException.class,
            NotPlayerTurnException.class,
            UnknownCardTypeException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(RuntimeException ex) {
        return handleException(ex, HttpStatus.BAD_REQUEST);
    }

    // 403 Forbidden
    @ExceptionHandler(NotGameSessionOwnerException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenExceptions(RuntimeException ex) {
        return handleException(ex, HttpStatus.FORBIDDEN);
    }

    // 404 Not Found
    @ExceptionHandler({
            GameSessionNotFoundException.class,
            PlayerNotFoundException.class,
            NoPlayersToStealFromException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(RuntimeException ex) {
        return handleException(ex, HttpStatus.NOT_FOUND);
    }


    @Override
    protected String getFriendlyMessage(RuntimeException ex) {
        if (ex == null) {
            return "An unexpected error occurred";
        }

        String className = ex.getClass().getSimpleName();
        return switch (className) {
            case "EmptyDeckException" -> "Deck is empty in game session";
            case "GameSessionAlreadyStartedException" -> "Game session has already started";
            case "GameSessionFullException" -> "Game session is full";
            case "GameSessionNotInProgressException" -> "Game session is not in progress";
            case "NotEnoughPlayersException" -> "Not enough players in game session";
            case "NotPlayerTurnException" -> "It's not your turn to play";
            case "UnknownCardTypeException" -> "Unknown card type encountered";
            case "NotGameSessionOwnerException" -> "You are not the owner of this game session";
            case "GameSessionNotFoundException" -> "Game session not found";
            case "PlayerNotFoundException" -> "Player not found";
            case "NoPlayersToStealFromException" -> "No players available to steal from";
            default -> "An unexpected error occurred";
        };
    }
}
