package com.orthoflow.stock.presentation.controller;

import com.orthoflow.stock.application.service.DeliveryNoteService;
import com.orthoflow.stock.domain.model.DeliveryNote;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stock/delivery-notes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class DeliveryNoteController {

    private final DeliveryNoteService deliveryNoteService;

    @GetMapping
    public ResponseEntity<List<DeliveryNote>> getAll() {
        return ResponseEntity.ok(deliveryNoteService.getAllDeliveryNotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryNote> getById(@PathVariable UUID id) {
        return deliveryNoteService.getDeliveryNoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<DeliveryNote> getByNumber(@PathVariable String number) {
        return deliveryNoteService.getDeliveryNoteByNumber(number)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DeliveryNote> create(@RequestBody DeliveryNote note) {
        if (note.getId() == null) {
            note.setId(UUID.randomUUID());
        }
        return ResponseEntity.ok(deliveryNoteService.createDeliveryNote(note));
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<DeliveryNote> receive(
            @PathVariable UUID id,
            @RequestParam UUID receivedBy,
            @RequestParam(required = false) String notes) {
        
        return ResponseEntity.ok(deliveryNoteService.receiveDeliveryNote(id, receivedBy, notes));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        deliveryNoteService.deleteDeliveryNote(id);
        return ResponseEntity.noContent().build();
    }
}
