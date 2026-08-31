package com.orthoflow.treatment.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PatientTreatmentConsumableRequest {

    @NotNull
    private UUID stockItemId;

    @NotNull
    @DecimalMin(value = "0.0001", message = "quantityUsed must be greater than zero")
    private BigDecimal quantityUsed;

    private String notes;
}
