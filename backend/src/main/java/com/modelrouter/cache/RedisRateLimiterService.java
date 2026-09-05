package com.modelrouter.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRateLimiterService {

    private final StringRedisTemplate redisTemplate;

    public boolean isAllowed(String orgId, int maxRpm) {
        if (orgId == null || orgId.isBlank() || redisTemplate == null) {
            return true;
        }

        try {
            long currentWindow = Instant.now().getEpochSecond() / 60;
            String key = "rate:org:" + orgId + ":" + currentWindow;

            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1) {
                redisTemplate.expire(key, Duration.ofMinutes(2));
            }

            if (count != null && count > maxRpm) {
                log.warn("Rate limit exceeded for org {}: {} requests in current minute (max: {})", orgId, count, maxRpm);
                return false;
            }
        } catch (Exception e) {
            log.warn("Redis rate limiter error: {}, allowing request", e.getMessage());
        }

        return true;
    }
}
