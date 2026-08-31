package com.orthoflow.procurement.infrastructure.adapter.persistence;

import com.orthoflow.procurement.domain.model.PurchaseOrder;
import com.orthoflow.procurement.domain.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PurchaseOrderRepositoryAdapter implements PurchaseOrderRepository {

    private final PurchaseOrderJpaRepository jpaRepository;

    @Override
    public PurchaseOrder save(PurchaseOrder purchaseOrder) {
        return jpaRepository.save(purchaseOrder);
    }

    @Override
    public Optional<PurchaseOrder> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<PurchaseOrder> findByPoNumber(String poNumber) {
        return jpaRepository.findByPoNumber(poNumber);
    }

    @Override
    public List<PurchaseOrder> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
