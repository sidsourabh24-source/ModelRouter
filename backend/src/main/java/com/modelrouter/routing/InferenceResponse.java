package com.modelrouter.routing;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InferenceResponse {

    private String requestId;
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
        private BigDecimal estimatedCost;
        private Long latencyMs;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoutingTrace {
        private String mode;
        private Double calculatedScore;
        private String reason;
    }
}
