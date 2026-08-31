package com.orthoflow.procurement.presentation.controller;

import com.orthoflow.common.security.CurrentUserProvider;
import com.orthoflow.procurement.application.dto.DeliveryNoteRequest;
import com.orthoflow.procurement.application.dto.DeliveryNoteResponse;
import com.orthoflow.procurement.application.service.DeliveryNoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stock/delivery-notes")
@RequiredArgsConstructor
public class DeliveryNoteController {

    private final DeliveryNoteService deliveryNoteService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<List<DeliveryNoteResponse>> getAll() {
        return ResponseEntity.ok(deliveryNoteService.getAllDeliveryNotes().stream()
                .map(DeliveryNoteResponse::from).collect(Collectors.toList()));
    }

    /** Open GRNIs: received but not yet vendor-invoiced (BR03 GRNI selection). */
    @GetMapping("/open-grni")
    public ResponseEntity<List<DeliveryNoteResponse>> getOpenGrni() {
        return ResponseEntity.ok(deliveryNoteService.getOpenGrni().stream()
                .map(DeliveryNoteResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryNoteResponse> getById(@PathVariable UUID id) {
        return deliveryNoteService.getDeliveryNoteById(id)
                .map(dn -> ResponseEntity.ok(DeliveryNoteResponse.from(dn)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<DeliveryNoteResponse> getByNumber(@PathVariable String number) {
        return deliveryNoteService.getDeliveryNoteByNumber(number)
                .map(dn -> ResponseEntity.ok(DeliveryNoteResponse.from(dn)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DeliveryNoteResponse> create(@Valid @RequestBody DeliveryNoteRequest request) {
        return ResponseEntity.ok(DeliveryNoteResponse.from(deliveryNoteService.createDeliveryNote(request)));
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<DeliveryNoteResponse> receive(
            @PathVariable UUID id,
            @RequestParam(required = false) String notes) {

        return ResponseEntity.ok(DeliveryNoteResponse.from(
                deliveryNoteService.receiveDeliveryNote(id, currentUserProvider.requireUserId(), notes)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        deliveryNoteService.deleteDeliveryNote(id);
        return ResponseEntity.noContent().build();
    }
}
