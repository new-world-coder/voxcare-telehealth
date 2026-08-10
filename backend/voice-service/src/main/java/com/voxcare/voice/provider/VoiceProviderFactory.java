package com.voxcare.voice.provider;

import com.voxcare.voice.config.VoiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VoiceProviderFactory {

    private static final Logger log = LoggerFactory.getLogger(VoiceProviderFactory.class);

    private final VoiceProperties properties;

    public VoiceProviderFactory(VoiceProperties properties) {
        this.properties = properties;
    }

    public VoiceProvider create() {
        String type = properties.getProvider() == null ? "mock" : properties.getProvider().trim().toLowerCase();
        return switch (type) {
            case "dial" -> {
                if (properties.getDialApiKey() == null || properties.getDialApiKey().isBlank()) {
                    log.warn("VOICE_PROVIDER=dial but DIAL_API_KEY is empty — falling back to mock");
                    yield new MockVoiceProvider();
                }
                yield new DialVoiceProvider(
                        properties.getDialApiKey(),
                        properties.getDialBaseUrl(),
                        properties.getDialFromNumberId());
            }
            default -> new MockVoiceProvider();
        };
    }
}
