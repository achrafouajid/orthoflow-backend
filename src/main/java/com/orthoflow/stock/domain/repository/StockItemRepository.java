package com.orthoflow.stock.domain.repository;

import com.orthoflow.stock.domain.model.StockItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockItemRepository {
    StockItem save(StockItem stockItem);
    Optional<StockItem> findById(UUID id);
    Optional<StockItem> findBySku(String sku);
    List<StockItem> findAll();
    void deleteById(UUID id);
}
