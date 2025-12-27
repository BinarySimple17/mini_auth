package ru.binarysimple.auth.dto;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}