package ru.game.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.game.practicum.dto.GameSessionDto;
import ru.game.practicum.entity.GameSession;
import ru.game.practicum.service.TurnService;

import java.util.UUID;

@RestController
@RequestMapping("/api/games/{gameId}/turns")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TurnController {
   TurnService turnService;

    @PostMapping
    public ResponseEntity<GameSessionDto> makeTurn(
            @PathVariable UUID gameId,
            @RequestHeader("X-User-Id") String userId) {
        GameSession gameSession = turnService.makeTurn(gameId, userId);
        return ResponseEntity.ok(toDto(gameSession));
    }
}
