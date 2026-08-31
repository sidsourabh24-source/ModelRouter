package com.modelrouter.routing;

import com.modelrouter.provider.Model;
import com.modelrouter.provider.ModelProvider;
import com.modelrouter.provider.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoutingEngineService {

    private final ModelRepository modelRepository;
    private final RoutingRequestRepository routingRequestRepository;
    private final List<ModelProvider> providerAdapters;

    public InferenceResponse routeAndExecute(InferenceRequest request) {
        List<Model> activeModels = modelRepository.findByStatus("ACTIVE");

        if (activeModels.isEmpty()) {
            // Fallback mock model if DB seed is not initialized
            Model mockModel = Model.builder()
                    .id("model-mock-cheap")
                    .providerId("prov-mock")
                    .name("mock-cheap-v1")
                    .capabilities("chat,code")
                    .contextLimit(32000)
                    .inputPricePer1k(java.math.BigDecimal.valueOf(0.00010))
                    .outputPricePer1k(java.math.BigDecimal.valueOf(0.00020))
                    .qualityScore(java.math.BigDecimal.valueOf(0.65))
                    .latencyScore(java.math.BigDecimal.valueOf(0.95))
                    .reliabilityScore(java.math.BigDecimal.valueOf(0.99))
                    .status("ACTIVE")
                    .build();
            activeModels = List.of(mockModel);
        }

        // 1. Calculate Score for each candidate model
        String mode = request.getMode() != null ? request.getMode().toUpperCase() : "BALANCED";
        Model selectedModel = selectBestModel(activeModels, mode);

        // 2. Select Adapter
        ModelProvider providerAdapter = providerAdapters.stream()
                .filter(adapter -> adapter.getProviderId().equals(selectedModel.getProviderId()))
                .findFirst()
                .orElseGet(() -> providerAdapters.stream()
                        .filter(a -> a.getProviderId().equals("prov-mock"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No provider adapter available")));

        // 3. Execute Inference
        InferenceResponse response = providerAdapter.executeInference(selectedModel, request);

        // 4. Attach Routing Decision Trace
        String reason = "Selected model '" + selectedModel.getName() + "' for mode '" + mode + "' based on weighted cost/latency/quality scoring.";
        response.setRouting(InferenceResponse.RoutingTrace.builder()
                .mode(mode)
                .calculatedScore(calculateScore(selectedModel, mode))
                .reason(reason)
                .build());

        // 5. Persist Telemetry
        try {
            RoutingRequest telemetry = RoutingRequest.builder()
                    .id("req-" + UUID.randomUUID().toString().substring(0, 8))
                    .organizationId(request.getOrganizationId() != null ? request.getOrganizationId() : "org-demo-001")
                    .requestId(response.getId())
                    .selectedModelId(selectedModel.getId())
                    .status("SUCCESS")
                    .mode(mode)
                    .latencyMs((int) response.getUsage().getLatencyMs())
                    .inputTokens(response.getUsage().getPromptTokens())
                    .outputTokens(response.getUsage().getCompletionTokens())
                    .estimatedCost(java.math.BigDecimal.valueOf(response.getUsage().getEstimatedCostUsd()))
                    .cacheHit(false)
                    .reason(reason)
                    .build();
            routingRequestRepository.save(telemetry);
        } catch (Exception e) {
            // Log telemetry error without failing inference call
        }

        return response;
    }

    public Model selectBestModel(List<Model> candidates, String mode) {
        return candidates.stream()
                .max(Comparator.comparingDouble(model -> calculateScore(model, mode)))
                .orElse(candidates.get(0));
    }

    public double calculateScore(Model model, String mode) {
        double wQuality = 0.35;
        double wLatency = 0.20;
        double wCost = 0.20;
        double wReliability = 0.20;
        double wCapability = 0.05;

        switch (mode) {
            case "CHEAP":
                wQuality = 0.10;
                wLatency = 0.10;
                wCost = 0.70;
                wReliability = 0.05;
                wCapability = 0.05;
                break;
            case "FAST":
                wQuality = 0.10;
                wLatency = 0.70;
                wCost = 0.10;
                wReliability = 0.05;
                wCapability = 0.05;
                break;
            case "QUALITY":
                wQuality = 0.70;
                wLatency = 0.10;
                wCost = 0.10;
                wReliability = 0.05;
                wCapability = 0.05;
                break;
            case "BALANCED":
            default:
                break;
        }

        double qScore = model.getQualityScore() != null ? model.getQualityScore().doubleValue() : 0.80;
        double lScore = model.getLatencyScore() != null ? model.getLatencyScore().doubleValue() : 0.80;
        double rScore = model.getReliabilityScore() != null ? model.getReliabilityScore().doubleValue() : 0.99;
        
        // Inverse cost score: lower price -> higher cost score
        double price = model.getInputPricePer1k() != null ? model.getInputPricePer1k().doubleValue() : 0.001;
        double cScore = Math.max(0.1, 1.0 - (price * 100.0));

        return (wQuality * qScore) + (wLatency * lScore) + (wCost * cScore) + (wReliability * rScore) + (wCapability * 0.90);
    }
}
