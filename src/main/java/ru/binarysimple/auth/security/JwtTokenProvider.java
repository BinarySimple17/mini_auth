package ru.binarysimple.auth.security;

import ru.binarysimple.auth.model.User;

public interface JwtTokenProvider {
    String generateToken(User user);
    String generateTokenFromUsername(String username);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
}
