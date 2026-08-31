package com.orthoflow.inventory.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * One entry of the raw-array body sent to PUT /stock/count-sessions/{id}/lines
 * (see updateCountSessionLines() in stock.service.ts and
 * saveDraftCounts()/postReconciliation() in stock-dashboard.component.ts).
 */
@Getter
@Setter
public class CountSessionLineUpdateRequest {

    @NotNull
    private UUID stockItemId;

    @NotNull
    private BigDecimal physicalQuantity;

    private String notes;
}
