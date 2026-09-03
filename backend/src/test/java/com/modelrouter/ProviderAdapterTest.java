package com.modelrouter;

import com.modelrouter.provider.*;
import com.modelrouter.routing.InferenceRequest;
import com.modelrouter.routing.InferenceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProviderAdapterTest {

    private MockProviderAdapter mockAdapter;
    private OpenAiProviderAdapter openAiAdapter;
    private AnthropicProviderAdapter anthropicAdapter;
    private Model testModel;
    private InferenceRequest testRequest;

    @BeforeEach
    void setUp() {
        mockAdapter = new MockProviderAdapter();
        openAiAdapter = new OpenAiProviderAdapter();
        anthropicAdapter = new AnthropicProviderAdapter();

        testModel = Model.builder()
                .id("model-gpt-4o")
                .providerId("prov-openai")
                .name("gpt-4o")
                .inputPricePer1k(BigDecimal.valueOf(0.0025))
                .outputPricePer1k(BigDecimal.valueOf(0.0100))
                .qualityScore(BigDecimal.valueOf(0.95))
                .latencyScore(BigDecimal.valueOf(0.85))
                .reliabilityScore(BigDecimal.valueOf(0.99))
                .build();

        testRequest = InferenceRequest.builder()
                .organizationId("org-test")
                .mode("BALANCED")
                .messages(List.of(
                        InferenceRequest.ChatMessage.builder()
                                .role("user")
                                .content("What is AI model routing?")
                                .build()
                ))
                .maxTokens(100)
                .build();
    }

    @Test
    void testMockProviderAdapterExecution() {
        assertEquals("prov-mock", mockAdapter.getProviderId());
        assertTrue(mockAdapter.isHealthy());
        assertTrue(mockAdapter.supportsCapability("chat"));

        InferenceResponse response = mockAdapter.executeInference(testModel, testRequest);
        assertNotNull(response);
        assertNotNull(response.getRequestId());
        assertEquals("Mock Provider", response.getProvider());
        assertNotNull(response.getUsage());
        assertTrue(response.getUsage().getEstimatedCost().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testOpenAiProviderAdapterContract() {
        assertEquals("prov-openai", openAiAdapter.getProviderId());
        assertEquals("OpenAI", openAiAdapter.getProviderName());
        assertTrue(openAiAdapter.supportsCapability("code"));

        InferenceResponse response = openAiAdapter.executeInference(testModel, testRequest);
        assertNotNull(response);
        assertEquals("OpenAI", response.getProvider());
        assertTrue(response.getContent().contains("OpenAI"));
    }

    @Test
    void testAnthropicProviderAdapterContract() {
        assertEquals("prov-anthropic", anthropicAdapter.getProviderId());
        assertEquals("Anthropic", anthropicAdapter.getProviderName());
        assertTrue(anthropicAdapter.supportsCapability("writing"));

        InferenceResponse response = anthropicAdapter.executeInference(testModel, testRequest);
        assertNotNull(response);
        assertEquals("Anthropic", response.getProvider());
        assertTrue(response.getContent().contains("Anthropic"));
    }
}
