package ru.game.practicum.dto.game_service;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TurnResultDto {
    private TurnDto turn;
    private PlayerDto currentPlayer;
    private GameState gameState;
    private UUID nextPlayerId;
}
