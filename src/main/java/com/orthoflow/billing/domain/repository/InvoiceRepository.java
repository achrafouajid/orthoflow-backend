package com.orthoflow.billing.domain.repository;

import com.orthoflow.billing.domain.model.Invoice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {
    Invoice save(Invoice invoice);
    Optional<Invoice> findById(UUID id);
    List<Invoice> findAll();
    List<Invoice> findByPatientId(UUID patientId);
    void deleteById(UUID id);
}
