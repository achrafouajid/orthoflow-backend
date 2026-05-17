package com.orthoflow.stock.domain.repository;

import com.orthoflow.stock.domain.model.Supplier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository {
    Supplier save(Supplier supplier);
    Optional<Supplier> findById(UUID id);
    List<Supplier> findAll();
    void deleteById(UUID id);
}
