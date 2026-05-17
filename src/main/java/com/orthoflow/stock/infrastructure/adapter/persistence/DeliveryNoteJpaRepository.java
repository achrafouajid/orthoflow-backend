package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.DeliveryNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryNoteJpaRepository extends JpaRepository<DeliveryNote, UUID> {
    Optional<DeliveryNote> findByDnNumber(String dnNumber);
}
