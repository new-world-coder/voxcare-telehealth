package com.pulsecare.patient.service;

import com.pulsecare.patient.dto.PatientRequest;
import com.pulsecare.patient.dto.PatientResponse;
import com.pulsecare.patient.model.Patient;
import com.pulsecare.patient.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class PatientService {

    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public PatientResponse create(PatientRequest request) {
        Patient patient = new Patient();
        apply(patient, request);
        return PatientResponse.from(repository.save(patient));
    }

    public PatientResponse update(Long id, PatientRequest request) {
        Patient patient = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        apply(patient, request);
        return PatientResponse.from(repository.save(patient));
    }

    @Transactional(readOnly = true)
    public PatientResponse getById(Long id) {
        return repository.findById(id)
                .map(PatientResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    @Transactional(readOnly = true)
    public PatientResponse getByUserId(Long userId) {
        return repository.findByUserId(userId)
                .map(PatientResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    @Transactional(readOnly = true)
    public PatientResponse getByPhone(String phone) {
        String normalized = Patient.normalizePhone(phone);
        if (normalized == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone number");
        }
        return repository.findByPhoneNormalized(normalized)
                .map(PatientResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found for phone"));
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> listAll() {
        return repository.findAll().stream().map(PatientResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> search(String q) {
        if (q == null || q.isBlank()) {
            return listAll();
        }
        return repository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(q, q)
                .stream()
                .map(PatientResponse::from)
                .toList();
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found");
        }
        repository.deleteById(id);
    }

    private void apply(Patient patient, PatientRequest request) {
        patient.setUserId(request.getUserId());
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setDob(request.getDob());
        patient.setPhone(request.getPhone());
    }
}
