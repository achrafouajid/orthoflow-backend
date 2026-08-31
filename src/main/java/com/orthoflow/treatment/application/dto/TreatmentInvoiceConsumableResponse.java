package com.orthoflow.treatment.application.dto;

import com.orthoflow.treatment.domain.model.TreatmentInvoiceConsumable;

import java.math.BigDecimal;
import java.util.UUID;
import com.orthoflow.inventory.application.dto.StockItemResponse;

public record TreatmentInvoiceConsumableResponse(
        UUID id,
        StockItemResponse stockItem,
        BigDecimal defaultQuantity,
        BigDecimal actualQuantity,
        BigDecimal pricePerUnit,
        BigDecimal totalCost,
        boolean modified
) {
    public static TreatmentInvoiceConsumableResponse from(TreatmentInvoiceConsumable c) {
        if (c == null) return null;
        return new TreatmentInvoiceConsumableResponse(
                c.getId(),
                StockItemResponse.from(c.getStockItem()),
                c.getDefaultQuantity(),
                c.getActualQuantity(),
                c.getPricePerUnit(),
                c.getTotalCost(),
                c.isModified()
        );
    }
}
