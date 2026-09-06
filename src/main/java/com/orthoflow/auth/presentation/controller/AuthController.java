package com.orthoflow.auth.presentation.controller;

import com.orthoflow.auth.application.dto.ForgotPasswordRequest;
import com.orthoflow.auth.application.dto.LoginRequest;
import com.orthoflow.auth.application.dto.LoginResponse;
import com.orthoflow.auth.application.dto.RegisterRequest;
import com.orthoflow.auth.application.dto.ResetPasswordRequest;
import com.orthoflow.auth.application.dto.UserResponse;
import com.orthoflow.auth.application.service.AuthService;
import com.orthoflow.common.exception.UnauthorizedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Off during pre-launch development so the team can create/reset
    // multiple test accounts without an admin session. Flip
    // RESTRICT_REGISTRATION_TO_BOOTSTRAP=true (no code change needed) once
    // a real practice is onboarded and self-service account creation should
    // require an authenticated ADMIN again.
    @Value("${orthoflow.auth.restrict-registration-to-bootstrap:false}")
    private boolean restrictRegistrationToBootstrap;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Open to bootstrap the very first (ADMIN) account on an empty database.
     * When orthoflow.auth.restrict-registration-to-bootstrap is enabled,
     * creating further accounts once any user exists requires an
     * authenticated ADMIN.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        if (restrictRegistrationToBootstrap) {
            boolean bootstrap = authService.noUsersExist();
            if (!bootstrap && !hasAdminRole()) {
                throw new UnauthorizedException("Only an administrator can create new accounts");
            }
        }
        return authService.register(request);
    }

    /**
     * Always 202/no body, whether or not the email is registered — see
     * AuthService#requestPasswordReset for why the response can't vary.
     */
    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.requestPasswordReset(request.getEmail());
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
    }

    private boolean hasAdminRole() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
