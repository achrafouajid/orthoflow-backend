package com.orthoflow.settings.application.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PracticeSettingsRequest {

    @NotNull
    @Min(0)
    @Max(23)
    private Short workingHoursStart;

    @NotNull
    @Min(1)
    @Max(24)
    private Short workingHoursEnd;

    @AssertTrue(message = "workingHoursStart must be before workingHoursEnd")
    public boolean isHoursOrderValid() {
        return workingHoursStart == null || workingHoursEnd == null || workingHoursStart < workingHoursEnd;
    }
}
