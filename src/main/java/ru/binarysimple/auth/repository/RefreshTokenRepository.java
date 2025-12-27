package ru.binarysimple.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.binarysimple.auth.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser_Id(Long userId);
}