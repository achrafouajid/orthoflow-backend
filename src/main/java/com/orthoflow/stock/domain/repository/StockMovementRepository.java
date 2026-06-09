package com.orthoflow.stock.domain.repository;

import com.orthoflow.stock.domain.model.StockMovement;
import com.orthoflow.stock.domain.model.StockMovementFilter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockMovementRepository {
    StockMovement save(StockMovement stockMovement);
    Optional<StockMovement> findById(UUID id);
    /** Returns all movements — backward-compatible, unfiltered. */
    List<StockMovement> findAll();
    /** Returns movements matching the given filter criteria with server-side sort. */
    List<StockMovement> findAll(StockMovementFilter filter);
    List<StockMovement> findByStockItemId(UUID stockItemId);
    void deleteById(UUID id);
}
