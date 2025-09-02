package com.example.jwt_auth.services;

import com.example.jwt_auth.entites.RedisRefreshToken;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> valueOperations;

    // TTL VALUE (7 Days)
    private static final Duration TOKEN_TTL = Duration.ofDays(7);

    // redis collection prefix
    private static final String KEY_PREFIX = "refresh_token:";

    public RefreshTokenService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        this.valueOperations = redisTemplate.opsForValue();
    }

    private String buildKey(String key) {
        return KEY_PREFIX + key;
    }

    public void save(String key, RedisRefreshToken refreshToken) {
        valueOperations.set(buildKey(key), refreshToken, TOKEN_TTL);
    }

    public Optional<RedisRefreshToken> findByToken(String refreshToken) {
        return Optional.ofNullable((RedisRefreshToken) valueOperations.get(buildKey(refreshToken)));
    }

    public void invalidate(String key) {
        RedisRefreshToken redisRefreshToken = (RedisRefreshToken) valueOperations.get(buildKey(key));
        if (redisRefreshToken != null) {
            redisRefreshToken.setValid(false);
            valueOperations.set(key, redisRefreshToken);
        }
    }
}
