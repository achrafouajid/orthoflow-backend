package com.orthoflow.billing.infrastructure.adapter.persistence;

import com.orthoflow.billing.domain.model.InvoiceAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvoiceAuditLogJpaRepository extends JpaRepository<InvoiceAuditLog, UUID> {
    List<InvoiceAuditLog> findByInvoiceIdOrderByCreatedAtDesc(UUID invoiceId);
}
