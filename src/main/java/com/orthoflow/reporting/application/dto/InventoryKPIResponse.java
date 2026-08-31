package com.orthoflow.reporting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class InventoryKPIResponse {
    private BigDecimal currentInventoryValue;
    private long lowStockItemCount;
    private long outOfStockItemCount;
    private long expiringItemCount;
    private BigDecimal monthlyInventoryCost;
    private BigDecimal inventoryVariancePercent;
    private BigDecimal inventoryLossGainValue;
    private List<TopConsumedItemResponse> topConsumedItems;
}
