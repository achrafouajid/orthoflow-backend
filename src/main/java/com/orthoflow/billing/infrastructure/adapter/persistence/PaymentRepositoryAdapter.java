package com.orthoflow.billing.infrastructure.adapter.persistence;

import com.orthoflow.billing.domain.model.Payment;
import com.orthoflow.billing.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    @Override
    public Payment save(Payment payment) {
        return jpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Payment> findByInvoiceId(UUID invoiceId) {
        return jpaRepository.findByInvoiceId(invoiceId);
    }
}
