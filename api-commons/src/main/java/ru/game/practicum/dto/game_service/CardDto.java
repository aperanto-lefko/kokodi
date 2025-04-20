package ru.game.practicum.dto.game_service;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CardDto {
    private UUID id;
    private String name;
    private Integer value;
    private String type;
}
