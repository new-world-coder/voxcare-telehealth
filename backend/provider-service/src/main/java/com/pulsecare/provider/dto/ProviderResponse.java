package com.pulsecare.provider.dto;

import com.pulsecare.provider.model.Provider;

import java.time.LocalDateTime;

public class ProviderResponse {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String specialty;
    private String timezone;
    private String displayName;
    private LocalDateTime createdAt;

    public static ProviderResponse from(Provider p) {
        ProviderResponse r = new ProviderResponse();
        r.id = p.getId();
        r.userId = p.getUserId();
        r.firstName = p.getFirstName();
        r.lastName = p.getLastName();
        r.specialty = p.getSpecialty();
        r.timezone = p.getTimezone();
        r.createdAt = p.getCreatedAt();
        if (p.getFirstName() != null || p.getLastName() != null) {
            r.displayName = (("Dr. " + (p.getFirstName() == null ? "" : p.getFirstName()) + " "
                    + (p.getLastName() == null ? "" : p.getLastName())).trim());
        } else {
            r.displayName = "Provider #" + p.getId() + " (" + p.getSpecialty() + ")";
        }
        return r;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
