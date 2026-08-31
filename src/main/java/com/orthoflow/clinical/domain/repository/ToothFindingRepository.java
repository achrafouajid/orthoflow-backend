package com.orthoflow.clinical.domain.repository;

import com.orthoflow.clinical.domain.model.ToothFinding;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ToothFindingRepository {
    ToothFinding save(ToothFinding finding);
    Optional<ToothFinding> findById(UUID id);
    List<ToothFinding> findActiveByChart(UUID chartId);
    List<ToothFinding> findActiveByChartAndFdi(UUID chartId, String fdi);
    Optional<ToothFinding> findActiveByChartFdiAndCode(UUID chartId, String fdi, String findingCode);
    List<ToothFinding> findBySession(UUID sessionId);
}
