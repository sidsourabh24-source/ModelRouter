package com.modelrouter.routing;

import com.modelrouter.provider.Model;
import com.modelrouter.provider.ModelProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FallbackExecutionEngine {

    private final List<ModelProvider> providerAdapters;

    public InferenceResponse executeWithFallback(List<Model> rankedModels, InferenceRequest request) {
        if (rankedModels == null || rankedModels.isEmpty()) {
            throw new IllegalArgumentException("No candidate models available for execution.");
        }

        Exception lastException = null;

        for (int i = 0; i < rankedModels.size(); i++) {
            Model model = rankedModels.get(i);
            ModelProvider adapter = providerAdapters.stream()
                    .filter(a -> a.getProviderId().equals(model.getProviderId()))
                    .findFirst()
                    .orElseGet(() -> providerAdapters.stream()
                            .filter(a -> a.getProviderId().equals("prov-mock"))
                            .findFirst()
                            .orElse(null));

            if (adapter == null || !adapter.isHealthy()) {
                log.warn("Skipping unhealthy or missing adapter for model: {}", model.getName());
                continue;
            }

            try {
                InferenceResponse response = adapter.executeInference(model, request);
                if (i > 0) {
                    log.info("Fallback succeeded on attempt {} using model {}", i + 1, model.getName());
                }
                return response;
            } catch (Exception e) {
                log.error("Execution failed on model {} (attempt {}): {}", model.getName(), i + 1, e.getMessage());
                lastException = e;
            }
        }

        throw new RuntimeException("All provider candidates failed inference execution. Last error: " + 
                (lastException != null ? lastException.getMessage() : "Unknown"), lastException);
    }
}
