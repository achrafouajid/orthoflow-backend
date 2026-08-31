package com.orthoflow.voice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * One line in the voice audit trail. Posted for every interpreted command,
 * including the ones that were refused, failed, or answered with a question —
 * what the system heard and chose not to do is the half that matters when an
 * entry in a clinical record is later disputed.
 */
@Getter
@Setter
public class RecordVoiceCommandRequest {

    private UUID patientId;
    private UUID sessionId;

    /** Dropped server-side when orthoflow.voice.audit.store-transcript is false. */
    private String transcript;

    @Size(max = 12)
    private String locale;

    @NotBlank
    @Size(max = 64)
    private String intent;

    /** Resolved entities, serialised as a JSON object by the caller. */
    private String entities;

    /** grammar | llm | manual */
    @NotBlank
    private String resolver;

    private Double confidence;

    @Size(max = 40)
    private String module;

    /** SAFE | CONFIRM | BLOCKED */
    @NotBlank
    private String riskTier;

    /** AUTO | CONFIRMED | REJECTED | CANCELLED | PENDING */
    @NotBlank
    private String confirmationStatus;

    /** EXECUTED | REJECTED | FAILED | CLARIFICATION | UNDONE */
    @NotBlank
    private String outcome;

    @Size(max = 48)
    private String targetType;

    private String targetId;

    private String previousValue;
    private String newValue;
    private String errorMessage;
}
