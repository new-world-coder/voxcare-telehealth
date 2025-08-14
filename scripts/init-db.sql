-- PulseCare Database Initialization Script
-- This script creates the database schema and initial data

-- Create database if it doesn't exist
-- Note: This needs to be run as a superuser or the database must exist
-- CREATE DATABASE pulsecare;

-- Connect to the pulsecare database
\c pulsecare;

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
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create providers table
CREATE TABLE IF NOT EXISTS providers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
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

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_patients_user_id ON patients(user_id);
CREATE INDEX IF NOT EXISTS idx_providers_user_id ON providers(user_id);
CREATE INDEX IF NOT EXISTS idx_availability_provider_id ON availability(provider_id);
CREATE INDEX IF NOT EXISTS idx_availability_start_time ON availability(start_time);
CREATE INDEX IF NOT EXISTS idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX IF NOT EXISTS idx_appointments_provider_id ON appointments(provider_id);
CREATE INDEX IF NOT EXISTS idx_appointments_start_time ON appointments(start_time);
CREATE INDEX IF NOT EXISTS idx_appointments_status ON appointments(status);

-- Create unique constraints
ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);

-- Insert demo users (passwords are BCrypt hashes of "Passw0rd!")
INSERT INTO users (email, password_hash, role, created_at, updated_at) VALUES
('admin@demo.dev', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('provider1@demo.dev', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'PROVIDER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('patient1@demo.dev', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'PATIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- Insert demo provider profile
INSERT INTO providers (user_id, specialty, timezone, created_at, updated_at) VALUES
((SELECT id FROM users WHERE email = 'provider1@demo.dev'), 'General Practice', 'America/New_York', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Insert demo patient profile
INSERT INTO patients (user_id, first_name, last_name, dob, phone, created_at, updated_at) VALUES
((SELECT id FROM users WHERE email = 'patient1@demo.dev'), 'John', 'Doe', '1990-01-01', '+1-555-0123', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
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

-- Grant permissions
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO pulsecare;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO pulsecare;

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
