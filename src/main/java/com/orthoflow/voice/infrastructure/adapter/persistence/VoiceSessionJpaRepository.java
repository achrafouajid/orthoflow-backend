package com.orthoflow.voice.infrastructure.adapter.persistence;

import com.orthoflow.voice.domain.model.VoiceSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VoiceSessionJpaRepository extends JpaRepository<VoiceSession, UUID> {
    List<VoiceSession> findByPatientIdOrderByStartedAtDesc(UUID patientId);
}
