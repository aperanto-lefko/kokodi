package ru.game.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.game.practicum.dto.auth_service.RegisterRequest;
import ru.game.practicum.entity.User;
import ru.game.practicum.exception.UserAlreadyExistsException;
import ru.game.practicum.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new UserAlreadyExistsException(request.getLogin());
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .login(request.getLogin())
                .password(encodedPassword)
                .name(request.getName())
                .build();

        return userRepository.save(user);
    }
}
