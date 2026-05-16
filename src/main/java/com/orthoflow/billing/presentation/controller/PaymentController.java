package com.orthoflow.billing.presentation.controller;

import com.orthoflow.billing.application.dto.RecordPaymentRequest;
import com.orthoflow.billing.application.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/invoices/{id}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final BillingService billingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void recordPayment(@PathVariable UUID id, @RequestBody RecordPaymentRequest request) {
        // In a real app, the recorderId would come from the JWT/SecurityContext
        UUID dummyRecorderId = UUID.randomUUID();
        billingService.recordPayment(id, request, dummyRecorderId);
    }
}
