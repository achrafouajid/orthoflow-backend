package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.Supplier;
import com.orthoflow.stock.domain.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SupplierRepositoryAdapter implements SupplierRepository {

    private final SupplierJpaRepository jpaRepository;

    @Override
    public Supplier save(Supplier supplier) {
        return jpaRepository.save(supplier);
    }

    @Override
    public Optional<Supplier> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Supplier> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
