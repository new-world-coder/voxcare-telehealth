package com.voxcare.voice.provider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockVoiceProviderTest {

    @Test
    void initiateCallReturnsMockId() {
        MockVoiceProvider provider = new MockVoiceProvider();
        CallResult result = provider.initiateCall(new InitiateCallParams(
                "+15551234567",
                null,
                "Book an appointment",
                1L,
                "BOOKING"));

        assertEquals("mock", result.provider());
        assertNotNull(result.callId());
        assertTrue(result.callId().startsWith("mock_"));
        assertEquals(CommunicationStatus.INITIATED, result.status());
    }

    @Test
    void listNumbersReturnsDemoNumber() {
        MockVoiceProvider provider = new MockVoiceProvider();
        assertFalse(provider.listNumbers().isEmpty());
    }
}
