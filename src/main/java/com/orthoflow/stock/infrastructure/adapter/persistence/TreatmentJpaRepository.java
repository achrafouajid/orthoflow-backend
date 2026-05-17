package com.orthoflow.stock.infrastructure.adapter.persistence;

import com.orthoflow.stock.domain.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TreatmentJpaRepository extends JpaRepository<Treatment, UUID> {
    Optional<Treatment> findByCode(String code);
}
