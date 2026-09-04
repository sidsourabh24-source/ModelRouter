package com.modelrouter.routing;

import com.modelrouter.classifier.TaskClassificationResult;
import com.modelrouter.classifier.TaskClassifierService;
import com.modelrouter.provider.Model;
import com.modelrouter.provider.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutingEngineService {

    private final ModelRepository modelRepository;
    private final RoutingRequestRepository routingRequestRepository;
    private final TaskClassifierService taskClassifierService;
    private final CandidateFilterEngine candidateFilterEngine;
    private final FallbackExecutionEngine fallbackExecutionEngine;

    public InferenceResponse routeAndExecute(InferenceRequest request) {
        // 1. Task & Complexity Classification (Day 11)
        TaskClassificationResult classification = taskClassifierService.classify(request);

        // 2. Fetch & Filter Candidate Models (Day 12)
        List<Model> allModels = modelRepository.findByStatus("ACTIVE");
        if (allModels.isEmpty()) {
            Model mockModel = Model.builder()
                    .id("model-mock-cheap")
                    .providerId("prov-mock")
                    .name("mock-cheap-v1")
                    .capabilities("chat,code,reasoning,writing")
                    .contextLimit(32000)
                    .inputPricePer1k(java.math.BigDecimal.valueOf(0.00010))
                    .outputPricePer1k(java.math.BigDecimal.valueOf(0.00020))
                    .qualityScore(java.math.BigDecimal.valueOf(0.65))
                    .latencyScore(java.math.BigDecimal.valueOf(0.95))
                    .reliabilityScore(java.math.BigDecimal.valueOf(0.99))
                    .status("ACTIVE")
                    .build();
            allModels = List.of(mockModel);
        }

        List<Model> candidates = candidateFilterEngine.filterCandidates(allModels, classification);

        // 3. Multi-Objective Ranked Model Selection (Day 13)
        String mode = request.getMode() != null ? request.getMode().toUpperCase() : "BALANCED";
        List<Model> rankedCandidates = candidates.stream()
                .sorted(Comparator.comparingDouble((Model m) -> calculateScore(m, mode, classification)).reversed())
                .collect(Collectors.toList());

        Model primaryModel = rankedCandidates.get(0);
        double topScore = calculateScore(primaryModel, mode, classification);

        // 4. Fallback Execution (Day 14)
        InferenceResponse response = fallbackExecutionEngine.executeWithFallback(rankedCandidates, request);

        // 5. Decision Trace & Explainability (Day 15)
        List<InferenceResponse.CandidateEvaluation> evaluations = rankedCandidates.stream()
                .map(m -> InferenceResponse.CandidateEvaluation.builder()
                        .modelId(m.getId())
                        .modelName(m.getName())
                        .score(calculateScore(m, mode, classification))
                        .build())
                .collect(Collectors.toList());

        String reason = String.format(
                "Selected model '%s' (Provider: %s) for mode '%s' and task '%s' (Complexity: %.2f) with score %.3f.",
                response.getModel(), response.getProvider(), mode, classification.getCategory(), classification.getComplexityScore(), topScore
        );

        response.setRouting(InferenceResponse.RoutingTrace.builder()
                .mode(mode)
                .taskCategory(classification.getCategory().name())
                .complexityScore(classification.getComplexityScore())
                .candidateCount(candidates.size())
                .calculatedScore(topScore)
                .reason(reason)
                .evaluatedCandidates(evaluations)
                .build());

        // 6. Telemetry Persistence
        try {
            RoutingRequest telemetry = RoutingRequest.builder()
                    .id("req-" + UUID.randomUUID().toString().substring(0, 8))
                    .organizationId(request.getOrganizationId() != null ? request.getOrganizationId() : "org-demo-001")
                    .requestId(response.getRequestId() != null ? response.getRequestId() : response.getId())
                    .selectedModelId(primaryModel.getId())
                    .status("SUCCESS")
                    .mode(mode)
                    .latencyMs(response.getUsage() != null && response.getUsage().getLatencyMs() != null ? response.getUsage().getLatencyMs().intValue() : 50)
                    .inputTokens(response.getUsage() != null ? response.getUsage().getInputTokens() : 10)
                    .outputTokens(response.getUsage() != null ? response.getUsage().getOutputTokens() : 20)
                    .estimatedCost(response.getUsage() != null ? response.getUsage().getEstimatedCost() : java.math.BigDecimal.valueOf(0.0001))
                    .cacheHit(false)
                    .reason(reason)
                    .build();
            routingRequestRepository.save(telemetry);
        } catch (Exception e) {
            // Telemetry failure fallback
        }

        return response;
    }

    public Model selectBestModel(List<Model> candidates, String mode, TaskClassificationResult classification) {
        return candidates.stream()
                .max(Comparator.comparingDouble(model -> calculateScore(model, mode, classification)))
                .orElse(candidates.get(0));
    }

    public double calculateScore(Model model, String mode, TaskClassificationResult classification) {
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

        if (classification != null && classification.getCategory() != null) {
            switch (classification.getCategory()) {
                case CODE:
                case REASONING:
                    wQuality += 0.10;
                    wCost = Math.max(0.05, wCost - 0.10);
                    break;
                case CHAT:
                    wLatency += 0.10;
                    wQuality = Math.max(0.10, wQuality - 0.10);
                    break;
                default:
                    break;
            }
        }

        double qScore = model.getQualityScore() != null ? model.getQualityScore().doubleValue() : 0.80;
        double lScore = model.getLatencyScore() != null ? model.getLatencyScore().doubleValue() : 0.80;
        double rScore = model.getReliabilityScore() != null ? model.getReliabilityScore().doubleValue() : 0.99;
        
        double price = model.getInputPricePer1k() != null ? model.getInputPricePer1k().doubleValue() : 0.001;
        double cScore = Math.max(0.1, 1.0 - (price * 100.0));

        double capScore = 0.5;
        if (classification != null && model.getCapabilities() != null && 
            model.getCapabilities().toLowerCase().contains(classification.getRecommendedCapability().toLowerCase())) {
            capScore = 1.0;
        }

        return (wQuality * qScore) + (wLatency * lScore) + (wCost * cScore) + (wReliability * rScore) + (wCapability * capScore);
    }
}
