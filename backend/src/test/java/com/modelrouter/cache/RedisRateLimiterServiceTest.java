package com.modelrouter.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class RedisRateLimiterServiceTest {

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private RedisRateLimiterService rateLimiterService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redisTemplate = Mockito.mock(StringRedisTemplate.class);
        valueOperations = Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        rateLimiterService = new RedisRateLimiterService(redisTemplate);
    }

    @Test
    void testRequestAllowedUnderLimit() {
        when(valueOperations.increment(anyString())).thenReturn(5L);

        boolean allowed = rateLimiterService.isAllowed("org-test-1", 100);
        assertTrue(allowed);
    }

    @Test
    void testRequestBlockedOverLimit() {
        when(valueOperations.increment(anyString())).thenReturn(101L);

        boolean allowed = rateLimiterService.isAllowed("org-test-1", 100);
        assertFalse(allowed);
    }
}
