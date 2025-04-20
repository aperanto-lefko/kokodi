package ru.game.practicum.mapper;

import org.mapstruct.Mapper;
import ru.game.practicum.dto.game_service.TurnResultDto;
import ru.game.practicum.entity.TurnResult;

@Mapper(componentModel = "spring")
public interface TurnResultMapper {
    TurnResultDto toDto (TurnResult turnResult);
}
