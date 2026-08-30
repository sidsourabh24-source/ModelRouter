package com.modelrouter.organization;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    private String plan = "FREE";

    @Column(name = "budget_limit")
    private BigDecimal budgetLimit;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;
}
