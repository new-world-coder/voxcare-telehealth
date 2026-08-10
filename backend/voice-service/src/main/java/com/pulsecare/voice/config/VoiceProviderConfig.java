package com.pulsecare.voice.config;

import com.pulsecare.voice.provider.VoiceProvider;
import com.pulsecare.voice.provider.VoiceProviderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VoiceProviderConfig {

    @Bean
    public VoiceProvider voiceProvider(VoiceProviderFactory factory) {
        return factory.create();
    }
}
