package com.orthoflow.voice.application.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record VoiceSessionResponse(
        UUID id,
        UUID patientId,
        UUID actorId,
        String status,
        String locale,
        String summary,
        OffsetDateTime startedAt,
        OffsetDateTime endedAt,
        OffsetDateTime confirmedAt
) {}
