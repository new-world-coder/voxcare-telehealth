package com.voxcare.voice.provider;

public record SmsResult(
        String messageId,
        String externalId,
        CommunicationStatus status,
        String provider
) {
}
