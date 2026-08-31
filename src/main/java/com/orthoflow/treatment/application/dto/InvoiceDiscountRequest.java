package com.orthoflow.treatment.application.dto;

import com.orthoflow.treatment.domain.model.DiscountTarget;
import com.orthoflow.treatment.domain.model.DiscountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class InvoiceDiscountRequest {

    @NotNull
    private DiscountType type;

    @NotNull
    private DiscountTarget target;

    /** Required when target == ITEM (references a stock item id); unused otherwise. */
    private UUID targetId;

    @NotNull
    @PositiveOrZero
    private BigDecimal value;

    private String reason;
}
