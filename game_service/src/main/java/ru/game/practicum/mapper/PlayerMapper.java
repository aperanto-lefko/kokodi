package ru.game.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.game.practicum.dto.game_service.PlayerDto;
import ru.game.practicum.entity.Player;
@Mapper(componentModel = "spring")
public interface PlayerMapper {
    PlayerDto toDto(Player player);
    Player toEntity(PlayerDto playerDto);
}
