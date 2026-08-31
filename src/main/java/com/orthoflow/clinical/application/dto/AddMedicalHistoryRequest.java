package com.orthoflow.clinical.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddMedicalHistoryRequest {

    /** CONDITION | MEDICATION | SURGERY | DENTAL_HISTORY | FAMILY | LIFESTYLE | OTHER */
    @NotBlank
    private String category;

    @NotBlank
    @Size(max = 240)
    private String label;

    private String detail;

    @NotBlank
    @Size(max = 20)
    private String source;

    private UUID sessionId;
}
