-- VoxCare Database Initialization Script
-- This script creates the database schema and initial data

-- Create database if it doesn't exist
-- Note: This needs to be run as a superuser or the database must exist
-- CREATE DATABASE voxcare;

-- Connect to the voxcare database
\c voxcare;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create patients table
CREATE TABLE IF NOT EXISTS patients (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    dob DATE,
    phone VARCHAR(20),
    phone_normalized VARCHAR(32),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create providers table
CREATE TABLE IF NOT EXISTS providers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    specialty VARCHAR(100) NOT NULL,
    timezone VARCHAR(50) NOT NULL DEFAULT 'UTC',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create availability table
CREATE TABLE IF NOT EXISTS availability (
    id BIGSERIAL PRIMARY KEY,
    provider_id BIGINT NOT NULL REFERENCES providers(id) ON DELETE CASCADE,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    provider_id BIGINT NOT NULL REFERENCES providers(id) ON DELETE CASCADE,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    telehealth_link VARCHAR(500),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Voice AI call records (GetDial integration)
CREATE TABLE IF NOT EXISTS voice_calls (
    id BIGSERIAL PRIMARY KEY,
    external_id VARCHAR(128) UNIQUE,
    provider VARCHAR(32) NOT NULL DEFAULT 'dial',
    purpose VARCHAR(32) NOT NULL,
    patient_id BIGINT,
    appointment_id BIGINT,
    provider_id BIGINT,
    to_number VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    outcome VARCHAR(32),
    duration_seconds INTEGER,
    transcript TEXT,
    retry_count INTEGER NOT NULL DEFAULT 0,
    sms_fallback_sent BOOLEAN NOT NULL DEFAULT FALSE,
    sms_external_id VARCHAR(128),
    outbound_instruction TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS voice_call_events (
    id BIGSERIAL PRIMARY KEY,
    voice_call_id BIGINT NOT NULL REFERENCES voice_calls(id) ON DELETE CASCADE,
    event_type VARCHAR(64) NOT NULL,
    payload_json TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- EstateCraft-compatible voice rules + scheduled retries
CREATE TABLE IF NOT EXISTS voice_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    min_qualification_score INTEGER NOT NULL DEFAULT 70,
    max_retries INTEGER NOT NULL DEFAULT 3,
    retry_delay_minutes INTEGER NOT NULL DEFAULT 30,
    sms_fallback_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    sms_fallback_template TEXT,
    outbound_instruction TEXT NOT NULL,
    priority INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS scheduled_follow_ups (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT,
    scheduled_at TIMESTAMP NOT NULL,
    type VARCHAR(64) NOT NULL DEFAULT 'voice_retry',
    notes TEXT,
    parent_call_id BIGINT,
    processed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_patients_user_id ON patients(user_id);
CREATE INDEX IF NOT EXISTS idx_patients_phone_normalized ON patients(phone_normalized);
CREATE INDEX IF NOT EXISTS idx_providers_user_id ON providers(user_id);
CREATE INDEX IF NOT EXISTS idx_availability_provider_id ON availability(provider_id);
CREATE INDEX IF NOT EXISTS idx_availability_start_time ON availability(start_time);
CREATE INDEX IF NOT EXISTS idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX IF NOT EXISTS idx_appointments_provider_id ON appointments(provider_id);
CREATE INDEX IF NOT EXISTS idx_appointments_start_time ON appointments(start_time);
CREATE INDEX IF NOT EXISTS idx_appointments_status ON appointments(status);
CREATE INDEX IF NOT EXISTS idx_voice_calls_external_id ON voice_calls(external_id);
CREATE INDEX IF NOT EXISTS idx_voice_calls_patient_id ON voice_calls(patient_id);
CREATE INDEX IF NOT EXISTS idx_voice_calls_status ON voice_calls(status);
CREATE INDEX IF NOT EXISTS idx_voice_call_events_call_id ON voice_call_events(voice_call_id);

-- Create unique constraints
ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);

-- Insert demo users (passwords are BCrypt hashes of "Passw0rd!")
INSERT INTO users (email, password_hash, role, created_at, updated_at) VALUES
('admin@demo.dev', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('provider1@demo.dev', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'PROVIDER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('patient1@demo.dev', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'PATIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- Insert demo provider profile
INSERT INTO providers (user_id, first_name, last_name, specialty, timezone, created_at, updated_at) VALUES
((SELECT id FROM users WHERE email = 'provider1@demo.dev'), 'Ada', 'Smith', 'General Practice', 'America/New_York', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Insert demo patient profile
INSERT INTO patients (user_id, first_name, last_name, dob, phone, phone_normalized, created_at, updated_at) VALUES
((SELECT id FROM users WHERE email = 'patient1@demo.dev'), 'John', 'Doe', '1990-01-01', '+15550123', '15550123', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Insert demo availability slots (next week)
INSERT INTO availability (provider_id, start_time, end_time, status, created_at, updated_at) VALUES
((SELECT id FROM providers WHERE user_id = (SELECT id FROM users WHERE email = 'provider1@demo.dev')), 
 CURRENT_DATE + INTERVAL '7 days' + INTERVAL '9 hours', 
 CURRENT_DATE + INTERVAL '7 days' + INTERVAL '10 hours', 
 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM providers WHERE user_id = (SELECT id FROM users WHERE email = 'provider1@demo.dev')), 
 CURRENT_DATE + INTERVAL '7 days' + INTERVAL '10 hours', 
 CURRENT_DATE + INTERVAL '7 days' + INTERVAL '11 hours', 
 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM providers WHERE user_id = (SELECT id FROM users WHERE email = 'provider1@demo.dev')), 
 CURRENT_DATE + INTERVAL '7 days' + INTERVAL '14 hours', 
 CURRENT_DATE + INTERVAL '7 days' + INTERVAL '15 hours', 
 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- EstateCraft-style Dial voice rules (templates use {{leadName}} / {{patientName}})
INSERT INTO voice_rules (
    name, enabled, min_qualification_score, max_retries, retry_delay_minutes,
    sms_fallback_enabled, sms_fallback_template, outbound_instruction, priority
) VALUES
(
    'Appointment Booking Outreach',
    TRUE, 0, 3, 30, TRUE,
    'Hi {{leadName}}, this is VoxCare. We tried calling to help book your telehealth visit. Reply YES or call us back to schedule!',
    'You are a friendly telehealth scheduling assistant for VoxCare. Greet {{leadName}} warmly, offer available appointment slots, and book the time they confirm. Be concise and HIPAA-aware: do not discuss diagnoses.',
    10
),
(
    'Appointment Reminder Follow-up',
    TRUE, 0, 2, 60, TRUE,
    'Hi {{leadName}}, VoxCare reminder: you have an upcoming telehealth appointment. Reply YES to confirm or call to reschedule.',
    'You are calling on behalf of VoxCare. Remind {{leadName}} about their upcoming telehealth appointment and offer to reschedule if needed. Do not discuss clinical details.',
    5
)
ON CONFLICT DO NOTHING;

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_patients_updated_at BEFORE UPDATE ON patients FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_providers_updated_at BEFORE UPDATE ON providers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_availability_updated_at BEFORE UPDATE ON availability FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_appointments_updated_at BEFORE UPDATE ON appointments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_voice_calls_updated_at BEFORE UPDATE ON voice_calls FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Grant permissions
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO voxcare;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO voxcare;

-- Display created tables
\dt

-- Display demo data
SELECT 'Users:' as info;
SELECT id, email, role, created_at FROM users;

SELECT 'Providers:' as info;
SELECT p.id, u.email, p.specialty, p.timezone FROM providers p JOIN users u ON p.user_id = u.id;

SELECT 'Patients:' as info;
SELECT p.id, u.email, p.first_name, p.last_name FROM patients p JOIN users u ON p.user_id = u.id;

SELECT 'Availability:' as info;
SELECT a.id, u.email, a.start_time, a.end_time, a.status FROM availability a JOIN providers p ON a.provider_id = p.id JOIN users u ON p.user_id = u.id;
