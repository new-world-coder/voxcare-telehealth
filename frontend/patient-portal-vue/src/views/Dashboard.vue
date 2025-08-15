<template>
  <div class="px-4 sm:px-6 lg:px-8">
    <div class="sm:flex sm:items-center">
      <div class="sm:flex-auto">
        <h1 class="text-2xl font-semibold text-gray-900">Dashboard</h1>
        <p class="mt-2 text-sm text-gray-700">
          Welcome to PulseCare Patient Portal. Book appointments and manage your health.
        </p>
      </div>
    </div>

    <div class="mt-8 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
      <!-- Quick Stats -->
      <div class="card">
        <h3 class="text-lg font-medium text-gray-900">Upcoming Appointments</h3>
        <p class="mt-2 text-3xl font-bold text-primary-600">2</p>
        <p class="mt-1 text-sm text-gray-500">Next 7 days</p>
      </div>

      <div class="card">
        <h3 class="text-lg font-medium text-gray-900">Completed Sessions</h3>
        <p class="mt-2 text-3xl font-bold text-green-600">8</p>
        <p class="mt-1 text-sm text-gray-500">This month</p>
      </div>

      <div class="card">
        <h3 class="text-lg font-medium text-gray-900">Available Providers</h3>
        <p class="mt-2 text-3xl font-bold text-blue-600">12</p>
        <p class="mt-1 text-sm text-gray-500">Online now</p>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="mt-8">
      <h2 class="text-lg font-medium text-gray-900 mb-4">Quick Actions</h2>
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <div class="card text-center cursor-pointer hover:shadow-lg transition-shadow">
          <div class="text-4xl mb-2">📅</div>
          <h3 class="text-lg font-medium text-gray-900">Book Appointment</h3>
          <p class="text-sm text-gray-500">Schedule with a provider</p>
        </div>
        <div class="card text-center cursor-pointer hover:shadow-lg transition-shadow">
          <div class="text-4xl mb-2">👨‍⚕️</div>
          <h3 class="text-lg font-medium text-gray-900">Find Provider</h3>
          <p class="text-sm text-gray-500">Browse specialties</p>
        </div>
        <div class="card text-center cursor-pointer hover:shadow-lg transition-shadow">
          <div class="text-4xl mb-2">📋</div>
          <h3 class="text-lg font-medium text-gray-900">My Appointments</h3>
          <p class="text-sm text-gray-500">View schedule</p>
        </div>
      </div>
    </div>

    <!-- Recent Activity -->
    <div class="mt-8">
      <h2 class="text-lg font-medium text-gray-900 mb-4">Recent Activity</h2>
      <div class="card">
        <div class="flow-root">
          <ul class="-my-5 divide-y divide-gray-200">
            <li class="py-4">
              <div class="flex items-center space-x-4">
                <div class="flex-shrink-0">
                  <div class="h-8 w-8 rounded-full bg-green-100 flex items-center justify-center">
                    <span class="text-green-600 text-sm font-medium">✓</span>
                  </div>
                </div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-gray-900 truncate">
                    Appointment confirmed
                  </p>
                  <p class="text-sm text-gray-500">
                    Dr. Smith - Tomorrow at 2:00 PM
                  </p>
                </div>
                <div class="flex-shrink-0 text-sm text-gray-500">
                  2 hours ago
                </div>
              </div>
            </li>
            <li class="py-4">
              <div class="flex items-center space-x-4">
                <div class="flex-shrink-0">
                  <div class="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center">
                    <span class="text-blue-600 text-sm font-medium">📅</span>
                  </div>
                </div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-gray-900 truncate">
                    New appointment booked
                  </p>
                  <p class="text-sm text-gray-500">
                    Dr. Johnson - Friday at 10:00 AM
                  </p>
                </div>
                <div class="flex-shrink-0 text-sm text-gray-500">
                  4 hours ago
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Dashboard component logic
</script>
