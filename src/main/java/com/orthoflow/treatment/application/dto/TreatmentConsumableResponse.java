package com.orthoflow.treatment.application.dto;

import com.orthoflow.treatment.domain.model.TreatmentConsumable;

import java.math.BigDecimal;
import com.orthoflow.inventory.application.dto.StockItemResponse;

public record TreatmentConsumableResponse(
        StockItemResponse stockItem,
        BigDecimal quantityUsed,
        boolean optional,
        String notes
) {
    public static TreatmentConsumableResponse from(TreatmentConsumable tc) {
        if (tc == null) return null;
        return new TreatmentConsumableResponse(
                StockItemResponse.from(tc.getStockItem()),
                tc.getQuantityUsed(),
                tc.isOptional(),
                tc.getNotes()
        );
    }
}
