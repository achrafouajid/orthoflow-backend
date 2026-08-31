package com.orthoflow.voice.infrastructure.adapter.persistence;

import com.orthoflow.voice.domain.model.VoiceSession;
import com.orthoflow.voice.domain.repository.VoiceSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VoiceSessionRepositoryAdapter implements VoiceSessionRepository {

    private final VoiceSessionJpaRepository jpaRepository;

    @Override
    public VoiceSession save(VoiceSession session) {
        return jpaRepository.save(session);
    }

    @Override
    public Optional<VoiceSession> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<VoiceSession> findByPatient(UUID patientId) {
        return jpaRepository.findByPatientIdOrderByStartedAtDesc(patientId);
    }
}
