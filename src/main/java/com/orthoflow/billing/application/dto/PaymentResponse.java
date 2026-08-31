package com.orthoflow.billing.application.dto;

import com.orthoflow.billing.domain.model.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record PaymentResponse(
        UUID id,
        BigDecimal amount,
        PaymentMethod method,
        LocalDate paymentDate,
        String reference) {
}
