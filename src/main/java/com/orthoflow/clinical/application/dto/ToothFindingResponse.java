package com.orthoflow.clinical.application.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ToothFindingResponse(
        UUID id,
        String fdi,
        String findingCode,
        String kind,
        String surface,
        String severity,
        String note,
        String status,
        String source,
        UUID sessionId,
        OffsetDateTime createdAt
) {}
