package com.orthoflow.inventory.application.dto;

import com.orthoflow.inventory.domain.model.MovementType;
import com.orthoflow.inventory.domain.model.SourceType;
import com.orthoflow.inventory.domain.model.StockMovement;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        StockItemResponse stockItem,
        MovementType movementType,
        BigDecimal quantity,
        BigDecimal quantityBefore,
        BigDecimal quantityAfter,
        SourceType sourceType,
        UUID sourceId,
        String sourceReference,
        String notes,
        UUID createdBy,
        OffsetDateTime createdAt
) {
    public static StockMovementResponse from(StockMovement m) {
        if (m == null) return null;
        return new StockMovementResponse(
                m.getId(),
                StockItemResponse.from(m.getStockItem()),
                m.getMovementType(),
                m.getQuantity(),
                m.getQuantityBefore(),
                m.getQuantityAfter(),
                m.getSourceType(),
                m.getSourceId(),
                m.getSourceReference(),
                m.getNotes(),
                m.getCreatedBy(),
                m.getCreatedAt()
        );
    }
}
