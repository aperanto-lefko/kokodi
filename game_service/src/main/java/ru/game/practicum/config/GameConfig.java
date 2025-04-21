package ru.game.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.game.practicum.feign.AuthServiceClient;

@Configuration
public class GameConfig {
    @Bean
    public AuthServiceClient authServiceClient() {
        // Реализация клиента для auth-сервиса
        return new AuthServiceClient();
    }

}
