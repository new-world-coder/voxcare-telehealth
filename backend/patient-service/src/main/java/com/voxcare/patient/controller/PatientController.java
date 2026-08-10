package com.voxcare.patient.controller;

import com.voxcare.patient.dto.PatientRequest;
import com.voxcare.patient.dto.PatientResponse;
import com.voxcare.patient.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody PatientRequest request) {
        return new ResponseEntity<>(patientService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public List<PatientResponse> list(@RequestParam(required = false) String q) {
        return patientService.search(q);
    }

    @GetMapping("/{id}")
    public PatientResponse getById(@PathVariable Long id) {
        return patientService.getById(id);
    }

    @GetMapping("/user/{userId}")
    public PatientResponse getByUserId(@PathVariable Long userId) {
        return patientService.getByUserId(userId);
    }

    /**
     * Lookup used by voice/dialer flows. Accepts raw or formatted numbers.
     */
    @GetMapping("/by-phone/{phone}")
    public PatientResponse getByPhone(@PathVariable String phone) {
        return patientService.getByPhone(phone);
    }

    @PutMapping("/{id}")
    public PatientResponse update(@PathVariable Long id, @Valid @RequestBody PatientRequest request) {
        return patientService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        patientService.delete(id);
    }
}
