package com.ohyes.GrowUpMoney.domain.auth.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String,String> redisTemplate;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;

    public String createRefreshToken(String username, String authorities) {

        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                "refreshToken:"+refreshToken,
                "RT:" + username + ":" + authorities,  // username:authorities 형식
                REFRESH_TOKEN_EXPIRE_TIME,
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }

    public String getUsernameByRefreshToken(String refreshToken) {
        String value = redisTemplate.opsForValue().get("refreshToken:"+refreshToken);
        if (value == null) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        // "RT:john:USER" → "john" 추출
        String[] parts = value.split(":");
        if (parts.length < 2) {
            throw new RuntimeException("Invalid token format");
        }
        return parts[1];  // username만 반환
    }

    public String getAuthoritiesByRefreshToken(String refreshToken) {
        String value = redisTemplate.opsForValue().get("refreshToken:"+refreshToken);
        if (value == null) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        // "RT:john:ROLE_USER,ROLE_ADMIN" → "ROLE_USER,ROLE_ADMIN" 추출
        String[] parts = value.split(":");
        return parts[2];
    }

    public boolean validateRefreshToken(String username, String refreshToken) {
        String storedValue = redisTemplate.opsForValue().get("refreshToken:" + refreshToken);

        if (storedValue == null) {
            return false;
        }

        boolean result = storedValue.startsWith("RT:" + username + ":");
        return result;
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete("refreshToken:"+refreshToken);
    }

}
