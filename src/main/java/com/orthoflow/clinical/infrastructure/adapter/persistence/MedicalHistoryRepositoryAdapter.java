package com.orthoflow.clinical.infrastructure.adapter.persistence;

import com.orthoflow.clinical.domain.model.MedicalHistoryEntry;
import com.orthoflow.clinical.domain.repository.MedicalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MedicalHistoryRepositoryAdapter implements MedicalHistoryRepository {

    private final MedicalHistoryJpaRepository jpaRepository;

    @Override
    public MedicalHistoryEntry save(MedicalHistoryEntry entry) {
        return jpaRepository.save(entry);
    }

    @Override
    public Optional<MedicalHistoryEntry> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<MedicalHistoryEntry> findByPatient(UUID patientId) {
        return jpaRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
    }

    @Override
    public List<MedicalHistoryEntry> findBySession(UUID sessionId) {
        return jpaRepository.findBySessionIdOrderByRecordedAtAsc(sessionId);
    }
}
