package com.orthoflow.clinical.application.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record AllergyResponse(
        UUID id,
        String substance,
        String reaction,
        String severity,
        String source,
        UUID sessionId,
        OffsetDateTime recordedAt
) {}
