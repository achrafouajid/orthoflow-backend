package com.orthoflow.voice.application.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record VoiceCommandAuditResponse(
        UUID id,
        UUID actorId,
        UUID patientId,
        UUID sessionId,
        OffsetDateTime occurredAt,
        String transcript,
        String locale,
        String intent,
        String entities,
        String resolver,
        Double confidence,
        String module,
        String riskTier,
        String confirmationStatus,
        String outcome,
        String targetType,
        String targetId,
        String previousValue,
        String newValue,
        String errorMessage,
        OffsetDateTime undoneAt
) {}
