package com.orthoflow.common.security;

import java.util.UUID;

/**
 * The authenticated principal attached to the SecurityContext by JwtAuthFilter.
 * Every controller/service that needs "who did this" (audit actors, ownership
 * checks) reads it from the SecurityContext rather than trusting a client-supplied id.
 */
public record AuthenticatedUser(UUID id, String email, String role) {
}
