package ru.game.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.game.practicum.dto.GameSessionDto;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.service.GameSessionService;

import java.util.UUID;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameSessionController {
    GameSessionService gameSessionService;
    @PostMapping
    public ResponseEntity<GameSessionDto> createGameSession(@RequestHeader("X-User-Id") String userId) {
        GameSession gameSession = gameSessionService.createGameSession(userId);
        return ResponseEntity.ok(toDto(gameSession));
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<GameSessionDto> joinGameSession(
            @PathVariable UUID gameId,
            @RequestHeader("X-User-Id") String userId) {
        GameSession gameSession = gameSessionService.joinGameSession(gameId, userId);
        return ResponseEntity.ok(toDto(gameSession));
    }

    @PostMapping("/{gameId}/start")
    public ResponseEntity<GameSessionDto> startGameSession(
            @PathVariable UUID gameId,
            @RequestHeader("X-User-Id") String userId) {
        GameSession gameSession = gameSessionService.startGameSession(gameId, userId);
        return ResponseEntity.ok(toDto(gameSession));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameSessionDto> getGameSessionStatus(@PathVariable UUID gameId) {
        GameSession gameSession = gameSessionService.getGameSessionStatus(gameId);
        return ResponseEntity.ok(toDto(gameSession));
    }
}
