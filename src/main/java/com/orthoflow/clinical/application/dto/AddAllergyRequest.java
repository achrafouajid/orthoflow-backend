package com.orthoflow.clinical.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddAllergyRequest {

    @NotBlank
    @Size(max = 160)
    private String substance;

    private String reaction;

    /** MILD | MODERATE | SEVERE, or null. */
    @Size(max = 16)
    private String severity;

    @NotBlank
    @Size(max = 20)
    private String source;

    private UUID sessionId;
}
