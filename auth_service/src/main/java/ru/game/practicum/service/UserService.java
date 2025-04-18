package ru.game.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.game.practicum.dto.RegisterRequest;
import ru.game.practicum.entity.User;
import ru.game.practicum.exception.UserAlreadyExistsException;
import ru.game.practicum.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new UserAlreadyExistsException(request.getLogin());
        }

        User user = User.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        return userRepository.save(user);
    }
}
