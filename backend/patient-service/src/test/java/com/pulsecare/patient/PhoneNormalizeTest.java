package com.pulsecare.patient;

import com.pulsecare.patient.model.Patient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneNormalizeTest {

    @Test
    void stripsNonDigits() {
        assertEquals("15550123", Patient.normalizePhone("+1-555-0123"));
        assertEquals("15551234567", Patient.normalizePhone("+1 (555) 123-4567"));
        assertNull(Patient.normalizePhone(null));
        assertNull(Patient.normalizePhone("abc"));
    }
}
