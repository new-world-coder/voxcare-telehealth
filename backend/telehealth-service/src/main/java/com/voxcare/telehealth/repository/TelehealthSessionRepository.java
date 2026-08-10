package com.voxcare.telehealth.repository;

import com.voxcare.telehealth.model.SessionStatus;
import com.voxcare.telehealth.model.TelehealthSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for telehealth session data access
 */
@Repository
public interface TelehealthSessionRepository extends MongoRepository<TelehealthSession, String> {

    /**
     * Find sessions by appointment ID
     */
    Optional<TelehealthSession> findByAppointmentId(Long appointmentId);

    /**
     * Find sessions by patient ID
     */
    List<TelehealthSession> findByPatientIdOrderByScheduledStartTimeDesc(Long patientId);

    /**
     * Find sessions by provider ID
     */
    List<TelehealthSession> findByProviderIdOrderByScheduledStartTimeDesc(Long providerId);

    /**
     * Find sessions by status
     */
    List<TelehealthSession> findByStatusOrderByScheduledStartTimeAsc(SessionStatus status);

    /**
     * Find active sessions
     */
    List<TelehealthSession> findByStatusInOrderByScheduledStartTimeAsc(List<SessionStatus> statuses);

    /**
     * Find sessions within a date range
     */
    @Query("{'scheduledStartTime': {$gte: ?0, $lte: ?1}}")
    List<TelehealthSession> findSessionsInDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find upcoming sessions for a patient
     */
    @Query("{'patientId': ?0, 'scheduledStartTime': {$gt: ?1}, 'status': {$in: ['SCHEDULED', 'WAITING_FOR_PARTICIPANTS']}}")
    List<TelehealthSession> findUpcomingSessionsByPatient(Long patientId, LocalDateTime now);

    /**
     * Find upcoming sessions for a provider
     */
    @Query("{'providerId': ?0, 'scheduledStartTime': {$gt: ?1}, 'status': {$in: ['SCHEDULED', 'WAITING_FOR_PARTICIPANTS']}}")
    List<TelehealthSession> findUpcomingSessionsByProvider(Long providerId, LocalDateTime now);

    /**
     * Find sessions by patient and provider
     */
    List<TelehealthSession> findByPatientIdAndProviderIdOrderByScheduledStartTimeDesc(Long patientId, Long providerId);

    /**
     * Find sessions by patient, provider, and status
     */
    List<TelehealthSession> findByPatientIdAndProviderIdAndStatusOrderByScheduledStartTimeDesc(Long patientId, Long providerId, SessionStatus status);

    /**
     * Find sessions that need to be started (scheduled for now)
     */
    @Query("{'status': 'SCHEDULED', 'scheduledStartTime': {$lte: ?0}}")
    List<TelehealthSession> findSessionsReadyToStart(LocalDateTime now);

    /**
     * Find completed sessions in a date range
     */
    @Query("{'status': 'COMPLETED', 'endTime': {$gte: ?0, $lte: ?1}}")
    List<TelehealthSession> findCompletedSessionsInDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find sessions by Jitsi room ID
     */
    Optional<TelehealthSession> findByJitsiRoomId(String jitsiRoomId);

    /**
     * Check if appointment already has a session
     */
    boolean existsByAppointmentId(Long appointmentId);
}
