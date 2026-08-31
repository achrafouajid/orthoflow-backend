package com.orthoflow.reporting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TreatmentProfitabilityResponse {
    private UUID treatmentId;
    private String treatmentName;
    private BigDecimal totalRevenue;
    private BigDecimal totalMaterialCost;
    private BigDecimal grossMargin;
    private BigDecimal marginPercent;
    private long sessionCount;
}
