package com.orthoflow.inventory.application.dto;

import com.orthoflow.inventory.domain.model.CountSessionLine;

import java.math.BigDecimal;
import java.util.UUID;

public record CountSessionLineResponse(
        UUID id,
        StockItemResponse stockItem,
        BigDecimal theoreticalQuantity,
        BigDecimal physicalQuantity,
        BigDecimal quantityVariance,
        BigDecimal costVariance,
        String notes
) {
    public static CountSessionLineResponse from(CountSessionLine line) {
        if (line == null) return null;
        return new CountSessionLineResponse(
                line.getId(),
                StockItemResponse.from(line.getStockItem()),
                line.getTheoreticalQuantity(),
                line.getPhysicalQuantity(),
                line.getQuantityVariance(),
                line.getCostVariance(),
                line.getNotes()
        );
    }
}
