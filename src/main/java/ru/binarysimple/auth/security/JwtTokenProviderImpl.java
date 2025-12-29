package ru.binarysimple.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.binarysimple.auth.model.User;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProviderImpl.class);

    private final SecretKey secretKey;
    private final int jwtExpiration;

    public JwtTokenProviderImpl(@Value("${jwt.secret}") String secret,
                                @Value("${jwt.expiration}") int jwtExpiration) {

        String trimmedSecret = secret.trim();
        logger.info("JWT Secret (first 10 chars): {}...", trimmedSecret.substring(0, Math.min(10, trimmedSecret.length())));

        byte[] decodedKey = Decoders.BASE64.decode(trimmedSecret);
        logger.info("Decoded JWT secret length: {} bytes", decodedKey.length); // Должно быть 32
        logger.info(Arrays.toString(decodedKey));

        // Создаем безопасный ключ
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.jwtExpiration = jwtExpiration;
    }


    public String generateToken(User user) {
        logger.debug("Generating JWT token for user: {}", user.getUsername());
        String username = user.getUsername();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", user.getRoles());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .addClaims(extraClaims)
                .compact();
    }

    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        logger.debug("Extracting username from JWT token: {}", token);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        logger.debug("Validating JWT token: {}", token);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
