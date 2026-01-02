package ru.binarysimple.auth.service;

import ru.binarysimple.auth.model.RefreshToken;
import ru.binarysimple.auth.model.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken refreshToken(String token);
    void deleteAllUserTokens(User user);
    void revokeAllUserTokens(User user);
    void revokeToken(String token);
}
