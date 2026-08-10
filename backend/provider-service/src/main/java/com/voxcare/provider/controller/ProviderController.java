package com.voxcare.provider.controller;

import com.voxcare.provider.dto.AvailabilityRequest;
import com.voxcare.provider.dto.AvailabilityResponse;
import com.voxcare.provider.dto.ProviderRequest;
import com.voxcare.provider.dto.ProviderResponse;
import com.voxcare.provider.service.ProviderService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/providers")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping
    public ResponseEntity<ProviderResponse> create(@Valid @RequestBody ProviderRequest request) {
        return new ResponseEntity<>(providerService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public List<ProviderResponse> list(@RequestParam(required = false) String specialty) {
        return providerService.list(specialty);
    }

    @GetMapping("/{id}")
    public ProviderResponse getById(@PathVariable Long id) {
        return providerService.getById(id);
    }

    @PutMapping("/{id}")
    public ProviderResponse update(@PathVariable Long id, @Valid @RequestBody ProviderRequest request) {
        return providerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        providerService.delete(id);
    }

    @PostMapping("/availability")
    public ResponseEntity<AvailabilityResponse> createAvailability(@Valid @RequestBody AvailabilityRequest request) {
        return new ResponseEntity<>(providerService.createAvailability(request), HttpStatus.CREATED);
    }

    @GetMapping("/{providerId}/availability")
    public List<AvailabilityResponse> listAvailability(@PathVariable Long providerId) {
        return providerService.listAvailability(providerId);
    }

    /**
     * Open slots for dialer / booking UI. Optional providerId filters one clinician.
     */
    @GetMapping("/slots/open")
    public List<AvailabilityResponse> openSlots(
            @RequestParam(required = false) Long providerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return providerService.openSlots(providerId, from, to);
    }

    @DeleteMapping("/availability/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvailability(@PathVariable Long id) {
        providerService.deleteAvailability(id);
    }
}
