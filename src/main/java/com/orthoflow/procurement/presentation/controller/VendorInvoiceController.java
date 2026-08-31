package com.orthoflow.procurement.presentation.controller;

import com.orthoflow.common.security.CurrentUserProvider;
import com.orthoflow.procurement.application.dto.VendorInvoiceCreateRequest;
import com.orthoflow.procurement.application.dto.VendorInvoiceResponse;
import com.orthoflow.procurement.application.service.VendorInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stock/vendor-invoices")
@RequiredArgsConstructor
public class VendorInvoiceController {

    private final VendorInvoiceService vendorInvoiceService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<List<VendorInvoiceResponse>> getAll() {
        return ResponseEntity.ok(vendorInvoiceService.getAllVendorInvoices().stream()
                .map(VendorInvoiceResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorInvoiceResponse> getById(@PathVariable UUID id) {
        return vendorInvoiceService.getVendorInvoiceById(id)
                .map(vi -> ResponseEntity.ok(VendorInvoiceResponse.from(vi)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VendorInvoiceResponse> create(@Valid @RequestBody VendorInvoiceCreateRequest request) {
        return ResponseEntity.ok(VendorInvoiceResponse.from(
                vendorInvoiceService.createVendorInvoice(request, currentUserProvider.requireUserId())));
    }

    /**
     * `validatedBy` is accepted for backward compatibility with the existing
     * frontend call but deliberately ignored — the real actor for the audit
     * trail always comes from the verified JWT via CurrentUserProvider, never
     * from client-supplied input (the same fabricated-actor bug pattern
     * fixed in InvoiceController/PaymentController — audit II.3).
     */
    @PostMapping("/{id}/validate")
    public ResponseEntity<VendorInvoiceResponse> validate(
            @PathVariable UUID id,
            @RequestParam(required = false) String validatedBy) {
        return ResponseEntity.ok(VendorInvoiceResponse.from(
                vendorInvoiceService.validateVendorInvoice(id, currentUserProvider.requireUserId())));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<VendorInvoiceResponse> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(VendorInvoiceResponse.from(vendorInvoiceService.cancelVendorInvoice(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        vendorInvoiceService.deleteVendorInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
