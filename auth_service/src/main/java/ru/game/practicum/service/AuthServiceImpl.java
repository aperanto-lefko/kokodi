package ru.game.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.game.practicum.dto.auth_service.AuthRequest;
import ru.game.practicum.dto.auth_service.AuthResponse;
import ru.game.practicum.entity.User;
import ru.game.practicum.exception.InvalidCredentialsException;
import ru.game.practicum.exception.UserNotFoundException;
import ru.game.practicum.repository.UserRepository;
import ru.game.practicum.security.JwtProvider;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        log.info("Attempting to authenticate user: {}", request.getLogin());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtProvider.generateToken(authentication);
            log.info("Authentication successful for user: {}", request.getLogin());
            return new AuthResponse(token);
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", request.getLogin(), e);
            throw new InvalidCredentialsException();
        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
