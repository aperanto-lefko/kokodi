package ru.game.practicum.dto.auth_service;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String name;
    private String login;
}
