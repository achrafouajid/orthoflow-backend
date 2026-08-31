package com.orthoflow.clinical.infrastructure.adapter.persistence;

import com.orthoflow.clinical.domain.model.ToothStateEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ToothStateEventJpaRepository extends JpaRepository<ToothStateEvent, UUID> {
    List<ToothStateEvent> findByPatientIdOrderByOccurredAtDesc(UUID patientId);
}
