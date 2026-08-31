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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Open only to bootstrap the very first (ADMIN) account on an empty
     * database. Once any user exists, creating further accounts requires an
     * authenticated ADMIN — see the @PreAuthorize below.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        boolean bootstrap = authService.noUsersExist();
        if (!bootstrap && !hasAdminRole()) {
            throw new UnauthorizedException("Only an administrator can create new accounts");
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
