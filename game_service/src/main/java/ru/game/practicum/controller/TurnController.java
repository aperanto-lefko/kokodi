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
import ru.game.practicum.dto.game_service.TurnResultDto;
import ru.game.practicum.entity.TurnResult;
import ru.game.practicum.mapper.TurnResultMapper;
import ru.game.practicum.service.TurnService;

import java.util.UUID;

@RestController
@RequestMapping("/api/games/{gameId}/turns")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TurnController {
    TurnService turnService;
    TurnResultMapper turnResultMapper;

    @PostMapping
    public ResponseEntity<TurnResultDto> makeTurn(
            @PathVariable UUID gameId,
            @RequestHeader("X-User-Id") String userId) {
        TurnResult turnResult = turnService.makeTurn(gameId, toUUID(userId));
        return ResponseEntity.ok(turnResultMapper.toDto(turnResult));
    }
    private static UUID toUUID (String uuid) {
        return UUID.fromString(uuid);
    }
}
