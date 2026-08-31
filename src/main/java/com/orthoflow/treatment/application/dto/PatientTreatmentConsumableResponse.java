package com.orthoflow.treatment.application.dto;

import com.orthoflow.treatment.domain.model.PatientTreatmentConsumable;

import java.math.BigDecimal;
import java.util.UUID;
import com.orthoflow.inventory.application.dto.StockItemResponse;

public record PatientTreatmentConsumableResponse(
        UUID id,
        StockItemResponse stockItem,
        BigDecimal quantityUsed,
        BigDecimal pricePerUnit,
        String notes
) {
    public static PatientTreatmentConsumableResponse from(PatientTreatmentConsumable c) {
        if (c == null) return null;
        return new PatientTreatmentConsumableResponse(
                c.getId(),
                StockItemResponse.from(c.getStockItem()),
                c.getQuantityUsed(),
                c.getPricePerUnit(),
                c.getNotes()
        );
    }
}
