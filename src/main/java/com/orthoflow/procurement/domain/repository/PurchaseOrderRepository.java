package com.orthoflow.procurement.domain.repository;

import com.orthoflow.procurement.domain.model.PurchaseOrder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderRepository {
    PurchaseOrder save(PurchaseOrder purchaseOrder);
    Optional<PurchaseOrder> findById(UUID id);
    Optional<PurchaseOrder> findByPoNumber(String poNumber);
    List<PurchaseOrder> findAll();
    void deleteById(UUID id);
}
