package com.orthoflow.settings.infrastructure.adapter.persistence;

import com.orthoflow.settings.domain.model.PracticeSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PracticeSettingsJpaRepository extends JpaRepository<PracticeSettings, UUID> {
}
