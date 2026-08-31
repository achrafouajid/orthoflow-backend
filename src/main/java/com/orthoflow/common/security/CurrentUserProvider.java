package com.orthoflow.common.security;

import com.orthoflow.common.exception.UnauthorizedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Single point of truth for "who is making this request". Every service that
 * previously stamped a random UUID as the actor of a mutation should read it
 * from here instead — it comes from the verified JWT, not client input.
 */
@Component
public class CurrentUserProvider {

    public UUID requireUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;
        if (!(principal instanceof AuthenticatedUser user)) {
            throw new UnauthorizedException("No authenticated user in context");
        }
        return user.id();
    }
}
