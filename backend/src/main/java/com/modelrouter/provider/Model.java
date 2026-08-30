package com.modelrouter.provider;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "models")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Model {

    @Id
    private String id;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(nullable = false)
    private String name;

    private String capabilities;

    @Column(name = "context_limit", nullable = false)
    private Integer contextLimit;

    @Column(name = "input_price_per_1k", nullable = false)
    private BigDecimal inputPricePer1k;

    @Column(name = "output_price_per_1k", nullable = false)
    private BigDecimal outputPricePer1k;

    @Column(name = "quality_score")
    private BigDecimal qualityScore;

    @Column(name = "latency_score")
    private BigDecimal latencyScore;

    @Column(name = "reliability_score")
    private BigDecimal reliabilityScore;

    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;
}
