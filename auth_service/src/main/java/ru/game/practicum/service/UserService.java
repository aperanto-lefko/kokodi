package ru.game.practicum.service;

import ru.game.practicum.dto.auth_service.RegisterRequest;
import ru.game.practicum.entity.User;

public interface UserService {
    User register(RegisterRequest request);
}
