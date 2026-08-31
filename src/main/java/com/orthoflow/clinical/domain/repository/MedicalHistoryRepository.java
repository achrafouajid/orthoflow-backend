package com.orthoflow.clinical.domain.repository;

import com.orthoflow.clinical.domain.model.MedicalHistoryEntry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicalHistoryRepository {
    MedicalHistoryEntry save(MedicalHistoryEntry entry);
    Optional<MedicalHistoryEntry> findById(UUID id);
    List<MedicalHistoryEntry> findByPatient(UUID patientId);
    List<MedicalHistoryEntry> findBySession(UUID sessionId);
}
