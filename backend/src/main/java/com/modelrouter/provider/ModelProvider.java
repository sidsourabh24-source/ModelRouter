package com.modelrouter.provider;

import com.modelrouter.routing.InferenceRequest;
import com.modelrouter.routing.InferenceResponse;

/**
 * Strategy interface implemented by all model provider adapters
 * (Mock, OpenAI, Anthropic, Gemini, DeepSeek, etc.).
 */
public interface ModelProvider {

    /**
     * Unique identifier for the provider (e.g. "prov-mock", "prov-openai", "prov-anthropic").
     */
    String getProviderId();

    /**
     * Display name of the provider.
     */
    String getProviderName();

    /**
     * Checks whether the provider API is currently healthy and reachable.
     */
    boolean isHealthy();

    /**
     * Checks if this provider adapter supports a specific capability (e.g., "chat", "code", "vision", "reasoning").
     */
    default boolean supportsCapability(String capability) {
        return true;
    }

    /**
     * Executes AI inference request against the target model.
     *
     * @param model Target database model configuration (pricing, capabilities, latency scores)
     * @param request Unified incoming inference request payload
     * @return Standardized inference response with content, token usage, cost, and routing trace
     */
    InferenceResponse executeInference(Model model, InferenceRequest request);
}

