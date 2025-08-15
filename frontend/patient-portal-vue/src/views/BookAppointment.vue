<template>
  <div class="px-4 sm:px-6 lg:px-8">
    <div class="sm:flex sm:items-center">
      <div class="sm:flex-auto">
        <h1 class="text-2xl font-semibold text-gray-900">Book Appointment</h1>
        <p class="mt-2 text-sm text-gray-700">
          Find and book an appointment with a healthcare provider.
        </p>
      </div>
    </div>

    <!-- Search Filters -->
    <div class="mt-8 card">
      <h3 class="text-lg font-medium text-gray-900 mb-4">Search Filters</h3>
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-4">
        <div>
          <label for="specialty" class="block text-sm font-medium text-gray-700">
            Specialty
          </label>
          <select
            id="specialty"
            v-model="selectedSpecialty"
            class="input-field mt-1"
          >
            <option value="">All Specialties</option>
            <option value="cardiology">Cardiology</option>
            <option value="dermatology">Dermatology</option>
            <option value="neurology">Neurology</option>
            <option value="orthopedics">Orthopedics</option>
            <option value="pediatrics">Pediatrics</option>
          </select>
        </div>
        <div>
          <label for="date" class="block text-sm font-medium text-gray-700">
            Preferred Date
          </label>
          <input
            type="date"
            id="date"
            v-model="preferredDate"
            class="input-field mt-1"
          />
        </div>
        <div>
          <label for="time" class="block text-sm font-medium text-gray-700">
            Preferred Time
          </label>
          <select
            id="time"
            v-model="preferredTime"
            class="input-field mt-1"
          >
            <option value="">Any Time</option>
            <option value="morning">Morning (9 AM - 12 PM)</option>
            <option value="afternoon">Afternoon (12 PM - 5 PM)</option>
            <option value="evening">Evening (5 PM - 8 PM)</option>
          </select>
        </div>
        <div class="flex items-end">
          <button
            @click="searchProviders"
            class="btn-primary w-full"
          >
            Search
          </button>
        </div>
      </div>
    </div>

    <!-- Available Providers -->
    <div class="mt-8">
      <h3 class="text-lg font-medium text-gray-900 mb-4">Available Providers</h3>
      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <div
          v-for="provider in availableProviders"
          :key="provider.id"
          class="card hover:shadow-lg transition-shadow cursor-pointer"
          @click="selectProvider(provider)"
        >
          <div class="flex items-center space-x-4">
            <div class="flex-shrink-0">
              <div class="h-12 w-12 rounded-full bg-primary-100 flex items-center justify-center">
                <span class="text-primary-600 text-lg font-medium">
                  {{ provider.name.charAt(0) }}
                </span>
              </div>
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-900 truncate">
                {{ provider.name }}
              </p>
              <p class="text-sm text-gray-500">{{ provider.specialty }}</p>
              <p class="text-sm text-gray-500">{{ provider.rating }} ⭐</p>
            </div>
          </div>
          <div class="mt-4">
            <p class="text-sm text-gray-600">
              Available slots: {{ provider.availableSlots }}
            </p>
            <button
              @click.stop="bookWithProvider(provider)"
              class="btn-primary w-full mt-2"
            >
              Book Appointment
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Selected Provider Modal -->
    <div
      v-if="selectedProvider"
      class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50"
      @click="closeModal"
    >
      <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white" @click.stop>
        <div class="mt-3">
          <h3 class="text-lg font-medium text-gray-900 mb-4">
            Book with {{ selectedProvider.name }}
          </h3>
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">Date</label>
              <input
                type="date"
                v-model="bookingDate"
                class="input-field mt-1"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Time</label>
              <select v-model="bookingTime" class="input-field mt-1">
                <option value="">Select time</option>
                <option value="09:00">9:00 AM</option>
                <option value="10:00">10:00 AM</option>
                <option value="11:00">11:00 AM</option>
                <option value="14:00">2:00 PM</option>
                <option value="15:00">3:00 PM</option>
                <option value="16:00">4:00 PM</option>
              </select>
            </div>
            <div class="flex space-x-3">
              <button
                @click="confirmBooking"
                class="btn-primary flex-1"
              >
                Confirm Booking
              </button>
              <button
                @click="closeModal"
                class="btn-secondary flex-1"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface Provider {
  id: number
  name: string
  specialty: string
  rating: number
  availableSlots: number
}

const selectedSpecialty = ref('')
const preferredDate = ref('')
const preferredTime = ref('')
const selectedProvider = ref<Provider | null>(null)
const bookingDate = ref('')
const bookingTime = ref('')

const availableProviders: Provider[] = [
  {
    id: 1,
    name: 'Dr. Sarah Smith',
    specialty: 'Cardiology',
    rating: 4.8,
    availableSlots: 5
  },
  {
    id: 2,
    name: 'Dr. Michael Johnson',
    specialty: 'Dermatology',
    rating: 4.9,
    availableSlots: 3
  },
  {
    id: 3,
    name: 'Dr. Emily Davis',
    specialty: 'Pediatrics',
    rating: 4.7,
    availableSlots: 7
  }
]

const searchProviders = () => {
  // Mock search functionality
  console.log('Searching providers with:', {
    specialty: selectedSpecialty.value,
    date: preferredDate.value,
    time: preferredTime.value
  })
}

const selectProvider = (provider: Provider) => {
  selectedProvider.value = provider
}

const closeModal = () => {
  selectedProvider.value = null
  bookingDate.value = ''
  bookingTime.value = ''
}

const bookWithProvider = (provider: Provider) => {
  selectedProvider.value = provider
}

const confirmBooking = () => {
  // Mock booking confirmation
  console.log('Booking confirmed:', {
    provider: selectedProvider.value?.name,
    date: bookingDate.value,
    time: bookingTime.value
  })
  closeModal()
}
</script>
