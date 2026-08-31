package com.orthoflow.voice.application.dto;

import java.util.List;
import java.util.Map;

/**
 * One intent the caller is willing to accept right now.
 *
 * <p>The browser sends its currently in-scope command set with every request
 * rather than the server holding a copy. Two things follow from that: context
 * scoping (audit XII.4 §7) is enforced by construction — a model cannot
 * propose "assign treatment" from the stock screen because that intent was
 * never offered — and a new module can add voice commands without touching
 * the server at all.
 */
public record IntentDescriptor(
        String id,
        String description,
        /** Argument name → what it means and what shape it takes. */
        Map<String, String> args,
        List<String> examples
) {}
