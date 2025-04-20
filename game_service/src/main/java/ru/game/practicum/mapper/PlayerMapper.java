package ru.game.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.game.practicum.dto.game_service.PlayerDto;
import ru.game.practicum.entity.Player;
@Mapper(componentModel = "spring")
public interface PlayerMapper {
    @Mapping(target = "gameSession", ignore = true)  // Игнорируем в DTO
    PlayerDto toDto(Player player);

    @Mapping(target = "gameSession", ignore = true)  // Игнорируем при создании Entity
    Player toEntity(PlayerDto playerDto);
}
