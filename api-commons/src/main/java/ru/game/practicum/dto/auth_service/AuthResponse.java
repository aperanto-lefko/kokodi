package ru.game.practicum.dto.auth_service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {
    private String token;

    @JsonCreator
    public AuthResponse(@JsonProperty("token") String token) {
        this.token = token;
    }
}
