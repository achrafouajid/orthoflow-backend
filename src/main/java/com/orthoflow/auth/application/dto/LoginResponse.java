package com.orthoflow.auth.application.dto;

import lombok.Builder;

@Builder
public record LoginResponse(String token, UserResponse user) {
}
