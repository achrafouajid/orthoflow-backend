package com.orthoflow.inventory.application.dto;

import com.orthoflow.inventory.domain.model.StockCategory;
import com.orthoflow.inventory.domain.model.StockItem;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response shape for StockItem — closes the "controllers return JPA
 * entities directly" half of audit I.5 that request DTOs (StockItemRequest)
 * didn't cover. Field names/types match frontend/src/app/core/models/
 * stock.model.ts StockItem exactly so this is a pure serialization-boundary
 * change, not a contract change.
 */
public record StockItemResponse(
        UUID id,
        String sku,
        String name,
        StockCategory category,
        String unit,
        String unitLabel,
        boolean decimalSupported,
        SupplierResponse supplier,
        BigDecimal currentStock,
        BigDecimal minimumStock,
        BigDecimal purchasePrice,
        BigDecimal unitSize,
        BigDecimal pricePerUse,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static StockItemResponse from(StockItem item) {
        if (item == null) return null;
        return new StockItemResponse(
                item.getId(),
                item.getSku(),
                item.getName(),
                item.getCategory(),
                item.getUnit(),
                item.getUnitLabel(),
                item.isDecimalSupported(),
                SupplierResponse.from(item.getSupplier()),
                item.getCurrentStock(),
                item.getMinimumStock(),
                item.getPurchasePrice(),
                item.getUnitSize(),
                item.getPricePerUse(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
