package com.voxcare.patient.dto;

import com.voxcare.patient.model.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientResponse {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PatientResponse from(Patient p) {
        PatientResponse r = new PatientResponse();
        r.id = p.getId();
        r.userId = p.getUserId();
        r.firstName = p.getFirstName();
        r.lastName = p.getLastName();
        r.dob = p.getDob();
        r.phone = p.getPhone();
        r.createdAt = p.getCreatedAt();
        r.updatedAt = p.getUpdatedAt();
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

    public LocalDate getDob() {
        return dob;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
