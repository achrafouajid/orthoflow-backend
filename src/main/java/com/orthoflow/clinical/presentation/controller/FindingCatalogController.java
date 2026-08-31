package com.orthoflow.clinical.presentation.controller;

import com.orthoflow.clinical.domain.model.FindingCatalog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Publishes the canonical finding vocabulary so the browser's voice lexicon
 * can assert at startup that it maps speech onto codes this server actually
 * accepts. Without this, a lexicon/catalog drift shows up as a doctor
 * dictating a finding that silently fails validation mid-examination.
 */
@RestController
@RequestMapping("/clinical/finding-catalog")
public class FindingCatalogController {

    @GetMapping
    public Map<String, Object> catalog() {
        List<Map<String, Object>> entries = FindingCatalog.all().values().stream()
                .map(def -> Map.<String, Object>of(
                        "code", def.code(),
                        "kind", def.kind().name(),
                        "impliedStatus", def.impliedStatus() == null ? "" : def.impliedStatus(),
                        "statusPriority", def.statusPriority()))
                .sorted((a, b) -> ((String) a.get("code")).compareTo((String) b.get("code")))
                .toList();
        return Map.of("codes", FindingCatalog.codes().stream().sorted().toList(), "definitions", entries);
    }
}
