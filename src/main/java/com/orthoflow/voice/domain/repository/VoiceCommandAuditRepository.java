package com.orthoflow.voice.domain.repository;

import com.orthoflow.voice.domain.model.VoiceCommandAudit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoiceCommandAuditRepository {
    VoiceCommandAudit save(VoiceCommandAudit entry);
    Optional<VoiceCommandAudit> findById(UUID id);
    List<VoiceCommandAudit> findByPatient(UUID patientId);
    List<VoiceCommandAudit> findBySession(UUID sessionId);
}
