package com.voxcare.voice.service;

public record PatientInfo(Long id, String firstName, String lastName, String phone) {

    public String displayName() {
        String first = firstName == null ? "" : firstName;
        String last = lastName == null ? "" : lastName;
        String name = (first + " " + last).trim();
        return name.isEmpty() ? "the patient" : name;
    }
}
