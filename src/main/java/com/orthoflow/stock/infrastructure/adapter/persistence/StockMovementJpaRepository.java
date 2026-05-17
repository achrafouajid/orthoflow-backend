package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface StockMovementJpaRepository extends JpaRepository<StockMovement, UUID> {
    List<StockMovement> findByStockItemId(UUID stockItemId);
}
