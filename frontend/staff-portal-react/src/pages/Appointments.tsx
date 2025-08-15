import React from 'react'

const Appointments: React.FC = () => {
  const appointments = [
    {
      id: 1,
      patientName: 'John Doe',
      date: 'Tomorrow',
      time: '2:00 PM - 3:00 PM',
      status: 'Confirmed',
      type: 'Follow-up'
    },
    {
      id: 2,
      patientName: 'Jane Smith',
      date: 'Friday',
      time: '10:00 AM - 11:00 AM',
      status: 'Pending',
      type: 'Initial Consultation'
    },
    {
      id: 3,
      patientName: 'Mike Johnson',
      date: 'Today',
      time: '4:00 PM - 5:00 PM',
      status: 'Completed',
      type: 'Check-up'
    }
  ]

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Confirmed':
        return 'bg-green-100 text-green-800'
      case 'Pending':
        return 'bg-yellow-100 text-yellow-800'
      case 'Completed':
        return 'bg-blue-100 text-blue-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  return (
    <div className="px-4 sm:px-6 lg:px-8">
      <div className="sm:flex sm:items-center">
        <div className="sm:flex-auto">
          <h1 className="text-2xl font-semibold text-gray-900">Appointments</h1>
          <p className="mt-2 text-sm text-gray-700">
            View and manage your patient appointments.
          </p>
        </div>
        <div className="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
          <button className="btn-primary">
            Schedule New
          </button>
        </div>
      </div>

      {/* Appointments List */}
      <div className="mt-8">
        <div className="card">
          <div className="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
            <table className="min-w-full divide-y divide-gray-300">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Patient
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Date & Time
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Type
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {appointments.map((appointment) => (
                  <tr key={appointment.id}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">
                        {appointment.patientName}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">{appointment.date}</div>
                      <div className="text-sm text-gray-500">{appointment.time}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {appointment.type}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(appointment.status)}`}>
                        {appointment.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      {appointment.status === 'Confirmed' && (
                        <button className="text-primary-600 hover:text-primary-900 mr-3">
                          Start Session
                        </button>
                      )}
                      <button className="text-gray-600 hover:text-gray-900">
                        View Details
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="mt-8 grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div className="card text-center">
          <h3 className="text-lg font-medium text-gray-900">Today's Schedule</h3>
          <p className="mt-2 text-3xl font-bold text-primary-600">3</p>
          <p className="mt-1 text-sm text-gray-500">appointments</p>
        </div>
        <div className="card text-center">
          <h3 className="text-lg font-medium text-gray-900">Pending</h3>
          <p className="mt-2 text-3xl font-bold text-yellow-600">1</p>
          <p className="mt-1 text-sm text-gray-500">confirmation needed</p>
        </div>
        <div className="card text-center">
          <h3 className="text-lg font-medium text-gray-900">Completed</h3>
          <p className="mt-2 text-3xl font-bold text-green-600">1</p>
          <p className="mt-1 text-sm text-gray-500">today</p>
        </div>
      </div>
    </div>
  )
}

export default Appointments
