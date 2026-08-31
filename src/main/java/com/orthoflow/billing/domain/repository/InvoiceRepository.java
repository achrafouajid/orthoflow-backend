package com.orthoflow.billing.domain.repository;

import com.orthoflow.billing.domain.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {
    Invoice save(Invoice invoice);
    Optional<Invoice> findById(UUID id);
    List<Invoice> findAll();
    Page<Invoice> findAll(Pageable pageable);
    List<Invoice> findByPatientId(UUID patientId);
    Page<Invoice> findByPatientId(UUID patientId, Pageable pageable);
    void deleteById(UUID id);
}
