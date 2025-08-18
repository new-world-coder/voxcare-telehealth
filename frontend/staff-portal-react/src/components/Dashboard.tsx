import React, { useState, useEffect } from 'react';
import './Dashboard.css';

interface Appointment {
  id: number;
  patientName: string;
  providerName: string;
  startTime: string;
  endTime: string;
  status: string;
}

const Dashboard: React.FC = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Simulate loading appointments
    setTimeout(() => {
      setAppointments([
        {
          id: 1,
          patientName: 'John Doe',
          providerName: 'Dr. Smith',
          startTime: '2024-01-15T09:00:00',
          endTime: '2024-01-15T10:00:00',
          status: 'SCHEDULED'
        },
        {
          id: 2,
          patientName: 'Jane Smith',
          providerName: 'Dr. Johnson',
          startTime: '2024-01-15T14:00:00',
          endTime: '2024-01-15T15:00:00',
          status: 'IN_PROGRESS'
        }
      ]);
      setLoading(false);
    }, 1000);
  }, []);

  const formatDateTime = (dateTime: string) => {
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
        <h1>PulseCare Staff Dashboard</h1>
        <div className="header-actions">
          <button className="btn btn-primary">New Appointment</button>
          <button className="btn btn-secondary">Manage Availability</button>
        </div>
      </header>

      <div className="dashboard-content">
        <div className="stats-grid">
          <div className="stat-card">
            <h3>Today's Appointments</h3>
            <p className="stat-number">{appointments.length}</p>
          </div>
          <div className="stat-card">
            <h3>Pending</h3>
            <p className="stat-number">
              {appointments.filter(a => a.status === 'SCHEDULED').length}
            </p>
          </div>
          <div className="stat-card">
            <h3>In Progress</h3>
            <p className="stat-number">
              {appointments.filter(a => a.status === 'IN_PROGRESS').length}
            </p>
          </div>
        </div>

        <div className="appointments-section">
          <h2>Recent Appointments</h2>
          <div className="appointments-table">
            <table>
              <thead>
                <tr>
                  <th>Patient</th>
                  <th>Provider</th>
                  <th>Time</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {appointments.map(appointment => (
                  <tr key={appointment.id}>
                    <td>{appointment.patientName}</td>
                    <td>{appointment.providerName}</td>
                    <td>
                      {formatDateTime(appointment.startTime)} - {formatDateTime(appointment.endTime)}
                    </td>
                    <td>
                      <span className={`status ${getStatusColor(appointment.status)}`}>
                        {appointment.status}
                      </span>
                    </td>
                    <td>
                      <button className="btn btn-small">View</button>
                      <button className="btn btn-small">Edit</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
