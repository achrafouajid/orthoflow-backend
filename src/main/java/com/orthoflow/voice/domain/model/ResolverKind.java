package com.orthoflow.voice.domain.model;

/**
 * Which layer turned the utterance into a structured command. Worth auditing
 * separately: a pilot needs to know what share of real clinical speech the
 * deterministic grammar handles before deciding how much to depend on a model.
 */
public enum ResolverKind {
    /** The on-device deterministic grammar. No audio or text left the browser. */
    grammar,
    /** The server-side NLU fallback. */
    llm,
    /** Typed or clicked by the doctor, recorded here for a complete trail. */
    manual
}
