package com.orthoflow.clinical.infrastructure.adapter.persistence;

import com.orthoflow.clinical.domain.model.MedicalHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MedicalHistoryJpaRepository extends JpaRepository<MedicalHistoryEntry, UUID> {
    List<MedicalHistoryEntry> findByPatientIdOrderByRecordedAtDesc(UUID patientId);
    List<MedicalHistoryEntry> findBySessionIdOrderByRecordedAtAsc(UUID sessionId);
}
