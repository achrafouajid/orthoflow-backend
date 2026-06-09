package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;
import java.util.UUID;

public interface StockItemJpaRepository
        extends JpaRepository<StockItem, UUID>, JpaSpecificationExecutor<StockItem> {
    Optional<StockItem> findBySku(String sku);
}
