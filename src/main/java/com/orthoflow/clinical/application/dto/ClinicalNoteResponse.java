package com.orthoflow.clinical.application.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ClinicalNoteResponse(
        UUID id,
        String category,
        String content,
        String fdi,
        UUID authorId,
        String source,
        UUID sessionId,
        OffsetDateTime createdAt
) {}
