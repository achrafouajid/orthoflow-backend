package com.orthoflow.billing.application.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InvoiceLineRequest {
    private String actCode;
    private String label;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPct;
    private Integer sortOrder;
}
