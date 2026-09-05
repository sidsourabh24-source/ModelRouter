package com.modelrouter.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modelrouter.routing.InferenceRequest;
import com.modelrouter.routing.InferenceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class RedisCacheServiceTest {

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private ObjectMapper objectMapper;
    private RedisCacheService redisCacheService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redisTemplate = Mockito.mock(StringRedisTemplate.class);
        valueOperations = Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        objectMapper = new ObjectMapper();

        redisCacheService = new RedisCacheService(redisTemplate, objectMapper);
    }

    @Test
    void testCacheKeyGenerationConsistency() {
        InferenceRequest req1 = InferenceRequest.builder()
                .mode("CHEAP")
                .messages(List.of(InferenceRequest.ChatMessage.builder().role("user").content("Hello World").build()))
                .build();

        InferenceRequest req2 = InferenceRequest.builder()
                .mode("cheap")
                .messages(List.of(InferenceRequest.ChatMessage.builder().role("user").content("Hello World").build()))
                .build();

        String key1 = redisCacheService.buildCacheKey(req1);
        String key2 = redisCacheService.buildCacheKey(req2);

        assertNotNull(key1);
        assertTrue(key1.startsWith("cache:chat:"));
        assertEquals(key1, key2);
    }

    @Test
    void testCacheHitReturnsDeserializedResponse() throws Exception {
        InferenceRequest request = InferenceRequest.builder()
                .mode("FAST")
                .messages(List.of(InferenceRequest.ChatMessage.builder().role("user").content("Test Cache").build()))
                .build();

        InferenceResponse expectedResponse = InferenceResponse.builder()
                .requestId("req-cached-1")
                .model("gpt-4o")
                .provider("OpenAI")
                .content("Cached answer")
                .cacheHit(false)
                .build();

        String json = objectMapper.writeValueAsString(expectedResponse);
        when(valueOperations.get(anyString())).thenReturn(json);

        InferenceResponse cachedResult = redisCacheService.getCachedResponse(request);

        assertNotNull(cachedResult);
        assertTrue(cachedResult.getCacheHit());
        assertEquals("gpt-4o", cachedResult.getModel());
    }
}
