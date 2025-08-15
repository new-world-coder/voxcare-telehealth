package com.pulsecare.patient.service;

import com.pulsecare.patient.model.Patient;
import com.pulsecare.patient.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {
    
    private final PatientRepository patientRepository;
    
    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }
    
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
    
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }
    
    public Optional<Patient> getPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId);
    }
    
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }
    
    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        
        patient.setFirstName(patientDetails.getFirstName());
        patient.setLastName(patientDetails.getLastName());
        patient.setDateOfBirth(patientDetails.getDateOfBirth());
        patient.setPhone(patientDetails.getPhone());
        
        return patientRepository.save(patient);
    }
    
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
    
    public List<Patient> searchPatientsByName(String firstName, String lastName) {
        if (firstName != null && lastName != null) {
            return patientRepository.findByFirstNameAndLastName(firstName, lastName)
                    .map(List::of)
                    .orElse(List.of());
        } else if (firstName != null) {
            return patientRepository.findAll().stream()
                    .filter(p -> p.getFirstName().toLowerCase().contains(firstName.toLowerCase()))
                    .toList();
        } else if (lastName != null) {
            return patientRepository.findAll().stream()
                    .filter(p -> p.getLastName().toLowerCase().contains(lastName.toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
