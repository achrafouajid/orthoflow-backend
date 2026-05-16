package com.orthoflow.billing.application.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class InvoiceLineResponse {
    private UUID id;
    private String actCode;
    private String label;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}
