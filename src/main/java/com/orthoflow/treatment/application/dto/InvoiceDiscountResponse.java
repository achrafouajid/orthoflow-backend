package com.orthoflow.treatment.application.dto;

import com.orthoflow.treatment.domain.model.DiscountTarget;
import com.orthoflow.treatment.domain.model.DiscountType;
import com.orthoflow.treatment.domain.model.InvoiceDiscount;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceDiscountResponse(
        UUID id,
        DiscountType type,
        DiscountTarget target,
        UUID targetId,
        BigDecimal value,
        String reason
) {
    public static InvoiceDiscountResponse from(InvoiceDiscount d) {
        if (d == null) return null;
        return new InvoiceDiscountResponse(d.getId(), d.getType(), d.getTarget(), d.getTargetId(), d.getValue(), d.getReason());
    }
}
