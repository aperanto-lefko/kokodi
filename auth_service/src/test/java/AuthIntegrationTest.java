import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import ru.game.practicum.AuthServiceApp;
import ru.game.practicum.dto.auth_service.AuthRequest;
import ru.game.practicum.dto.auth_service.AuthResponse;
import ru.game.practicum.dto.auth_service.RegisterRequest;
import ru.game.practicum.entity.User;
import ru.game.practicum.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = AuthServiceApp.class)
@AutoConfigureMockMvc
@Transactional
public class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterAndLogin() throws Exception {

        String login = "testuser_" + System.currentTimeMillis();
        String password = "password";

        RegisterRequest registerRequest = new RegisterRequest(login, password, "Test User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 2. Проверка сохранения в БД
        User user = userRepository.findByLogin(login).orElseThrow();
        assertTrue(passwordEncoder.matches(password, user.getPassword()));

        // 3. Логин
        AuthRequest authRequest = new AuthRequest(login, password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthResponse.class);

        assertNotNull(response.getToken());
    }
}
