package com.orthoflow.scheduling.presentation.controller;

import com.orthoflow.scheduling.application.dto.ChairResponse;
import com.orthoflow.scheduling.infrastructure.adapter.persistence.ChairJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/scheduling/chairs")
@RequiredArgsConstructor
public class ChairController {

    private final ChairJpaRepository chairJpaRepository;

    @GetMapping
    public List<ChairResponse> getActiveChairs() {
        return chairJpaRepository.findByActiveTrue().stream()
                .map(ChairResponse::from)
                .collect(Collectors.toList());
    }
}
