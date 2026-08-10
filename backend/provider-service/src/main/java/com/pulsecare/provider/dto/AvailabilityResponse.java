package com.pulsecare.provider.dto;

import com.pulsecare.provider.model.AvailabilitySlot;

import java.time.LocalDateTime;

public class AvailabilityResponse {

    private Long id;
    private Long providerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public static AvailabilityResponse from(AvailabilitySlot slot) {
        AvailabilityResponse r = new AvailabilityResponse();
        r.id = slot.getId();
        r.providerId = slot.getProviderId();
        r.startTime = slot.getStartTime();
        r.endTime = slot.getEndTime();
        r.status = slot.getStatus();
        return r;
    }

    public Long getId() {
        return id;
    }

    public Long getProviderId() {
        return providerId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }
}
