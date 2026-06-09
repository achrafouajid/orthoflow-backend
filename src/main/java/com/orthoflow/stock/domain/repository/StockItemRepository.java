package com.orthoflow.stock.domain.repository;

import com.orthoflow.stock.domain.model.StockItem;
import com.orthoflow.stock.domain.model.StockItemFilter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockItemRepository {
    StockItem save(StockItem stockItem);
    Optional<StockItem> findById(UUID id);
    Optional<StockItem> findBySku(String sku);
    /** Returns all items — backward-compatible, unfiltered. */
    List<StockItem> findAll();
    /** Returns items matching the given filter criteria with server-side sort. */
    List<StockItem> findAll(StockItemFilter filter);
    void deleteById(UUID id);
}
