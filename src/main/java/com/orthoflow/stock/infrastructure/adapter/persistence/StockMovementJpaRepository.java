package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.UUID;

public interface StockMovementJpaRepository
        extends JpaRepository<StockMovement, UUID>, JpaSpecificationExecutor<StockMovement> {
    List<StockMovement> findByStockItemId(UUID stockItemId);
}
