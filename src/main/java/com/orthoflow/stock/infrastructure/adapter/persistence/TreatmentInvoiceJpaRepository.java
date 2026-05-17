package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.TreatmentInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TreatmentInvoiceJpaRepository extends JpaRepository<TreatmentInvoice, UUID> {
    Optional<TreatmentInvoice> findByInvoiceNumber(String invoiceNumber);
    List<TreatmentInvoice> findByPatientId(UUID patientId);
}
