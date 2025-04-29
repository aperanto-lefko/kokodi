package ru.game.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.game.practicum.dto.auth_service.UserDto;

import java.util.UUID;

@FeignClient(name = "auth-service",path = "/api/auth", fallback = AuthServiceFallback.class)
public interface AuthServiceClient {
    @GetMapping("/users/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable("userId") UUID userId);
}
