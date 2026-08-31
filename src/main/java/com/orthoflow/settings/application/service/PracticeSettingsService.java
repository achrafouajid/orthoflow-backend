package com.orthoflow.settings.application.service;

import com.orthoflow.settings.application.dto.PracticeSettingsRequest;
import com.orthoflow.settings.application.dto.PracticeSettingsResponse;
import com.orthoflow.settings.domain.model.PracticeSettings;
import com.orthoflow.settings.infrastructure.adapter.persistence.PracticeSettingsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PracticeSettingsService {

    private final PracticeSettingsJpaRepository repository;

    @Transactional(readOnly = true)
    public PracticeSettingsResponse get() {
        PracticeSettings settings = load();
        return new PracticeSettingsResponse(settings.getWorkingHoursStart(), settings.getWorkingHoursEnd());
    }

    @Transactional
    public PracticeSettingsResponse update(PracticeSettingsRequest request, UUID actorId) {
        PracticeSettings settings = load();
        settings.setWorkingHoursStart(request.getWorkingHoursStart());
        settings.setWorkingHoursEnd(request.getWorkingHoursEnd());
        settings.setUpdatedBy(actorId);
        PracticeSettings saved = repository.save(settings);
        return new PracticeSettingsResponse(saved.getWorkingHoursStart(), saved.getWorkingHoursEnd());
    }

    // The singleton row is seeded by V20__practice_settings.sql; this is a
    // defensive fallback, not the primary path.
    private PracticeSettings load() {
        return repository.findById(PracticeSettings.SINGLETON_ID)
                .orElseGet(() -> repository.save(PracticeSettings.builder()
                        .id(PracticeSettings.SINGLETON_ID)
                        .workingHoursStart((short) 8)
                        .workingHoursEnd((short) 19)
                        .build()));
    }
}
