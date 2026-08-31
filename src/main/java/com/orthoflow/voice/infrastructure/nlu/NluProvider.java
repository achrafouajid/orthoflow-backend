package com.orthoflow.voice.infrastructure.nlu;

import com.orthoflow.voice.application.dto.InterpretRequest;

/**
 * Turns an utterance the browser's grammar could not parse into one of the
 * intents the caller declared it can currently accept.
 *
 * <p>Implementations map to an intent <em>id</em> and arguments — never to
 * anything executable. The interpretation still passes through the same
 * validation, risk tier, confirmation and audit path as a grammar match, so
 * the worst a wrong or adversarial model response can do is propose a command
 * the doctor is then shown and asked to confirm.
 */
public interface NluProvider {

    /** Stable identifier used in logs and in the audit trail. */
    String name();

    /** True when this provider is configured well enough to be called. */
    boolean isAvailable();

    NluInterpretation interpret(InterpretRequest request);
}
