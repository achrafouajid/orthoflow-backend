package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.StockItem;
import com.orthoflow.stock.domain.repository.StockItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StockItemRepositoryAdapter implements StockItemRepository {

    private final StockItemJpaRepository jpaRepository;

    @Override
    public StockItem save(StockItem stockItem) {
        return jpaRepository.save(stockItem);
    }

    @Override
    public Optional<StockItem> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<StockItem> findBySku(String sku) {
        return jpaRepository.findBySku(sku);
    }

    @Override
    public List<StockItem> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
