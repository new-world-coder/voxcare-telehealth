import React from 'react'

const Dashboard: React.FC = () => {
  return (
    <div className="px-4 sm:px-6 lg:px-8">
      <div className="sm:flex sm:items-center">
        <div className="sm:flex-auto">
          <h1 className="text-2xl font-semibold text-gray-900">Dashboard</h1>
          <p className="mt-2 text-sm text-gray-700">
            Welcome to PulseCare Staff Portal. Manage your availability and appointments.
          </p>
        </div>
      </div>

      <div className="mt-8 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {/* Quick Stats */}
        <div className="card">
          <h3 className="text-lg font-medium text-gray-900">Today's Appointments</h3>
          <p className="mt-2 text-3xl font-bold text-primary-600">3</p>
          <p className="mt-1 text-sm text-gray-500">2 confirmed, 1 pending</p>
        </div>

        <div className="card">
          <h3 className="text-lg font-medium text-gray-900">Available Slots</h3>
          <p className="mt-2 text-3xl font-bold text-green-600">12</p>
          <p className="mt-1 text-sm text-gray-500">Next 7 days</p>
        </div>

        <div className="card">
          <h3 className="text-lg font-medium text-gray-900">Total Patients</h3>
          <p className="mt-2 text-3xl font-bold text-blue-600">47</p>
          <p className="mt-1 text-sm text-gray-500">Active patients</p>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="mt-8">
        <h2 className="text-lg font-medium text-gray-900 mb-4">Recent Activity</h2>
        <div className="card">
          <div className="flow-root">
            <ul className="-my-5 divide-y divide-gray-200">
              <li className="py-4">
                <div className="flex items-center space-x-4">
                  <div className="flex-shrink-0">
                    <div className="h-8 w-8 rounded-full bg-green-100 flex items-center justify-center">
                      <span className="text-green-600 text-sm font-medium">✓</span>
                    </div>
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900 truncate">
                      Appointment confirmed
                    </p>
                    <p className="text-sm text-gray-500">
                      John Doe - Tomorrow at 2:00 PM
                    </p>
                  </div>
                  <div className="flex-shrink-0 text-sm text-gray-500">
                    2 hours ago
                  </div>
                </div>
              </li>
              <li className="py-4">
                <div className="flex items-center space-x-4">
                  <div className="flex-shrink-0">
                    <div className="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center">
                      <span className="text-blue-600 text-sm font-medium">📅</span>
                    </div>
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900 truncate">
                      New appointment booked
                    </p>
                    <p className="text-sm text-gray-500">
                      Jane Smith - Friday at 10:00 AM
                    </p>
                  </div>
                  <div className="flex-shrink-0 text-sm text-gray-500">
                    4 hours ago
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Dashboard
