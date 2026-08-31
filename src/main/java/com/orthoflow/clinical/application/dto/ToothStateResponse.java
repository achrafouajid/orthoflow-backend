package com.orthoflow.clinical.application.dto;

import lombok.Builder;

@Builder
public record ToothStateResponse(String fdi, String status, String notes) {
}
