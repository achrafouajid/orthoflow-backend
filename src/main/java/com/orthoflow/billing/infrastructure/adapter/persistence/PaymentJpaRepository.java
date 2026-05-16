package com.orthoflow.billing.infrastructure.adapter.persistence;

import com.orthoflow.billing.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByInvoiceId(UUID invoiceId);
}
