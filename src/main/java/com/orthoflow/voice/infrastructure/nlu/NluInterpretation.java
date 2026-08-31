package com.orthoflow.voice.infrastructure.nlu;

import lombok.Builder;

import java.util.Map;

/**
 * What the fallback made of an utterance.
 *
 * <p>A null {@code intent} together with a {@code clarification} is a
 * first-class, expected result, not a failure: "upper molar needs treatment"
 * names no single tooth, and the only safe response is a question. Models are
 * instructed to prefer this over choosing between candidates.
 */
@Builder
public record NluInterpretation(
        String intent,
        Map<String, Object> entities,
        double confidence,
        String clarification,
        String rawResponse,
        String providerName,
        String error
) {
    public static NluInterpretation unavailable(String providerName, String error) {
        return NluInterpretation.builder()
                .providerName(providerName)
                .confidence(0)
                .error(error)
                .build();
    }

    public boolean hasIntent() {
        return intent != null && !intent.isBlank();
    }
}
