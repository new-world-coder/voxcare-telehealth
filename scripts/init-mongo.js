// PulseCare MongoDB Initialization Script
// This script creates the database collections and initial data

// Switch to pulsecare database
db = db.getSiblingDB('pulsecare');

// Create audit_events collection
db.createCollection('audit_events');
db.audit_events.createIndex({ "timestamp": -1 });
db.audit_events.createIndex({ "actorId": 1 });
db.audit_events.createIndex({ "action": 1 });
db.audit_events.createIndex({ "targetType": 1 });

// Create telehealth_sessions collection
db.createCollection('telehealth_sessions');
db.telehealth_sessions.createIndex({ "appointmentId": 1 });
db.telehealth_sessions.createIndex({ "roomSlug": 1 });
db.telehealth_sessions.createIndex({ "createdAt": -1 });

// Create notification_logs collection
db.createCollection('notification_logs');
db.notification_logs.createIndex({ "recipientId": 1 });
db.notification_logs.createIndex({ "type": 1 });
db.notification_logs.createIndex({ "createdAt": -1 });

// Insert sample audit events
db.audit_events.insertMany([
    {
        actorId: "admin@demo.dev",
        action: "USER_LOGIN",
        targetType: "USER",
        targetId: "admin@demo.dev",
        timestamp: new Date(),
        ip: "127.0.0.1",
        userAgent: "Mozilla/5.0 (Demo Browser)",
        metadata: {
            sessionId: "demo-session-001",
            location: "localhost"
        }
    },
    {
        actorId: "provider1@demo.dev",
        action: "AVAILABILITY_CREATED",
        targetType: "AVAILABILITY",
        targetId: "avail-001",
        timestamp: new Date(),
        ip: "127.0.0.1",
        userAgent: "Mozilla/5.0 (Demo Browser)",
        metadata: {
            startTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000), // 7 days from now
            endTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000 + 60 * 60 * 1000), // +1 hour
            specialty: "General Practice"
        }
    },
    {
        actorId: "patient1@demo.dev",
        action: "APPOINTMENT_BOOKED",
        targetType: "APPOINTMENT",
        targetId: "apt-001",
        timestamp: new Date(),
        ip: "127.0.0.1",
        userAgent: "Mozilla/5.0 (Demo Browser)",
        metadata: {
            providerId: "provider1@demo.dev",
            appointmentDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000),
            duration: "60 minutes"
        }
    }
]);

// Insert sample telehealth sessions
db.telehealth_sessions.insertMany([
    {
        appointmentId: "apt-001",
        roomSlug: "demo-room-001",
        createdAt: new Date(),
        endedAt: null,
        notesNonPHI: "Initial consultation session",
        metadata: {
            providerSpecialty: "General Practice",
            sessionType: "Initial Consultation",
            maxParticipants: 2
        }
    }
]);

// Insert sample notification logs
db.notification_logs.insertMany([
    {
        recipientId: "patient1@demo.dev",
        type: "APPOINTMENT_REMINDER",
        subject: "Appointment Reminder",
        content: "Your appointment is scheduled for tomorrow at 9:00 AM",
        status: "SENT",
        createdAt: new Date(),
        metadata: {
            appointmentId: "apt-001",
            reminderTime: "24 hours before",
            channel: "email"
        }
    },
    {
        recipientId: "provider1@demo.dev",
        type: "NEW_APPOINTMENT",
        subject: "New Appointment Scheduled",
        content: "A new appointment has been scheduled with John Doe",
        status: "SENT",
        createdAt: new Date(),
        metadata: {
            appointmentId: "apt-001",
            patientName: "John Doe",
            appointmentTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)
        }
    }
]);

// Create users for MongoDB authentication (if needed)
db.createUser({
    user: "pulsecare",
    pwd: "pulsecare123",
    roles: [
        { role: "readWrite", db: "pulsecare" }
    ]
});

// Display created collections
print("Created collections:");
db.getCollectionNames().forEach(function(collectionName) {
    print("  - " + collectionName);
});

// Display sample data counts
print("\nSample data counts:");
print("  - Audit Events: " + db.audit_events.countDocuments());
print("  - Telehealth Sessions: " + db.telehealth_sessions.countDocuments());
print("  - Notification Logs: " + db.notification_logs.countDocuments());

// Display sample audit events
print("\nSample audit events:");
db.audit_events.find().limit(3).forEach(function(event) {
    print("  - " + event.action + " by " + event.actorId + " at " + event.timestamp);
});

print("\nMongoDB initialization completed successfully!");
