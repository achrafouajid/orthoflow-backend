package com.orthoflow.billing.infrastructure.adapter.persistence;

import com.orthoflow.billing.domain.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface InvoiceJpaRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByPatientId(UUID patientId);
}
