package ru.game.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.game.practicum.dto.auth_service.UserDto;
import ru.game.practicum.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "login", target = "login")
    @Mapping(source = "name", target = "name")
    UserDto toDto(User user);
}
