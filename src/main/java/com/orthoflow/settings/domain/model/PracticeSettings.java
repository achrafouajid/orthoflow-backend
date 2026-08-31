package com.orthoflow.settings.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Singleton row (audit VIII.6 / P2 #29): the app is single-tenant-per-
 * deployment (ADR 0002), so there is exactly one practice's working hours
 * to configure, not a table keyed by practice id.
 */
@Entity
@Table(name = "practice_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PracticeSettings {

    public static final UUID SINGLETON_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Id
    private UUID id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "working_hours_start", nullable = false)
    private Short workingHoursStart;

    @Column(name = "working_hours_end", nullable = false)
    private Short workingHoursEnd;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @PrePersist
    @PreUpdate
    public void touch() {
        updatedAt = OffsetDateTime.now();
    }
}
