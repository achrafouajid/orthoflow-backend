package com.orthoflow.clinical.application.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record MedicalHistoryResponse(
        UUID id,
        String category,
        String label,
        String detail,
        String source,
        UUID sessionId,
        OffsetDateTime recordedAt
) {}
