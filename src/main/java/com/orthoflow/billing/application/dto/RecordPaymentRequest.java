package com.orthoflow.billing.application.dto;

import com.orthoflow.billing.domain.model.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecordPaymentRequest {

    @NotNull
    @DecimalMin(value = "0.0001", message = "amount must be greater than zero")
    private BigDecimal amount;

    @NotNull
    private PaymentMethod method;

    @NotNull
    @PastOrPresent(message = "paymentDate cannot be in the future")
    private LocalDate paymentDate;

    private String reference;

    private String notes;
}
