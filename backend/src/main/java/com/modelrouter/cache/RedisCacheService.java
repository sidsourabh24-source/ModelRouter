package com.modelrouter.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modelrouter.routing.InferenceRequest;
import com.modelrouter.routing.InferenceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${modelrouter.cache.enabled:true}")
    private boolean cacheEnabled;

    @Value("${modelrouter.cache.ttl-minutes:60}")
    private long ttlMinutes;

    public InferenceResponse getCachedResponse(InferenceRequest request) {
        if (!cacheEnabled || redisTemplate == null) {
            return null;
        }

        try {
            String key = buildCacheKey(request);
            String json = redisTemplate.opsForValue().get(key);
            if (json != null && !json.isBlank()) {
                InferenceResponse cachedResponse = objectMapper.readValue(json, InferenceResponse.class);
                cachedResponse.setCacheHit(true);
                log.info("Redis cache hit for key {}", key);
                return cachedResponse;
            }
        } catch (Exception e) {
            log.warn("Redis cache read error: {}", e.getMessage());
        }

        return null;
    }

    public void putResponseInCache(InferenceRequest request, InferenceResponse response) {
        if (!cacheEnabled || redisTemplate == null || response == null) {
            return;
        }

        try {
            String key = buildCacheKey(request);
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(ttlMinutes));
            log.info("Cached response in Redis for key {}", key);
        } catch (Exception e) {
            log.warn("Redis cache write error: {}", e.getMessage());
        }
    }

    public String buildCacheKey(InferenceRequest request) {
        StringBuilder raw = new StringBuilder();
        if (request.getMode() != null) {
            raw.append("mode=").append(request.getMode().toUpperCase()).append(";");
        }
        if (request.getMessages() != null) {
            for (InferenceRequest.ChatMessage msg : request.getMessages()) {
                raw.append(msg.getRole()).append(":").append(msg.getContent()).append(";");
            }
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.toString().getBytes(StandardCharsets.UTF_8));
            return "cache:chat:" + HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            return "cache:chat:" + Math.abs(raw.toString().hashCode());
        }
    }
}
