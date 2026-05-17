package com.orthoflow.stock.domain.repository;

import com.orthoflow.stock.domain.model.StockMovement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockMovementRepository {
    StockMovement save(StockMovement stockMovement);
    Optional<StockMovement> findById(UUID id);
    List<StockMovement> findAll();
    List<StockMovement> findByStockItemId(UUID stockItemId);
    void deleteById(UUID id);
}
