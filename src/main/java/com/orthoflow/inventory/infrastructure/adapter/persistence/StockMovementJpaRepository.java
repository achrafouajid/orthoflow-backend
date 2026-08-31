package com.orthoflow.inventory.infrastructure.adapter.persistence;

import com.orthoflow.inventory.domain.model.MovementType;
import com.orthoflow.inventory.domain.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface StockMovementJpaRepository
        extends JpaRepository<StockMovement, UUID>, JpaSpecificationExecutor<StockMovement> {
    List<StockMovement> findByStockItemId(UUID stockItemId);
    List<StockMovement> findByMovementTypeAndCreatedAtBetween(MovementType movementType, OffsetDateTime start, OffsetDateTime end);
}
