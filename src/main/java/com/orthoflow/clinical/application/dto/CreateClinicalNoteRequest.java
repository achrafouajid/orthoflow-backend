package com.orthoflow.clinical.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateClinicalNoteRequest {

    /** GENERAL | CHIEF_COMPLAINT | OBSERVATION | DENTAL_HISTORY | MEDICAL_HISTORY | DIAGNOSIS | FOLLOW_UP | TREATMENT_PLAN */
    @NotBlank
    private String category;

    @NotBlank
    private String content;

    /** Only when the doctor explicitly attached the note to a tooth. */
    @Size(max = 3)
    private String fdi;

    @NotBlank
    @Size(max = 20)
    private String source;

    private UUID sessionId;
}
