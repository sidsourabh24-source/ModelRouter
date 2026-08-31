package com.modelrouter.routing;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "routing_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutingRequest {

    @Id
    private String id;

    @Column(name = "organization_id")
    private String organizationId;

    @Column(name = "request_id", nullable = false, unique = true)
    private String requestId;

    @Column(name = "selected_model_id")
    private String selectedModelId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String mode;

    @Column(name = "latency_ms", nullable = false)
    private Integer latencyMs;

    @Column(name = "input_tokens", nullable = false)
    private Integer inputTokens;

    @Column(name = "output_tokens", nullable = false)
    private Integer outputTokens;

    @Column(name = "estimated_cost", nullable = false, precision = 10, scale = 6)
    private BigDecimal estimatedCost;

    @Builder.Default
    @Column(name = "cache_hit")
    private Boolean cacheHit = false;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;
}
