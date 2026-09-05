package com.modelrouter.routing;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InferenceResponse {

    private String requestId;
    private String id;
    private String model;
    private String provider;
    private String content;
    private Boolean cacheHit;
    private UsageMetrics usage;
    private RoutingTrace routing;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsageMetrics {
        private Integer inputTokens;
        private Integer outputTokens;
        private Integer promptTokens;
        private Integer completionTokens;
        private BigDecimal estimatedCost;
        private Double estimatedCostUsd;
        private Long latencyMs;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoutingTrace {
        private String mode;
        private String taskCategory;
        private Double complexityScore;
        private Integer candidateCount;
        private Double calculatedScore;
        private String reason;
        private List<CandidateEvaluation> evaluatedCandidates;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CandidateEvaluation {
        private String modelId;
        private String modelName;
        private Double score;
    }
}
