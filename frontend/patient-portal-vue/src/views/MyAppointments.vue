<template>
  <div class="px-4 sm:px-6 lg:px-8">
    <div class="sm:flex sm:items-center">
      <div class="sm:flex-auto">
        <h1 class="text-2xl font-semibold text-gray-900">My Appointments</h1>
        <p class="mt-2 text-sm text-gray-700">
          View and manage your scheduled appointments.
        </p>
      </div>
    </div>

    <!-- Appointments List -->
    <div class="mt-8">
      <div class="card">
        <div class="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
          <table class="min-w-full divide-y divide-gray-300">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Provider
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Date & Time
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Type
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="appointment in appointments" :key="appointment.id">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-gray-900">
                    {{ appointment.providerName }}
                  </div>
                  <div class="text-sm text-gray-500">
                    {{ appointment.specialty }}
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">{{ appointment.date }}</div>
                  <div class="text-sm text-gray-500">{{ appointment.time }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ appointment.type }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    :class="`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(appointment.status)}`"
                  >
                    {{ appointment.status }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <button
                    v-if="appointment.status === 'Confirmed'"
                    @click="joinSession(appointment)"
                    class="text-primary-600 hover:text-primary-900 mr-3"
                  >
                    Join Session
                  </button>
                  <button
                    v-if="appointment.status === 'Scheduled'"
                    @click="cancelAppointment(appointment)"
                    class="text-red-600 hover:text-red-900 mr-3"
                  >
                    Cancel
                  </button>
                  <button
                    @click="rescheduleAppointment(appointment)"
                    class="text-gray-600 hover:text-gray-900"
                  >
                    Reschedule
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Quick Stats -->
    <div class="mt-8 grid grid-cols-1 gap-4 sm:grid-cols-3">
      <div class="card text-center">
        <h3 class="text-lg font-medium text-gray-900">Upcoming</h3>
        <p class="mt-2 text-3xl font-bold text-primary-600">2</p>
        <p class="mt-1 text-sm text-gray-500">appointments</p>
      </div>
      <div class="card text-center">
        <h3 class="text-lg font-medium text-gray-900">Completed</h3>
        <p class="mt-2 text-3xl font-bold text-green-600">8</p>
        <p class="mt-1 text-sm text-gray-500">this month</p>
      </div>
      <div class="card text-center">
        <h3 class="text-lg font-medium text-gray-900">Cancelled</h3>
        <p class="mt-2 text-3xl font-bold text-red-600">1</p>
        <p class="mt-1 text-sm text-gray-500">this month</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface Appointment {
  id: number
  providerName: string
  specialty: string
  date: string
  time: string
  type: string
  status: string
}

const appointments: Appointment[] = [
  {
    id: 1,
    providerName: 'Dr. Sarah Smith',
    specialty: 'Cardiology',
    date: 'Tomorrow',
    time: '2:00 PM - 3:00 PM',
    type: 'Follow-up',
    status: 'Confirmed'
  },
  {
    id: 2,
    providerName: 'Dr. Michael Johnson',
    specialty: 'Dermatology',
    date: 'Friday',
    time: '10:00 AM - 11:00 AM',
    type: 'Initial Consultation',
    status: 'Scheduled'
  },
  {
    id: 3,
    providerName: 'Dr. Emily Davis',
    specialty: 'Pediatrics',
    date: 'Today',
    time: '4:00 PM - 5:00 PM',
    type: 'Check-up',
    status: 'Completed'
  }
]

const getStatusColor = (status: string) => {
  switch (status) {
    case 'Confirmed':
      return 'bg-green-100 text-green-800'
    case 'Scheduled':
      return 'bg-blue-100 text-blue-800'
    case 'Completed':
      return 'bg-gray-100 text-gray-800'
    case 'Cancelled':
      return 'bg-red-100 text-red-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const joinSession = (appointment: Appointment) => {
  // Mock join session functionality
  console.log('Joining session for:', appointment)
  // In a real app, this would open the telehealth session
  window.open('https://meet.jit.si/demo-room', '_blank')
}

const cancelAppointment = (appointment: Appointment) => {
  // Mock cancel functionality
  console.log('Cancelling appointment:', appointment)
  appointment.status = 'Cancelled'
}

const rescheduleAppointment = (appointment: Appointment) => {
  // Mock reschedule functionality
  console.log('Rescheduling appointment:', appointment)
}
</script>
