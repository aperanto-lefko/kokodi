package ru.game.practicum.mapper;

import org.mapstruct.Mapper;
import ru.game.practicum.dto.auth_service.UserDto;
import ru.game.practicum.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
