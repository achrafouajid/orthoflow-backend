package com.orthoflow.reporting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TopConsumedItemResponse {
    private UUID stockItemId;
    private String stockItemName;
    private BigDecimal totalConsumed;
    private BigDecimal totalCost;
}
