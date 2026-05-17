package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface StockItemJpaRepository extends JpaRepository<StockItem, UUID> {
    Optional<StockItem> findBySku(String sku);
}
