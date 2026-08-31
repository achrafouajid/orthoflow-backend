package com.orthoflow.auth.infrastructure.adapter.notification;

import com.orthoflow.auth.application.port.PasswordResetNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Default notifier for a deployment with no SMTP/SES configured yet: it logs
 * the reset link server-side so an operator can hand it to the user out of
 * band. Not a substitute for real email delivery in production — see
 * {@link PasswordResetNotifier} for how to replace it.
 */
@Component
public class LoggingPasswordResetNotifier implements PasswordResetNotifier {

    private static final Logger log = LoggerFactory.getLogger(LoggingPasswordResetNotifier.class);

    @Override
    public void sendResetLink(String toEmail, String resetUrl) {
        log.info("Password reset requested for {}. Reset link: {}", toEmail, resetUrl);
    }
}
