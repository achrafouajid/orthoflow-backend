package com.orthoflow.settings.presentation.controller;

import com.orthoflow.common.security.CurrentUserProvider;
import com.orthoflow.settings.application.dto.PracticeSettingsRequest;
import com.orthoflow.settings.application.dto.PracticeSettingsResponse;
import com.orthoflow.settings.application.service.PracticeSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings/practice")
@RequiredArgsConstructor
public class PracticeSettingsController {

    private final PracticeSettingsService practiceSettingsService;
    private final CurrentUserProvider currentUserProvider;

    // Readable by any authenticated staff member — the schedule view needs
    // working hours to render at all, not just the admin who set them.
    @GetMapping
    public PracticeSettingsResponse get() {
        return practiceSettingsService.get();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PracticeSettingsResponse update(@Valid @RequestBody PracticeSettingsRequest request) {
        return practiceSettingsService.update(request, currentUserProvider.requireUserId());
    }
}
