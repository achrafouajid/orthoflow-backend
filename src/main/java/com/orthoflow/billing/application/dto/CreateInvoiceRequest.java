package com.orthoflow.billing.application.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreateInvoiceRequest {
    private UUID practiceId;
    private UUID patientId;
    private UUID treatmentPlanId;
    private String currency;
    private String regionCode;
    private String notes;
    private List<InvoiceLineRequest> lines;
}
