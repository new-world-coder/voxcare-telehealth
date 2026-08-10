package com.voxcare.voice.service;

public record ReminderAppointment(
        Long id,
        Long patientId,
        Long providerId,
        String appointmentDate,
        Integer durationMinutes
) {
}
