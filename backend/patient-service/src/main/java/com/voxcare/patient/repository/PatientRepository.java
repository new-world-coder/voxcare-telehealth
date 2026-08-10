package com.voxcare.patient.repository;

import com.voxcare.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUserId(Long userId);

    Optional<Patient> findByPhoneNormalized(String phoneNormalized);

    List<Patient> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(String last, String first);
}
