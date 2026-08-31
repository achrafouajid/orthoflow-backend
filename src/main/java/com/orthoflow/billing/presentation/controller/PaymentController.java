package com.orthoflow.billing.presentation.controller;

import com.orthoflow.common.security.CurrentUserProvider;
import com.orthoflow.billing.application.dto.RecordPaymentRequest;
import com.orthoflow.billing.application.service.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/invoices/{id}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final BillingService billingService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void recordPayment(@PathVariable UUID id, @Valid @RequestBody RecordPaymentRequest request) {
        billingService.recordPayment(id, request, currentUserProvider.requireUserId());
    }
}
