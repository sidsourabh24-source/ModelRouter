package com.modelrouter.auth;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "api_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKey {

    @Id
    private String id;

    @Column(name = "organization_id", nullable = false)
    private String organizationId;

    @Column(name = "key_hash", nullable = false, unique = true)
    private String keyHash;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "last_used_at")
    private ZonedDateTime lastUsedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;
}
