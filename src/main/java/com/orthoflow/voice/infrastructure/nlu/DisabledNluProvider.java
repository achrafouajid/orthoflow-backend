package com.orthoflow.voice.infrastructure.nlu;

import com.orthoflow.voice.application.dto.InterpretRequest;
import org.springframework.stereotype.Component;

/**
 * The default. Every utterance the on-device grammar could not parse comes
 * back as "not understood", which the assistant surfaces as a question rather
 * than a guess — the same behaviour as a low-confidence match.
 */
@Component
public class DisabledNluProvider implements NluProvider {

    @Override
    public String name() {
        return "disabled";
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public NluInterpretation interpret(InterpretRequest request) {
        return NluInterpretation.builder()
                .providerName(name())
                .confidence(0)
                .clarification("I didn't catch that. Try naming the tooth and the finding, "
                        + "for example \"upper right first molar, recurrent caries\".")
                .build();
    }
}
