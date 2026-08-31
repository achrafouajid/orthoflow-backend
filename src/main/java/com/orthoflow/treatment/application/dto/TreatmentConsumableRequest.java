package com.orthoflow.treatment.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import com.orthoflow.common.web.IdReference;

@Getter
@Setter
public class TreatmentConsumableRequest {

    @NotNull
    @Valid
    private IdReference stockItem;

    @NotNull
    @Positive
    private BigDecimal quantityUsed;

    private boolean optional = false;

    private String notes;
}
