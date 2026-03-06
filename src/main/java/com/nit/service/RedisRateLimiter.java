package com.nit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisRateLimiter {

    private final StringRedisTemplate redisTemplate;

    private static final int LIMIT = 10;          // requests
    private static final int WINDOW_SECONDS = 60; // per minute

    public boolean allowRequest(Long userId) {

        String key = "rate:user:" + userId;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == null) return false;

        // first request → set TTL
        if (count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(WINDOW_SECONDS));
        }

        return count <= LIMIT;
    }
}
