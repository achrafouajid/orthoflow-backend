package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.StockMovement;
import com.orthoflow.stock.domain.model.StockMovementFilter;
import com.orthoflow.stock.domain.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StockMovementRepositoryAdapter implements StockMovementRepository {

    private final StockMovementJpaRepository jpaRepository;

    @Override
    public StockMovement save(StockMovement stockMovement) {
        return jpaRepository.save(stockMovement);
    }

    @Override
    public Optional<StockMovement> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<StockMovement> findAll() {
        return jpaRepository.findAll();
    }

    /**
     * Executes a filtered + sorted query via JPA Specifications.
     * Sort field is whitelisted in {@link StockMovementFilter} — no SQL injection risk.
     */
    @Override
    public List<StockMovement> findAll(StockMovementFilter filter) {
        Sort sort = filter.isAscending()
                ? Sort.by(filter.sortBy()).ascending()
                : Sort.by(filter.sortBy()).descending();

        return jpaRepository.findAll(StockMovementSpecification.from(filter), sort);
    }

    @Override
    public List<StockMovement> findByStockItemId(UUID stockItemId) {
        return jpaRepository.findByStockItemId(stockItemId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

