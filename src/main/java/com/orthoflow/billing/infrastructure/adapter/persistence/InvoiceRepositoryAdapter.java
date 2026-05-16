package com.orthoflow.billing.infrastructure.adapter.persistence;

import com.orthoflow.billing.domain.model.Invoice;
import com.orthoflow.billing.domain.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InvoiceRepositoryAdapter implements InvoiceRepository {

    private final InvoiceJpaRepository jpaRepository;

    @Override
    public Invoice save(Invoice invoice) {
        return jpaRepository.save(invoice);
    }

    @Override
    public Optional<Invoice> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Invoice> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Invoice> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatientId(patientId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
