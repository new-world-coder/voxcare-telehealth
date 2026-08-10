package com.voxcare.voice.provider;

public record CallStatusResult(
        String callId,
        String externalId,
        CommunicationStatus status,
        CallOutcome outcome,
        Integer durationSeconds,
        String transcript
) {
}
