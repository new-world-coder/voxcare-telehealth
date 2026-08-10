package com.voxcare.voice.provider;

public record InitiateCallParams(
        String to,
        String fromNumberId,
        String outboundInstruction,
        Long patientId,
        String purpose
) {
}
