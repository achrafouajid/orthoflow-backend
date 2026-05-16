package com.orthoflow.billing.application.dto;

import com.orthoflow.billing.domain.model.InvoiceStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class BillingSummaryResponse {
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal totalInvoiced;
    private BigDecimal totalCollected;
    private BigDecimal outstandingAmount;
    private Integer invoiceCount;
    private Map<InvoiceStatus, Long> byStatus;
}
