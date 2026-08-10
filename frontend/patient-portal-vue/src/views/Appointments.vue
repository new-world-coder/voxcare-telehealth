<template>
  <div class="appointments">
    <h2>My Appointments</h2>
    
    <div class="appointments-header">
      <button class="btn btn-primary" @click="showBookingModal = true">
        Book New Appointment
      </button>
    </div>

    <div class="appointments-list">
      <div v-if="appointments.length === 0" class="no-appointments">
        <p>No appointments scheduled. Book your first appointment!</p>
      </div>
      
      <div v-else class="appointment-cards">
        <div 
          v-for="appointment in appointments" 
          :key="appointment.id" 
          class="appointment-card"
        >
          <div class="appointment-header">
            <h3>{{ appointment.providerName }}</h3>
            <span :class="['status', getStatusClass(appointment.status)]">
              {{ appointment.status }}
            </span>
          </div>
          
          <div class="appointment-details">
            <p><strong>Date:</strong> {{ formatDate(appointment.startTime) }}</p>
            <p><strong>Time:</strong> {{ formatTime(appointment.startTime) }} - {{ formatTime(appointment.endTime) }}</p>
            <p><strong>Type:</strong> {{ appointment.type }}</p>
          </div>
          
          <div class="appointment-actions">
            <button 
              v-if="appointment.status === 'SCHEDULED'" 
              class="btn btn-secondary"
              @click="rescheduleAppointment(appointment.id)"
            >
              Reschedule
            </button>
            <button 
              v-if="appointment.status === 'SCHEDULED'" 
              class="btn btn-danger"
              @click="cancelAppointment(appointment.id)"
            >
              Cancel
            </button>
            <button 
              v-if="appointment.status === 'SCHEDULED'" 
              class="btn btn-primary"
              @click="joinSession(appointment.id)"
            >
              Join Session
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Booking Modal -->
    <div v-if="showBookingModal" class="modal-overlay" @click="showBookingModal = false">
      <div class="modal" @click.stop>
        <h3>Book New Appointment</h3>
        <form @submit.prevent="bookAppointment">
          <div class="form-group">
            <label>Provider</label>
            <select v-model="newAppointment.providerId" required>
              <option value="">Select a provider</option>
              <option v-for="provider in providers" :key="provider.id" :value="provider.id">
                {{ provider.name }} - {{ provider.specialty }}
              </option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Date</label>
            <input type="date" v-model="newAppointment.date" required>
          </div>
          
          <div class="form-group">
            <label>Time</label>
            <select v-model="newAppointment.time" required>
              <option value="">Select time</option>
              <option value="09:00">9:00 AM</option>
              <option value="10:00">10:00 AM</option>
              <option value="11:00">11:00 AM</option>
              <option value="14:00">2:00 PM</option>
              <option value="15:00">3:00 PM</option>
              <option value="16:00">4:00 PM</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Notes</label>
            <textarea v-model="newAppointment.notes" rows="3"></textarea>
          </div>
          
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="showBookingModal = false">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary">Book Appointment</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from 'axios'

export default {
  name: 'Appointments',
  setup() {
    const appointments = ref([])
    const providers = ref([])
    const showBookingModal = ref(false)
    const newAppointment = ref({
      providerId: '',
      date: '',
      time: '',
      notes: ''
    })

    const loadAppointments = async () => {
      try {
        const response = await axios.get('/api/appointments')
        appointments.value = response.data
      } catch (error) {
        console.error('Failed to load appointments:', error)
      }
    }

    const loadProviders = async () => {
      try {
        const response = await axios.get('/api/providers')
        providers.value = response.data
      } catch (error) {
        console.error('Failed to load providers:', error)
      }
    }

    const bookAppointment = async () => {
      try {
        const [hour, minute] = newAppointment.value.time.split(':').map(Number)
        const startTime = `${newAppointment.value.date}T${newAppointment.value.time}:00`
        const endHour = String(hour + 1).padStart(2, '0')
        const endTime = `${newAppointment.value.date}T${endHour}:${String(minute).padStart(2, '0')}:00`
        
        const response = await axios.post('/api/appointments', {
          patientId: 1,
          providerId: Number(newAppointment.value.providerId),
          startTime,
          endTime,
          notes: newAppointment.value.notes
        })
        
        appointments.value.push(response.data)
        showBookingModal.value = false
        newAppointment.value = { providerId: '', date: '', time: '', notes: '' }
      } catch (error) {
        console.error('Failed to book appointment:', error)
      }
    }

    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleDateString()
    }

    const formatTime = (dateString) => {
      return new Date(dateString).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    }

    const getStatusClass = (status) => {
      return `status-${status.toLowerCase()}`
    }

    const rescheduleAppointment = (id) => {
      // Implementation for rescheduling
      console.log('Reschedule appointment:', id)
    }

    const cancelAppointment = async (id) => {
      if (confirm('Are you sure you want to cancel this appointment?')) {
        try {
          await axios.delete(`/api/appointments/${id}`)
          appointments.value = appointments.value.filter(a => a.id !== id)
        } catch (error) {
          console.error('Failed to cancel appointment:', error)
        }
      }
    }

    const joinSession = (id) => {
      // Implementation for joining telehealth session
      console.log('Join session:', id)
    }

    onMounted(() => {
      loadAppointments()
      loadProviders()
    })

    return {
      appointments,
      providers,
      showBookingModal,
      newAppointment,
      bookAppointment,
      formatDate,
      formatTime,
      getStatusClass,
      rescheduleAppointment,
      cancelAppointment,
      joinSession
    }
  }
}
</script>

<style scoped>
.appointments {
  max-width: 800px;
  margin: 0 auto;
}

.appointments-header {
  margin-bottom: 2rem;
  text-align: right;
}

.appointments-list {
  margin-top: 2rem;
}

.no-appointments {
  text-align: center;
  padding: 3rem;
  color: #7f8c8d;
}

.appointment-cards {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.appointment-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.appointment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.appointment-header h3 {
  margin: 0;
  color: #2c3e50;
}

.status {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 500;
  text-transform: uppercase;
}

.status-scheduled {
  background-color: #e3f2fd;
  color: #1976d2;
}

.status-completed {
  background-color: #e8f5e8;
  color: #388e3c;
}

.status-cancelled {
  background-color: #ffebee;
  color: #d32f2f;
}

.appointment-details {
  margin-bottom: 1.5rem;
}

.appointment-details p {
  margin: 0.5rem 0;
  color: #555;
}

.appointment-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  text-decoration: none;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-primary {
  background-color: #3498db;
  color: white;
}

.btn-primary:hover {
  background-color: #2980b9;
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background-color: #7f8c8d;
}

.btn-danger {
  background-color: #e74c3c;
  color: white;
}

.btn-danger:hover {
  background-color: #c0392b;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal h3 {
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #2c3e50;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group textarea {
  resize: vertical;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
}

@media (max-width: 768px) {
  .appointment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .appointment-actions {
    justify-content: center;
  }
  
  .modal {
    margin: 1rem;
    padding: 1.5rem;
  }
}
</style>
