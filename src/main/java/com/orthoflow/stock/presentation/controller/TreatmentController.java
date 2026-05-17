package com.orthoflow.stock.presentation.controller;

import com.orthoflow.stock.application.service.TreatmentService;
import com.orthoflow.stock.domain.model.Treatment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stock/treatments")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class TreatmentController {

    private final TreatmentService treatmentService;

    @GetMapping
    public ResponseEntity<List<Treatment>> getAllTreatments() {
        return ResponseEntity.ok(treatmentService.getAllTreatments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Treatment> getTreatmentById(@PathVariable UUID id) {
        return treatmentService.getTreatmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Treatment> getTreatmentByCode(@PathVariable String code) {
        return treatmentService.getTreatmentByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Treatment> createTreatment(@RequestBody Treatment treatment) {
        if (treatment.getId() == null) {
            treatment.setId(UUID.randomUUID());
        }
        return ResponseEntity.ok(treatmentService.saveTreatment(treatment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Treatment> updateTreatment(@PathVariable UUID id, @RequestBody Treatment treatment) {
        treatment.setId(id);
        return ResponseEntity.ok(treatmentService.saveTreatment(treatment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTreatment(@PathVariable UUID id) {
        treatmentService.deleteTreatment(id);
        return ResponseEntity.noContent().build();
    }
}
