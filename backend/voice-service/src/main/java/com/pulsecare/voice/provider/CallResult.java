package com.pulsecare.voice.provider;

public record CallResult(
        String callId,
        String externalId,
        CommunicationStatus status,
        String provider
) {
}
