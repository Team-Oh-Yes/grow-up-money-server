package com.ohyes.GrowUpMoney.domain.user.service;


import com.ohyes.GrowUpMoney.global.config.RedisConfig;
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

    public String createRefreshToken(String username){
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                "RT:" + username,
                refreshToken,
                REFRESH_TOKEN_EXPIRE_TIME,
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }

    public boolean validateRefreshToken(String username, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get("RT:" + username);
        return refreshToken.equals(storedToken);
    }

    public void deleteRefreshToken(String username) {
        redisTemplate.delete("RT:" + username);
    }
}
