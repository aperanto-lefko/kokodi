package ru.game.practicum.dto.game_service;

import lombok.Builder;
import lombok.Data;


import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GameSessionDto {
    private UUID id;
    private GameState state;
    private List<PlayerDto> players;
    private Integer deckSize;
    private Integer currentPlayerIndex;
}
