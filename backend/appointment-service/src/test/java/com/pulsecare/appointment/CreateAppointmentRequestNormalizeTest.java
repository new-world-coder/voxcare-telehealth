package com.pulsecare.appointment;

import com.pulsecare.appointment.dto.CreateAppointmentRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CreateAppointmentRequestNormalizeTest {

    @Test
    void derivesDurationFromStartAndEnd() {
        CreateAppointmentRequest req = new CreateAppointmentRequest();
        LocalDateTime start = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        req.setStartTime(start);
        req.setEndTime(start.plusMinutes(45));
        req.normalize();
        assertEquals(45, req.getDurationMinutes());
        assertEquals(start, req.getAppointmentDate());
    }
}
