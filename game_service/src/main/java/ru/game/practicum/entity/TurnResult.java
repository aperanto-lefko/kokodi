package ru.game.practicum.entity;

import lombok.Builder;
import lombok.Data;
import ru.game.practicum.dto.game_service.GameState;

import java.util.UUID;

@Data
@Builder
public class TurnResult {
    private Turn turn;
    private Player currentPlayer;
    private GameState gameState;
    private UUID nextPlayerId;
}

