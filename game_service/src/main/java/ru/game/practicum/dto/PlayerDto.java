package ru.game.practicum.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PlayerDto {
    private UUID id;
    private String userId;
    private Integer score;
    private boolean blocked;
}
