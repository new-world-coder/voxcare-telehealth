package com.voxcare.telehealth.service;

import com.voxcare.telehealth.dto.CreateSessionRequest;
import com.voxcare.telehealth.dto.SessionResponse;
import com.voxcare.telehealth.model.SessionStatus;
import com.voxcare.telehealth.model.TelehealthSession;
import com.voxcare.telehealth.repository.TelehealthSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for telehealth business logic
 */
@Service
public class TelehealthService {

    private final TelehealthSessionRepository sessionRepository;

    @Value("${jitsi.base-url:https://meet.jit.si}")
    private String jitsiBaseUrl;

    @Autowired
    public TelehealthService(TelehealthSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    /**
     * Create a new telehealth session
     */
    public SessionResponse createSession(CreateSessionRequest request) {
        // Check if appointment already has a session
        if (sessionRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new RuntimeException("Appointment already has a telehealth session");
        }

        // Create session
        TelehealthSession session = new TelehealthSession(
                request.getAppointmentId(),
                request.getPatientId(),
                request.getProviderId(),
                request.getScheduledStartTime()
        );

        if (request.getNotes() != null) {
            session.setNotes(request.getNotes());
        }

        // Generate Jitsi room ID and URL
        String roomId = generateRoomId(request.getAppointmentId());
        session.setJitsiRoomId(roomId);
        session.setJitsiRoomUrl(generateRoomUrl(roomId));

        session = sessionRepository.save(session);
        return convertToResponse(session);
    }

    /**
     * Get session by ID
     */
    public SessionResponse getSessionById(String id) {
        TelehealthSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        return convertToResponse(session);
    }

    /**
     * Get session by appointment ID
     */
    public SessionResponse getSessionByAppointmentId(Long appointmentId) {
        TelehealthSession session = sessionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Session not found for appointment: " + appointmentId));
        return convertToResponse(session);
    }

    /**
     * Get sessions by patient ID
     */
    public List<SessionResponse> getSessionsByPatient(Long patientId) {
        return sessionRepository.findByPatientIdOrderByScheduledStartTimeDesc(patientId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get sessions by provider ID
     */
    public List<SessionResponse> getSessionsByProvider(Long providerId) {
        return sessionRepository.findByProviderIdOrderByScheduledStartTimeDesc(providerId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming sessions for a patient
     */
    public List<SessionResponse> getUpcomingSessionsByPatient(Long patientId) {
        return sessionRepository.findUpcomingSessionsByPatient(patientId, LocalDateTime.now())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming sessions for a provider
     */
    public List<SessionResponse> getUpcomingSessionsByProvider(Long providerId) {
        return sessionRepository.findUpcomingSessionsByProvider(providerId, LocalDateTime.now())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Start a session
     */
    public SessionResponse startSession(String id) {
        TelehealthSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));

        if (!session.canBeStarted()) {
            throw new RuntimeException("Session cannot be started. Current status: " + session.getStatus());
        }

        session.start();
        session = sessionRepository.save(session);
        return convertToResponse(session);
    }

    /**
     * End a session
     */
    public SessionResponse endSession(String id) {
        TelehealthSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));

        if (!session.canBeEnded()) {
            throw new RuntimeException("Session cannot be ended. Current status: " + session.getStatus());
        }

        session.end();
        session = sessionRepository.save(session);
        return convertToResponse(session);
    }

    /**
     * Cancel a session
     */
    public SessionResponse cancelSession(String id) {
        TelehealthSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));

        if (session.getStatus() != SessionStatus.SCHEDULED) {
            throw new RuntimeException("Session cannot be cancelled. Current status: " + session.getStatus());
        }

        session.cancel();
        session = sessionRepository.save(session);
        return convertToResponse(session);
    }

    /**
     * Join a session
     */
    public SessionResponse joinSession(String id, String participantId) {
        TelehealthSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));

        if (session.getStatus() != SessionStatus.SCHEDULED && 
            session.getStatus() != SessionStatus.WAITING_FOR_PARTICIPANTS) {
            throw new RuntimeException("Session cannot be joined. Current status: " + session.getStatus());
        }

        // Add participant if not already present
        if (session.getParticipants() == null) {
            session.setParticipants(List.of(participantId));
        } else if (!session.getParticipants().contains(participantId)) {
            session.getParticipants().add(participantId);
        }

        // Update status if this is the first participant
        if (session.getStatus() == SessionStatus.SCHEDULED) {
            session.setStatus(SessionStatus.WAITING_FOR_PARTICIPANTS);
        }

        session = sessionRepository.save(session);
        return convertToResponse(session);
    }

    /**
     * Update session notes
     */
    public SessionResponse updateNotes(String id, String notes) {
        TelehealthSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));

        session.setNotes(notes);
        session = sessionRepository.save(session);
        return convertToResponse(session);
    }

    /**
     * Update session diagnosis
     */
    public SessionResponse updateDiagnosis(String id, String diagnosis) {
        TelehealthSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));

        session.setDiagnosis(diagnosis);
        session = sessionRepository.save(session);
        return convertToResponse(session);
    }

    /**
     * Update session prescription
     */
    public SessionResponse updatePrescription(String id, String prescription) {
        TelehealthSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));

        session.setPrescription(prescription);
        session = sessionRepository.save(session);
        return convertToResponse(session);
    }

    /**
     * Get sessions in a date range
     */
    public List<SessionResponse> getSessionsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return sessionRepository.findSessionsInDateRange(startDate, endDate)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get sessions ready to start
     */
    public List<SessionResponse> getSessionsReadyToStart() {
        return sessionRepository.findSessionsReadyToStart(LocalDateTime.now())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Generate a unique room ID for Jitsi
     */
    private String generateRoomId(Long appointmentId) {
        return "voxcare-" + appointmentId + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Generate the full Jitsi room URL
     */
    private String generateRoomUrl(String roomId) {
        return jitsiBaseUrl + "/" + roomId;
    }

    /**
     * Convert TelehealthSession entity to DTO
     */
    private SessionResponse convertToResponse(TelehealthSession session) {
        return new SessionResponse(
                session.getId(),
                session.getAppointmentId(),
                session.getPatientId(),
                session.getProviderId(),
                session.getScheduledStartTime(),
                session.getActualStartTime(),
                session.getEndTime(),
                session.getDurationMinutes(),
                session.getStatus(),
                session.getJitsiRoomId(),
                session.getJitsiRoomUrl(),
                session.getRecordingUrl(),
                session.getParticipants(),
                session.getNotes(),
                session.getDiagnosis(),
                session.getPrescription(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
