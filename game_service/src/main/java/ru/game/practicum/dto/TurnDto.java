package ru.game.practicum.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class TurnDto {
    private UUID id;
    private UUID playerId;
    private CardDto card;
    private Instant createdAt;
}
