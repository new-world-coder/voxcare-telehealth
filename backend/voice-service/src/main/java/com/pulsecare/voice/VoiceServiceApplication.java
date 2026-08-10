package com.pulsecare.voice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Voice AI service — GetDial outbound calls for appointment booking and reminders.
 * Provider-specific HTTP stays inside {@code com.pulsecare.voice.provider}.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class VoiceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoiceServiceApplication.class, args);
    }
}
