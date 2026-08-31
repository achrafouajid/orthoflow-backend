package com.orthoflow.procurement.infrastructure.adapter.persistence;

import com.orthoflow.procurement.domain.model.VendorInvoice;
import com.orthoflow.procurement.domain.repository.VendorInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VendorInvoiceRepositoryAdapter implements VendorInvoiceRepository {

    private final VendorInvoiceJpaRepository jpaRepository;

    @Override
    public VendorInvoice save(VendorInvoice vendorInvoice) {
        return jpaRepository.save(vendorInvoice);
    }

    @Override
    public Optional<VendorInvoice> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<VendorInvoice> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<VendorInvoice> findActiveByDeliveryNoteId(UUID deliveryNoteId) {
        return jpaRepository.findActiveByDeliveryNoteId(deliveryNoteId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
