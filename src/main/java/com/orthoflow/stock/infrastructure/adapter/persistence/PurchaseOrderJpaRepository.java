package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderJpaRepository extends JpaRepository<PurchaseOrder, UUID> {
    Optional<PurchaseOrder> findByPoNumber(String poNumber);
}
