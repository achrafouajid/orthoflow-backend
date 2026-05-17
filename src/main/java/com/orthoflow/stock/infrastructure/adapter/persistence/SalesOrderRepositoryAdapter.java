package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.SalesOrder;
import com.orthoflow.stock.domain.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SalesOrderRepositoryAdapter implements SalesOrderRepository {

    private final SalesOrderJpaRepository jpaRepository;

    @Override
    public SalesOrder save(SalesOrder salesOrder) {
        return jpaRepository.save(salesOrder);
    }

    @Override
    public Optional<SalesOrder> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<SalesOrder> findBySoNumber(String soNumber) {
        return jpaRepository.findBySoNumber(soNumber);
    }

    @Override
    public List<SalesOrder> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
