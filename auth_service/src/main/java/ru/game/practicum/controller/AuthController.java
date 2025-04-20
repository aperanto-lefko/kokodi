package ru.game.practicum.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.game.practicum.dto.auth_service.AuthRequest;
import ru.game.practicum.dto.auth_service.AuthResponse;
import ru.game.practicum.dto.auth_service.RegisterRequest;
import ru.game.practicum.dto.auth_service.UserDto;
import ru.game.practicum.entity.User;
import ru.game.practicum.mapper.UserMapper;
import ru.game.practicum.service.AuthService;
import ru.game.practicum.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;
    UserService userService;
    UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable("userId") UUID userId) {
        User user = authService.getUserById(userId);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

}
