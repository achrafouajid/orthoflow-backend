package com.orthoflow.voice.domain.repository;

import com.orthoflow.voice.domain.model.VoiceSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoiceSessionRepository {
    VoiceSession save(VoiceSession session);
    Optional<VoiceSession> findById(UUID id);
    List<VoiceSession> findByPatient(UUID patientId);
}
