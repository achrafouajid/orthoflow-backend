package com.orthoflow.stock.domain.repository;

import com.orthoflow.stock.domain.model.TreatmentInvoice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TreatmentInvoiceRepository {
    TreatmentInvoice save(TreatmentInvoice treatmentInvoice);
    Optional<TreatmentInvoice> findById(UUID id);
    Optional<TreatmentInvoice> findByInvoiceNumber(String invoiceNumber);
    List<TreatmentInvoice> findAll();
    List<TreatmentInvoice> findByPatientId(UUID patientId);
    void deleteById(UUID id);
}
