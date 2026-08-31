package com.orthoflow.procurement.application.dto;

import com.orthoflow.procurement.domain.model.DeliveryNoteLine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import com.orthoflow.inventory.application.dto.StockItemResponse;

public record DeliveryNoteLineResponse(
        UUID id,
        PurchaseOrderLineResponse poLine,
        StockItemResponse stockItem,
        BigDecimal quantityExpected,
        BigDecimal quantityReceived,
        String batchNumber,
        LocalDate expiryDate
) {
    public static DeliveryNoteLineResponse from(DeliveryNoteLine line) {
        if (line == null) return null;
        return new DeliveryNoteLineResponse(
                line.getId(),
                PurchaseOrderLineResponse.from(line.getPoLine()),
                StockItemResponse.from(line.getStockItem()),
                line.getQuantityExpected(),
                line.getQuantityReceived(),
                line.getBatchNumber(),
                line.getExpiryDate()
        );
    }
}
