package com.orthoflow.voice.infrastructure.nlu;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.ContentBlock;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.OutputConfig;
import com.orthoflow.voice.application.dto.InterpretRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.stream.Collectors;

/**
 * Natural-language fallback backed by the Claude API.
 *
 * <p>Off unless {@code orthoflow.voice.nlu.provider=anthropic} and an API key
 * is set. Turning it on means utterances — which in a consultation room can
 * contain health information about the patient and anyone else present — are
 * sent to a processor, so it needs a DPA and a lawful basis before it is
 * switched on in a real clinic (audit XII.5). For clinics that cannot make
 * that arrangement, {@link OpenAiCompatibleNluProvider} pointed at a model on
 * the clinic's own server keeps the data inside the same trust boundary as
 * the database.
 *
 * <p>Runs at low effort: mapping one short utterance onto a listed intent is a
 * classification, not a reasoning problem, and a doctor mid-examination is
 * waiting for the answer.
 */
@Component
@Slf4j
public class AnthropicNluProvider implements NluProvider {

    private final VoiceNluProperties properties;
    private final NluPromptBuilder promptBuilder;
    private final NluResponseParser responseParser;
    private final AnthropicClient client;

    public AnthropicNluProvider(VoiceNluProperties properties,
                                NluPromptBuilder promptBuilder,
                                NluResponseParser responseParser) {
        this.properties = properties;
        this.promptBuilder = promptBuilder;
        this.responseParser = responseParser;
        this.client = buildClient(properties);
    }

    private static AnthropicClient buildClient(VoiceNluProperties properties) {
        String apiKey = properties.getNlu().getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }
        return AnthropicOkHttpClient.builder()
                .apiKey(apiKey)
                .timeout(Duration.ofMillis(properties.getNlu().getTimeoutMs()))
                .build();
    }

    @Override
    public String name() {
        return "anthropic";
    }

    @Override
    public boolean isAvailable() {
        return client != null;
    }

    @Override
    public NluInterpretation interpret(InterpretRequest request) {
        if (!isAvailable()) {
            return NluInterpretation.unavailable(name(),
                    "Anthropic NLU selected but orthoflow.voice.nlu.api-key is not set");
        }
        try {
            MessageCreateParams params = MessageCreateParams.builder()
                    .model(properties.getNlu().getModel())
                    .maxTokens(properties.getNlu().getMaxOutputTokens())
                    .outputConfig(OutputConfig.builder()
                            .effort(OutputConfig.Effort.LOW)
                            .build())
                    .system(promptBuilder.systemPrompt(request))
                    .addUserMessage(promptBuilder.userPrompt(request))
                    .build();

            Message response = client.messages().create(params);
            String text = response.content().stream()
                    .map(ContentBlock::text)
                    .filter(java.util.Optional::isPresent)
                    .map(block -> block.get().text())
                    .collect(Collectors.joining());

            return responseParser.parse(text, request, name());
        } catch (RuntimeException e) {
            // A failed call must never look like "understood, doing nothing".
            // The assistant surfaces this as an audible failure so the doctor
            // knows the utterance was lost rather than silently recorded.
            log.warn("Anthropic NLU call failed: {}", e.toString());
            return NluInterpretation.unavailable(name(), "NLU request failed: " + e.getClass().getSimpleName());
        }
    }
}
