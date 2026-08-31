package com.orthoflow.procurement.application.dto;

import com.orthoflow.procurement.domain.model.PurchaseOrderLine;

import java.math.BigDecimal;
import java.util.UUID;
import com.orthoflow.inventory.application.dto.StockItemResponse;

public record PurchaseOrderLineResponse(
        UUID id,
        StockItemResponse stockItem,
        BigDecimal quantityOrdered,
        BigDecimal quantityReceived,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
    public static PurchaseOrderLineResponse from(PurchaseOrderLine line) {
        if (line == null) return null;
        return new PurchaseOrderLineResponse(
                line.getId(),
                StockItemResponse.from(line.getStockItem()),
                line.getQuantityOrdered(),
                line.getQuantityReceived(),
                line.getUnitPrice(),
                line.getTotalPrice()
        );
    }
}
