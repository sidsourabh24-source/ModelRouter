package com.modelrouter.provider;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provider {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "base_url")
    private String baseUrl;

    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;
}
