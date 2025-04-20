package ru.game.practicum.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.game.practicum.exception.InvalidCredentialsException;
import ru.game.practicum.exception.UserAlreadyExistsException;
import ru.game.practicum.exception.UserNotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends BaseErrorHandler {
    // 400 Bad Request - Ошибки валидации
    @ExceptionHandler({
            InvalidCredentialsException.class,
            UserAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(RuntimeException ex) {
        return handleException(ex, HttpStatus.BAD_REQUEST);
    }

    // 404 Not Found - Ресурсы не найдены
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex) {
        return handleException(ex, HttpStatus.NOT_FOUND);
    }


    @Override
    protected String getFriendlyMessage(RuntimeException ex) {
        return switch (ex) {
            case InvalidCredentialsException e -> "Invalid login or password";
            case UserAlreadyExistsException e -> "User already exists";
            case UserNotFoundException e -> "User not found";
            default -> "An unexpected error occurred";
        };
    }
}
