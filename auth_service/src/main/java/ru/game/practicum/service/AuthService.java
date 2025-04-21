package ru.game.practicum.service;

import ru.game.practicum.dto.auth_service.AuthRequest;
import ru.game.practicum.dto.auth_service.AuthResponse;
import ru.game.practicum.entity.User;

import java.util.UUID;

public interface AuthService {
    AuthResponse authenticate(AuthRequest request);
    User getUserById(UUID userId);
}
