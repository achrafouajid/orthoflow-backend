package com.orthoflow.clinical.infrastructure.adapter.persistence;

import com.orthoflow.clinical.domain.model.ClinicalNote;
import com.orthoflow.clinical.domain.repository.ClinicalNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ClinicalNoteRepositoryAdapter implements ClinicalNoteRepository {

    private final ClinicalNoteJpaRepository jpaRepository;

    @Override
    public ClinicalNote save(ClinicalNote note) {
        return jpaRepository.save(note);
    }

    @Override
    public Optional<ClinicalNote> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ClinicalNote> findByPatient(UUID patientId) {
        return jpaRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    @Override
    public List<ClinicalNote> findBySession(UUID sessionId) {
        return jpaRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }
}
