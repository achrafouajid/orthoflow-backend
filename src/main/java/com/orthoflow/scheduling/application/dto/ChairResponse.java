package com.orthoflow.scheduling.application.dto;

import com.orthoflow.scheduling.domain.model.Chair;

import java.util.UUID;

public record ChairResponse(UUID id, String name) {
    public static ChairResponse from(Chair chair) {
        return new ChairResponse(chair.getId(), chair.getName());
    }
}
