package com.modelrouter.provider;

import com.modelrouter.routing.InferenceRequest;
import com.modelrouter.routing.InferenceResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Component
public class MockProviderAdapter implements ModelProvider {

    @Override
    public String getProviderId() {
        return "prov-mock";
    }

    @Override
    public String getProviderName() {
        return "Mock Provider";
    }

    @Override
    public boolean isHealthy() {
        return true;
    }

    @Override
    public InferenceResponse executeInference(Model model, InferenceRequest request) {
        long startTime = System.currentTimeMillis();

        // Calculate pseudo tokens based on request text length
        int inputTokens = request.getMessages().stream()
                .mapToInt(msg -> msg.getContent() != null ? msg.getContent().length() / 4 + 1 : 0)
                .sum();
        int outputTokens = request.getMaxTokens() != null ? Math.min(request.getMaxTokens(), 150) : 120;

        // Calculate cost based on model input/output rates per 1k tokens
        BigDecimal inputCost = model.getInputPricePer1k()
                .multiply(BigDecimal.valueOf(inputTokens))
                .divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
        BigDecimal outputCost = model.getOutputPricePer1k()
                .multiply(BigDecimal.valueOf(outputTokens))
                .divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
        BigDecimal estimatedCost = inputCost.add(outputCost);

        long latencyMs = System.currentTimeMillis() - startTime + 45;

        String generatedAnswer = "[ModelRouter Mock Response via " + model.getName() + "]: "
                + "Request processed successfully using mode '" + request.getMode() + "'.";

        return InferenceResponse.builder()
                .requestId("req-" + UUID.randomUUID().toString().substring(0, 8))
                .model(model.getName())
                .provider(getProviderName())
                .content(generatedAnswer)
                .cacheHit(false)
                .usage(InferenceResponse.UsageMetrics.builder()
                        .inputTokens(inputTokens)
                        .outputTokens(outputTokens)
                        .estimatedCost(estimatedCost)
                        .latencyMs(latencyMs)
                        .build())
                .build();
    }
}
