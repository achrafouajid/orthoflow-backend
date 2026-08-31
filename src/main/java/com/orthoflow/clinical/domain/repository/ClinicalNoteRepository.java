package com.orthoflow.clinical.domain.repository;

import com.orthoflow.clinical.domain.model.ClinicalNote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClinicalNoteRepository {
    ClinicalNote save(ClinicalNote note);
    Optional<ClinicalNote> findById(UUID id);
    List<ClinicalNote> findByPatient(UUID patientId);
    List<ClinicalNote> findBySession(UUID sessionId);
}
