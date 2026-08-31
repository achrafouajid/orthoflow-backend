package com.orthoflow.voice.infrastructure.adapter.persistence;

import com.orthoflow.voice.domain.model.VoiceCommandAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VoiceCommandAuditJpaRepository extends JpaRepository<VoiceCommandAudit, UUID> {
    List<VoiceCommandAudit> findByPatientIdOrderByOccurredAtDesc(UUID patientId);
    List<VoiceCommandAudit> findBySessionIdOrderByOccurredAtAsc(UUID sessionId);
}
