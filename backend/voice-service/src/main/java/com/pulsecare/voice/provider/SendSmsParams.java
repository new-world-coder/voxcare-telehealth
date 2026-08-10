package com.pulsecare.voice.provider;

public record SendSmsParams(
        String to,
        String fromNumberId,
        String body,
        Long patientId
) {
}
