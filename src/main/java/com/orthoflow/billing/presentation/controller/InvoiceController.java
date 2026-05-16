package com.orthoflow.billing.presentation.controller;

import com.orthoflow.billing.application.dto.CreateInvoiceRequest;
import com.orthoflow.billing.application.dto.InvoiceResponse;
import com.orthoflow.billing.application.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final BillingService billingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponse createInvoice(@RequestBody CreateInvoiceRequest request) {
        UUID dummyCreatorId = UUID.randomUUID();
        return billingService.createInvoice(request, dummyCreatorId);
    }

    @GetMapping
    public java.util.List<InvoiceResponse> getInvoices() {
        return billingService.getInvoices();
    }

    @GetMapping("/{id}")
    public InvoiceResponse getInvoice(@PathVariable UUID id) {
        return billingService.getInvoice(id);
    }

    @GetMapping("/summary")
    public com.orthoflow.billing.application.dto.BillingSummaryResponse getBillingSummary() {
        return billingService.getBillingSummary();
    }
}
