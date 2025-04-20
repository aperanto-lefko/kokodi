package ru.game.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.game.practicum.dto.auth_service.UserDto;

import java.util.UUID;

@FeignClient(name = "auth-service",fallback = AuthServiceFallback.class)
public interface AuthServiceClient {
    @GetMapping("/api/users/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable("userId") UUID userId);
}
