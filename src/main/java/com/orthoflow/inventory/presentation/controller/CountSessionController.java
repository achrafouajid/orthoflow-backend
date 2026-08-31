package com.orthoflow.inventory.presentation.controller;

import com.orthoflow.common.security.CurrentUserProvider;
import com.orthoflow.inventory.application.dto.CountSessionCreateRequest;
import com.orthoflow.inventory.application.dto.CountSessionLineUpdateRequest;
import com.orthoflow.inventory.application.dto.CountSessionResponse;
import com.orthoflow.inventory.application.service.CountSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stock/count-sessions")
@RequiredArgsConstructor
public class CountSessionController {

    private final CountSessionService countSessionService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<List<CountSessionResponse>> getAll() {
        return ResponseEntity.ok(countSessionService.getAllCountSessions().stream()
                .map(CountSessionResponse::from).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountSessionResponse> getById(@PathVariable UUID id) {
        return countSessionService.getCountSessionById(id)
                .map(cs -> ResponseEntity.ok(CountSessionResponse.from(cs)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CountSessionResponse> create(@RequestBody(required = false) CountSessionCreateRequest request) {
        String notes = request != null ? request.getNotes() : null;
        return ResponseEntity.ok(CountSessionResponse.from(
                countSessionService.createCountSession(notes, currentUserProvider.requireUserId())));
    }

    @PutMapping("/{id}/lines")
    public ResponseEntity<CountSessionResponse> updateLines(
            @PathVariable UUID id,
            @Valid @RequestBody List<CountSessionLineUpdateRequest> lines) {
        return ResponseEntity.ok(CountSessionResponse.from(countSessionService.updateCountSessionLines(id, lines)));
    }

    /**
     * `validatedBy` is accepted for backward compatibility with the existing
     * frontend call but deliberately ignored — the real actor for the audit
     * trail and the posted stock movements always comes from the verified
     * JWT via CurrentUserProvider, never from client-supplied input (audit II.3).
     */
    @PostMapping("/{id}/validate")
    public ResponseEntity<CountSessionResponse> validate(
            @PathVariable UUID id,
            @RequestParam(required = false) String validatedBy) {
        return ResponseEntity.ok(CountSessionResponse.from(
                countSessionService.validateCountSession(id, currentUserProvider.requireUserId())));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<CountSessionResponse> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(CountSessionResponse.from(countSessionService.cancelCountSession(id)));
    }
}
