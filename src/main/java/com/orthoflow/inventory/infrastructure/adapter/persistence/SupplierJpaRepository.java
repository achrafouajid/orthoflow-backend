package com.orthoflow.inventory.infrastructure.adapter.persistence;

import com.orthoflow.inventory.domain.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SupplierJpaRepository extends JpaRepository<Supplier, UUID> {
}
