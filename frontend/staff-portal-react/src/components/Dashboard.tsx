import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import './Dashboard.css';

interface Appointment {
  id: number;
  patientId?: number;
  providerId?: number;
  patientName?: string;
  providerName?: string;
  appointmentDate?: string;
  startTime?: string;
  endTime?: string;
  durationMinutes?: number;
  status: string;
}

interface Patient {
  id: number;
  firstName: string;
  lastName: string;
  phone?: string;
}

interface Provider {
  id: number;
  displayName?: string;
  specialty?: string;
}

interface AvailabilitySlot {
  id: number;
  providerId: number;
  startTime: string;
  endTime: string;
  status: string;
}

const Dashboard: React.FC = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [providers, setProviders] = useState<Provider[]>([]);
  const [openSlots, setOpenSlots] = useState<AvailabilitySlot[]>([]);
  const [loading, setLoading] = useState(true);
  const [voiceStatus, setVoiceStatus] = useState<string | null>(null);
  const [showCallModal, setShowCallModal] = useState(false);
  const [callForm, setCallForm] = useState({
    patientId: '',
    providerId: '',
    purpose: 'BOOKING',
    to: '',
  });

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const [apptRes, patientRes, providerRes, slotsRes] = await Promise.allSettled([
        axios.get<Appointment[]>('/api/appointments/range', {
          params: {
            startDate: new Date(Date.now() - 7 * 86400000).toISOString().slice(0, 19),
            endDate: new Date(Date.now() + 14 * 86400000).toISOString().slice(0, 19),
          },
        }),
        axios.get<Patient[]>('/api/patients'),
        axios.get<Provider[]>('/api/providers'),
        axios.get<AvailabilitySlot[]>('/api/providers/slots/open'),
      ]);

      if (apptRes.status === 'fulfilled') {
        setAppointments(apptRes.value.data || []);
      } else {
        setAppointments([]);
      }
      if (patientRes.status === 'fulfilled') {
        setPatients(patientRes.value.data || []);
      }
      if (providerRes.status === 'fulfilled') {
        setProviders(providerRes.value.data || []);
      }
      if (slotsRes.status === 'fulfilled') {
        setOpenSlots(slotsRes.value.data || []);
      }
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const formatDateTime = (dateTime?: string) => {
    if (!dateTime) return '—';
    return new Date(dateTime).toLocaleString();
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'SCHEDULED':
        return 'status-scheduled';
      case 'IN_PROGRESS':
        return 'status-in-progress';
      case 'COMPLETED':
        return 'status-completed';
      case 'CANCELLED':
        return 'status-cancelled';
      default:
        return 'status-default';
    }
  };

  const openCallModal = (purpose: string, patient?: Patient) => {
    setCallForm({
      patientId: patient ? String(patient.id) : '',
      providerId: providers[0] ? String(providers[0].id) : '',
      purpose,
      to: patient?.phone || '',
    });
    setVoiceStatus(null);
    setShowCallModal(true);
  };

  const initiateVoiceCall = async () => {
    setVoiceStatus('Starting AI call…');
    try {
      const payload: Record<string, unknown> = {
        purpose: callForm.purpose,
      };
      if (callForm.patientId) payload.patientId = Number(callForm.patientId);
      if (callForm.providerId) payload.providerId = Number(callForm.providerId);
      if (callForm.to) payload.to = callForm.to;

      const res = await axios.post('/api/voice/calls', payload);
      setVoiceStatus(`Call queued (${res.data.provider}): ${res.data.status} — id ${res.data.id}`);
      setShowCallModal(false);
    } catch (err: unknown) {
      const message = axios.isAxiosError(err)
        ? err.response?.data?.message || err.message
        : 'Failed to start call';
      setVoiceStatus(String(message));
    }
  };

  if (loading) {
    return (
      <div className="dashboard">
        <div className="loading">Loading appointments...</div>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>VoxCare Staff Dashboard</h1>
        <div className="header-actions">
          <button className="btn btn-primary" type="button" onClick={() => openCallModal('BOOKING')}>
            AI Call to Book
          </button>
          <button className="btn btn-secondary" type="button" onClick={() => openCallModal('REMINDER')}>
            AI Reminder Call
          </button>
          <button className="btn btn-secondary" type="button" onClick={loadData}>
            Refresh
          </button>
        </div>
      </header>

      <div className="dashboard-content">
        {voiceStatus && <div className="voice-banner">{voiceStatus}</div>}

        <div className="stats-grid">
          <div className="stat-card">
            <h3>Appointments (window)</h3>
            <p className="stat-number">{appointments.length}</p>
          </div>
          <div className="stat-card">
            <h3>Patients</h3>
            <p className="stat-number">{patients.length}</p>
          </div>
          <div className="stat-card">
            <h3>Open slots</h3>
            <p className="stat-number">{openSlots.length}</p>
          </div>
        </div>

        <div className="appointments-section">
          <h2>Patients</h2>
          <div className="appointments-table">
            <table>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Phone</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {patients.length === 0 && (
                  <tr>
                    <td colSpan={3}>No patients loaded. Ensure patient-service is running.</td>
                  </tr>
                )}
                {patients.map((patient) => (
                  <tr key={patient.id}>
                    <td>
                      {patient.firstName} {patient.lastName}
                    </td>
                    <td>{patient.phone || '—'}</td>
                    <td>
                      <button
                        className="btn btn-small"
                        type="button"
                        onClick={() => openCallModal('BOOKING', patient)}
                      >
                        Call to book
                      </button>
                      <button
                        className="btn btn-small"
                        type="button"
                        onClick={() => openCallModal('REMINDER', patient)}
                      >
                        Remind
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="appointments-section">
          <h2>Recent Appointments</h2>
          <div className="appointments-table">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Patient</th>
                  <th>Provider</th>
                  <th>Time</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {appointments.length === 0 && (
                  <tr>
                    <td colSpan={5}>No appointments in range.</td>
                  </tr>
                )}
                {appointments.map((appointment) => (
                  <tr key={appointment.id}>
                    <td>{appointment.id}</td>
                    <td>{appointment.patientName || appointment.patientId || '—'}</td>
                    <td>{appointment.providerName || appointment.providerId || '—'}</td>
                    <td>
                      {formatDateTime(appointment.startTime || appointment.appointmentDate)}
                    </td>
                    <td>
                      <span className={`status ${getStatusColor(appointment.status)}`}>
                        {appointment.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {showCallModal && (
        <div className="modal-backdrop" role="dialog" aria-modal="true">
          <div className="modal-card">
            <h2>Start GetDial AI call</h2>
            <label>
              Purpose
              <select
                value={callForm.purpose}
                onChange={(e) => setCallForm({ ...callForm, purpose: e.target.value })}
              >
                <option value="BOOKING">BOOKING</option>
                <option value="REMINDER">REMINDER</option>
                <option value="RESCHEDULE">RESCHEDULE</option>
                <option value="FOLLOW_UP">FOLLOW_UP</option>
              </select>
            </label>
            <label>
              Patient
              <select
                value={callForm.patientId}
                onChange={(e) => {
                  const id = e.target.value;
                  const patient = patients.find((p) => String(p.id) === id);
                  setCallForm({
                    ...callForm,
                    patientId: id,
                    to: patient?.phone || callForm.to,
                  });
                }}
              >
                <option value="">Select patient</option>
                {patients.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.firstName} {p.lastName}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Provider (optional)
              <select
                value={callForm.providerId}
                onChange={(e) => setCallForm({ ...callForm, providerId: e.target.value })}
              >
                <option value="">None</option>
                {providers.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.displayName || `Provider ${p.id}`} ({p.specialty})
                  </option>
                ))}
              </select>
            </label>
            <label>
              Phone (optional if patient has phone on file)
              <input
                type="tel"
                value={callForm.to}
                onChange={(e) => setCallForm({ ...callForm, to: e.target.value })}
                placeholder="+15551234567"
              />
            </label>
            <div className="modal-actions">
              <button className="btn btn-secondary" type="button" onClick={() => setShowCallModal(false)}>
                Cancel
              </button>
              <button className="btn btn-primary" type="button" onClick={initiateVoiceCall}>
                Start call
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
