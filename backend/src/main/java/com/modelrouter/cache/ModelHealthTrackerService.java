package com.modelrouter.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelHealthTrackerService {

    private final StringRedisTemplate redisTemplate;

    public void recordSuccess(String modelId) {
        if (modelId == null || redisTemplate == null) return;
        try {
            redisTemplate.delete("health:failures:" + modelId);
            redisTemplate.opsForValue().set("health:status:" + modelId, "HEALTHY");
        } catch (Exception e) {
            log.warn("Error recording success health for model {}: {}", modelId, e.getMessage());
        }
    }

    public void recordFailure(String modelId) {
        if (modelId == null || redisTemplate == null) return;
        try {
            String failKey = "health:failures:" + modelId;
            Long count = redisTemplate.opsForValue().increment(failKey);
            if (count != null && count == 1) {
                redisTemplate.expire(failKey, Duration.ofMinutes(5));
            }
            if (count != null && count >= 3) {
                redisTemplate.opsForValue().set("health:status:" + modelId, "UNHEALTHY", Duration.ofMinutes(5));
                log.warn("Model {} marked UNHEALTHY in Redis due to {} consecutive failures", modelId, count);
            }
        } catch (Exception e) {
            log.warn("Error recording failure health for model {}: {}", modelId, e.getMessage());
        }
    }

    public String getHealthStatus(String modelId) {
        if (modelId == null || redisTemplate == null) return "HEALTHY";
        try {
            String status = redisTemplate.opsForValue().get("health:status:" + modelId);
            return status != null ? status : "HEALTHY";
        } catch (Exception e) {
            return "HEALTHY";
        }
    }
}
