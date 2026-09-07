package com.orthoflow.voice.infrastructure.nlu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orthoflow.voice.application.dto.IntentDescriptor;
import com.orthoflow.voice.application.dto.InterpretRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A model's JSON types are not the application's types, and the gap is not
 * caught by anything downstream: the write path calls {@code toString()} and
 * succeeds, so a wrongly-typed identifier produces a perfectly correct
 * clinical record and a broken review page.
 */
class NluResponseParserTest {

    private NluResponseParser parser;

    @BeforeEach
    void setUp() {
        parser = new NluResponseParser(new ObjectMapper());
    }

    private InterpretRequest request() {
        InterpretRequest request = new InterpretRequest();
        request.setAvailableIntents(List.of(new IntentDescriptor(
                "chart.addToothFindings", "Record findings", Map.of(), List.of())));
        request.setFindingCodes(List.of("recurrent_caries", "caries"));
        return request;
    }

    @Test
    void coercesANumericToothCodeToTheStringTheRestOfTheSystemUses() {
        // Gemini returns `"fdi": 16` about as often as `"fdi": "16"`.
        String raw = """
                {"intent":"chart.addToothFindings",
                 "entities":{"fdi":16,"findingCodes":["recurrent_caries"]},
                 "confidence":0.95,"clarification":null}
                """;

        NluInterpretation result = parser.parse(raw, request(), "test");

        assertThat(result.hasIntent()).isTrue();
        assertThat(result.entities().get("fdi"))
                .isInstanceOf(String.class)
                .isEqualTo("16");
    }

    @Test
    void leavesAToothCodeThatWasAlreadyAStringAlone() {
        String raw = """
                {"intent":"chart.addToothFindings",
                 "entities":{"fdi":"16"},"confidence":0.9}
                """;

        NluInterpretation result = parser.parse(raw, request(), "test");

        assertThat(result.entities().get("fdi")).isEqualTo("16");
    }

    @Test
    void refusesAnIntentThatWasNeverOffered() {
        // Context scoping is only real if it is enforced here rather than
        // merely described in the prompt.
        String raw = """
                {"intent":"billing.deleteInvoice","entities":{},"confidence":0.99}
                """;

        NluInterpretation result = parser.parse(raw, request(), "test");

        assertThat(result.hasIntent()).isFalse();
        assertThat(result.clarification()).isNotNull();
    }
}
