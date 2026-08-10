package com.voxcare.provider.service;

import com.voxcare.provider.dto.AvailabilityRequest;
import com.voxcare.provider.dto.AvailabilityResponse;
import com.voxcare.provider.dto.ProviderRequest;
import com.voxcare.provider.dto.ProviderResponse;
import com.voxcare.provider.model.AvailabilitySlot;
import com.voxcare.provider.model.Provider;
import com.voxcare.provider.repository.AvailabilityRepository;
import com.voxcare.provider.repository.ProviderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final AvailabilityRepository availabilityRepository;

    public ProviderService(ProviderRepository providerRepository, AvailabilityRepository availabilityRepository) {
        this.providerRepository = providerRepository;
        this.availabilityRepository = availabilityRepository;
    }

    public ProviderResponse create(ProviderRequest request) {
        Provider provider = new Provider();
        apply(provider, request);
        return ProviderResponse.from(providerRepository.save(provider));
    }

    public ProviderResponse update(Long id, ProviderRequest request) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));
        apply(provider, request);
        return ProviderResponse.from(providerRepository.save(provider));
    }

    @Transactional(readOnly = true)
    public ProviderResponse getById(Long id) {
        return providerRepository.findById(id)
                .map(ProviderResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));
    }

    @Transactional(readOnly = true)
    public List<ProviderResponse> list(String specialty) {
        List<Provider> providers = (specialty == null || specialty.isBlank())
                ? providerRepository.findAll()
                : providerRepository.findBySpecialtyIgnoreCase(specialty);
        return providers.stream().map(ProviderResponse::from).toList();
    }

    public void delete(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found");
        }
        providerRepository.deleteById(id);
    }

    public AvailabilityResponse createAvailability(AvailabilityRequest request) {
        if (!providerRepository.existsById(request.getProviderId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found");
        }
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endTime must be after startTime");
        }
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setProviderId(request.getProviderId());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setStatus(request.getStatus() == null ? "AVAILABLE" : request.getStatus());
        return AvailabilityResponse.from(availabilityRepository.save(slot));
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> listAvailability(Long providerId) {
        return availabilityRepository.findByProviderIdOrderByStartTimeAsc(providerId).stream()
                .map(AvailabilityResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> openSlots(Long providerId, LocalDateTime from, LocalDateTime to) {
        LocalDateTime start = from != null ? from : LocalDateTime.now();
        LocalDateTime end = to != null ? to : start.plusDays(14);
        List<AvailabilitySlot> slots = providerId != null
                ? availabilityRepository.findOpenSlots(providerId, start, end)
                : availabilityRepository.findAllOpenSlots(start, end);
        return slots.stream().map(AvailabilityResponse::from).toList();
    }

    public void deleteAvailability(Long id) {
        if (!availabilityRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Availability slot not found");
        }
        availabilityRepository.deleteById(id);
    }

    private void apply(Provider provider, ProviderRequest request) {
        provider.setUserId(request.getUserId());
        provider.setFirstName(request.getFirstName());
        provider.setLastName(request.getLastName());
        provider.setSpecialty(request.getSpecialty());
        provider.setTimezone(request.getTimezone() == null ? "UTC" : request.getTimezone());
    }
}
