package ru.game.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Login cannot be blank")
    private String login;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @NotBlank(message = "Name cannot be blank")
    private String name;
}
