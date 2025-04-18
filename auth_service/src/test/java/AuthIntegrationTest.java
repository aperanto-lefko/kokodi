import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import ru.game.practicum.AuthServiceApp;
import ru.game.practicum.dto.AuthRequest;
import ru.game.practicum.dto.AuthResponse;
import ru.game.practicum.dto.RegisterRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = AuthServiceApp.class)
@ActiveProfiles("test") // Активируем профиль test
public class AuthIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testRegisterAndLogin() {
        // Register
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setLogin("testuser_" + System.currentTimeMillis()); // Уникальный логин
        registerRequest.setPassword("password");
        registerRequest.setName("Test User");

        ResponseEntity<Void> registerResponse = restTemplate.postForEntity(
                "/api/auth/register",
                registerRequest,
                Void.class
        );
        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());

        // Login
        AuthRequest authRequest = new AuthRequest();
        authRequest.setLogin(registerRequest.getLogin());
        authRequest.setPassword(registerRequest.getPassword());

        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                "/api/auth/login",
                authRequest,
                AuthResponse.class
        );
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody().getToken());
    }
}
