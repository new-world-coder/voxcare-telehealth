package com.pulsecare.appointment.repository;

import com.pulsecare.appointment.model.Appointment;
import com.pulsecare.appointment.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for appointment data access
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Find appointments by patient ID
     */
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);

    /**
     * Find appointments by provider ID
     */
    List<Appointment> findByProviderIdOrderByAppointmentDateDesc(Long providerId);

    /**
     * Find appointments by status
     */
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    /**
     * Find upcoming appointments for a patient
     */
    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId AND a.appointmentDate > :now AND a.status IN ('SCHEDULED', 'RESCHEDULED') ORDER BY a.appointmentDate ASC")
    List<Appointment> findUpcomingAppointmentsByPatient(@Param("patientId") Long patientId, @Param("now") LocalDateTime now);

    /**
     * Find upcoming appointments for a provider
     */
    @Query("SELECT a FROM Appointment a WHERE a.providerId = :providerId AND a.appointmentDate > :now AND a.status IN ('SCHEDULED', 'RESCHEDULED') ORDER BY a.appointmentDate ASC")
    List<Appointment> findUpcomingAppointmentsByProvider(@Param("providerId") Long providerId, @Param("now") LocalDateTime now);

    /**
     * Find appointments within a date range
     */
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate ORDER BY a.appointmentDate ASC")
    List<Appointment> findAppointmentsInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find appointments by patient and provider
     */
    List<Appointment> findByPatientIdAndProviderIdOrderByAppointmentDateDesc(Long patientId, Long providerId);

    /**
     * Find appointments by patient, provider, and status
     */
    List<Appointment> findByPatientIdAndProviderIdAndStatusOrderByAppointmentDateDesc(Long patientId, Long providerId, AppointmentStatus status);

    /**
     * Find active appointments for a provider in a time range
     */
    @Query("SELECT a FROM Appointment a WHERE a.providerId = :providerId AND a.status IN ('SCHEDULED', 'RESCHEDULED') AND a.appointmentDate BETWEEN :startTime AND :endTime")
    List<Appointment> findActiveAppointmentsInTimeRange(@Param("providerId") Long providerId, 
                                                       @Param("startTime") LocalDateTime startTime, 
                                                       @Param("endTime") LocalDateTime endTime);

    /**
     * Find appointments that need reminders (scheduled for tomorrow)
     */
    @Query("SELECT a FROM Appointment a WHERE a.status = 'SCHEDULED' AND DATE(a.appointmentDate) = DATE(:tomorrow)")
    List<Appointment> findAppointmentsNeedingReminders(@Param("tomorrow") LocalDateTime tomorrow);

    /**
     * Find cancelled appointments in a date range
     */
    @Query("SELECT a FROM Appointment a WHERE a.status = 'CANCELLED' AND a.cancelledAt BETWEEN :startDate AND :endDate ORDER BY a.cancelledAt DESC")
    List<Appointment> findCancelledAppointmentsInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
