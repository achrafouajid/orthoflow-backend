package com.orthoflow.treatment.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import com.orthoflow.common.web.IdReference;

@Getter
@Setter
public class SalesOrderLineRequest {

    @NotNull
    @Valid
    private IdReference stockItem;

    @NotNull
    @Positive
    private BigDecimal quantity;

    /**
     * Optional — falls back to the stock item's purchase price server-side
     * if omitted, matching prior behaviour in SalesOrderService.
     */
    @PositiveOrZero
    private BigDecimal unitPrice;

    /** Percentage, 0-100 (see SalesOrderLine.calculateLineTotal). */
    @DecimalMin("0")
    @DecimalMax("100")
    private BigDecimal discount;
}
