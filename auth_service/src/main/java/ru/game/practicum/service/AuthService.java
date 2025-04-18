package ru.game.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.game.practicum.dto.AuthRequest;
import ru.game.practicum.dto.AuthResponse;
import ru.game.practicum.exception.InvalidCredentialsException;
import ru.game.practicum.repository.UserRepository;
import ru.game.practicum.security.JwtProvider;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public AuthResponse authenticate(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtProvider.generateToken(authentication);

            return new AuthResponse(token);
        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }
    }
}
