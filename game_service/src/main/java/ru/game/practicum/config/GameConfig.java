package ru.game.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfig {
    @Bean
    public AuthServiceClient authServiceClient() {
        // Реализация клиента для auth-сервиса
        return new AuthServiceClient();
    }

}
