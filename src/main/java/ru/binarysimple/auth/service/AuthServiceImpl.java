package ru.binarysimple.auth.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.binarysimple.auth.client.UsersServiceClient;
import ru.binarysimple.auth.dto.*;
import ru.binarysimple.auth.mapper.UserMapper;
import ru.binarysimple.auth.model.RefreshToken;
import ru.binarysimple.auth.model.Roles;
import ru.binarysimple.auth.model.User;
import ru.binarysimple.auth.repository.UserRepository;
import ru.binarysimple.auth.security.JwtTokenProvider;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UsersServiceClient usersServiceClient;
    private final UserMapper mapper;

    public AuthResponse login(LoginRequest request) {
        logger.info("Login attempt for user: {}", request.getUsername());
        // Аутентификация
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

//        SecurityContextHolder.getContext().setAuthentication(auth);

        // Генерация токена
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtTokenProvider.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

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
        user.setRoles(new HashSet<>(Set.of(Roles.USER)));

        userRepository.save(user);
        logger.info("User registered successfully: {}", request.getUsername());

        CreateUserExternalDto userExternalDto = mapper.toCreateUserExternalDto(request);

        try {
            usersServiceClient.createUser(userExternalDto);
        } catch (Exception e) {
            logger.error("Error creating user in external service: {}", e.getMessage());
            userRepository.delete(user);
            throw new RuntimeException("Error creating user in external service");
        }

        // Автоматический логин после регистрации
        LoginRequest loginRequest = new LoginRequest(request.getUsername(), request.getPassword());
        return login(loginRequest);
    }

    public Boolean validateToken(String token) {
        boolean isValid = jwtTokenProvider.validateToken(token);
        if (isValid) {
            logger.debug("Token validation successful");
        } else {
            logger.debug("Token validation failed");
        }
        return isValid;
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
