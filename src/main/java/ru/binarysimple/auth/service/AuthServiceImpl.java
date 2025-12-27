package ru.binarysimple.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.binarysimple.auth.dto.AuthResponse;
import ru.binarysimple.auth.dto.LoginRequest;
import ru.binarysimple.auth.dto.RegisterRequest;
import ru.binarysimple.auth.dto.UserInfo;
import ru.binarysimple.auth.model.RefreshToken;
import ru.binarysimple.auth.model.User;
import ru.binarysimple.auth.repository.UserRepository;
import ru.binarysimple.auth.security.JwtTokenProvider;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse login(LoginRequest request) {
        // Аутентификация
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Генерация токена
        String token = jwtTokenProvider.generateToken(auth);
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

//        // Получение информации о пользователе
//        User user = userRepository.findByUsername(request.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"));

        // Создание ответа
        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        response.setRefreshToken(refreshToken.getToken());
        response.setUserInfo(mapToUserInfo(user));

        return response;
    }

    public AuthResponse register(RegisterRequest request) {
        // Проверка существования пользователя
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Создание нового пользователя
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(new HashSet<>(Set.of("USER")));

        userRepository.save(user);

        // Автоматический логин после регистрации
        LoginRequest loginRequest = new LoginRequest(request.getUsername(), request.getPassword());
        return login(loginRequest);
    }

    public Boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public UserInfo getUserInfo(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserInfo(user);
    }

    public UserInfo mapToUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRoles(user.getRoles());
        return userInfo;
    }
}
