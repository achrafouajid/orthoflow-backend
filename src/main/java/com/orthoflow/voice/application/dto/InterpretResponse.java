package com.orthoflow.voice.application.dto;

import lombok.Builder;

import java.util.Map;

/**
 * The structured command the utterance was understood to mean — or, just as
 * validly, a question.
 */
@Builder
public record InterpretResponse(
        String intent,
        Map<String, Object> entities,
        double confidence,
        /** Non-null when the utterance was understood but is not specific enough to act on. */
        String clarification,
        /** 'grammar' | 'llm' | 'disabled' — which layer produced this. */
        String resolver,
        String provider,
        /** Set when the provider itself failed, as distinct from not understanding. */
        String error
) {}
