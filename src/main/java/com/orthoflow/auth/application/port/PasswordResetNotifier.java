package com.orthoflow.auth.application.port;

/**
 * Delivery channel for password-reset links, kept separate from
 * {@code AuthService} so the service depends on this abstraction (DIP)
 * rather than on a specific transport. Swap the bound implementation for an
 * SMTP/SES-backed one once outbound email is wired up; nothing in
 * AuthService needs to change.
 */
public interface PasswordResetNotifier {
    void sendResetLink(String toEmail, String resetUrl);
}
