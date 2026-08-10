package com.voxcare.voice.config;

import com.voxcare.voice.provider.VoiceProvider;
import com.voxcare.voice.provider.VoiceProviderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VoiceProviderConfig {

    @Bean
    public VoiceProvider voiceProvider(VoiceProviderFactory factory) {
        return factory.create();
    }
}
