package com.orthoflow.procurement.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import com.orthoflow.common.web.IdReference;

@Getter
@Setter
public class PurchaseOrderLineRequest {

    @NotNull
    @Valid
    private IdReference stockItem;

    @NotNull
    @Positive
    private BigDecimal quantityOrdered;

    @NotNull
    @PositiveOrZero
    private BigDecimal unitPrice;
}
