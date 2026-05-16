package com.orthoflow.billing.application.dto;

import com.orthoflow.billing.domain.model.PaymentMethod;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class RecordPaymentRequest {
    private BigDecimal amount;
    private PaymentMethod method;
    private LocalDate paymentDate;
    private String reference;
    private String notes;
}
