package com.orthoflow.treatment.application.dto;

import com.orthoflow.treatment.domain.model.SalesOrderLine;

import java.math.BigDecimal;
import java.util.UUID;
import com.orthoflow.inventory.application.dto.StockItemResponse;

public record SalesOrderLineResponse(
        UUID id,
        StockItemResponse stockItem,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal discount
) {
    public static SalesOrderLineResponse from(SalesOrderLine line) {
        if (line == null) return null;
        return new SalesOrderLineResponse(
                line.getId(),
                StockItemResponse.from(line.getStockItem()),
                line.getQuantity(),
                line.getUnitPrice(),
                line.getDiscount()
        );
    }
}
