package com.orthoflow.stock.presentation.controller;

import com.orthoflow.stock.application.service.TreatmentInvoiceService;
import com.orthoflow.stock.domain.model.TreatmentInvoice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stock/treatment-invoices")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class TreatmentInvoiceController {

    private final TreatmentInvoiceService treatmentInvoiceService;

    @GetMapping
    public ResponseEntity<List<TreatmentInvoice>> getAll() {
        return ResponseEntity.ok(treatmentInvoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreatmentInvoice> getById(@PathVariable UUID id) {
        return treatmentInvoiceService.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<TreatmentInvoice>> getByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(treatmentInvoiceService.getInvoicesByPatient(patientId));
    }

    @PostMapping("/draft")
    public ResponseEntity<TreatmentInvoice> createDraft(
            @RequestParam UUID patientId,
            @RequestParam UUID treatmentId,
            @RequestParam(required = false) UUID createdBy) {
        
        return ResponseEntity.ok(treatmentInvoiceService.createDraftFromTreatment(patientId, treatmentId, createdBy));
    }

    @PostMapping
    public ResponseEntity<TreatmentInvoice> save(@RequestBody TreatmentInvoice invoice) {
        return ResponseEntity.ok(treatmentInvoiceService.saveInvoice(invoice));
    }

    @PostMapping("/{id}/finalize")
    public ResponseEntity<TreatmentInvoice> finalizeInvoice(
            @PathVariable UUID id,
            @RequestParam UUID finalizedBy) {
        
        return ResponseEntity.ok(treatmentInvoiceService.finalizeInvoice(id, finalizedBy));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<TreatmentInvoice> cancelInvoice(
            @PathVariable UUID id,
            @RequestParam UUID cancelledBy) {
        
        return ResponseEntity.ok(treatmentInvoiceService.cancelInvoice(id, cancelledBy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        treatmentInvoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
