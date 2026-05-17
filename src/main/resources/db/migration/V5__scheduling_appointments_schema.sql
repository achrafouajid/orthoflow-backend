-- V5__scheduling_appointments_schema.sql

-- 1. Create Appointments Table
CREATE TABLE appointments (
  id UUID PRIMARY KEY,
  patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
  date_time TIMESTAMPTZ NOT NULL,
  type VARCHAR(100) NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
  notes TEXT,
  appliance_step INTEGER,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 2. Create Indexes for Scheduling Queries
CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_datetime ON appointments(date_time);

-- 3. Seed Demo Patients
INSERT INTO patients (id, first_name, last_name, date_of_birth, gender, email, phone, address, cin, insurance_provider, insurance_number, status, created_at, updated_at)
VALUES 
(
  'b1e12345-0001-4bc6-849c-f9e4034876b0',
  'Achraf',
  'Ouajid',
  '1995-04-12',
  'M',
  'achraf@orthoflow.ma',
  '+212 661-123456',
  'Gauthier, Casablanca, Morocco',
  'BE123456',
  'CNOPS',
  'INS-99882233',
  'ACTIVE',
  NOW() - INTERVAL '10 days',
  NOW() - INTERVAL '10 days'
),
(
  'b1e12345-0002-4bc6-849c-f9e4034876b0',
  'Sarah',
  'Benziane',
  '1998-08-22',
  'F',
  'sarah@orthoflow.ma',
  '+212 662-987654',
  'Agdal, Rabat, Morocco',
  'AB987654',
  'CNSS',
  'INS-77665544',
  'ACTIVE',
  NOW() - INTERVAL '5 days',
  NOW() - INTERVAL '5 days'
),
(
  'b1e12345-0003-4bc6-849c-f9e4034876b0',
  'Karim',
  'Tazi',
  '2002-11-05',
  'M',
  'karim@orthoflow.ma',
  '+212 663-555666',
  'Hivernage, Marrakech, Morocco',
  'MX555666',
  'AXA Insurance',
  'INS-11223344',
  'ACTIVE',
  NOW() - INTERVAL '2 days',
  NOW() - INTERVAL '2 days'
);

-- 4. Seed Upcoming and Past Appointments
-- Note: We schedule today's appointments relative to today's date dynamically
INSERT INTO appointments (id, patient_id, date_time, type, status, notes, appliance_step, created_at, updated_at)
VALUES
(
  'c1e12345-0001-4bc6-849c-f9e4034876b0',
  'b1e12345-0001-4bc6-849c-f9e4034876b0',
  CURRENT_DATE + TIME '10:00:00',
  'Full Bracket Bonding (Metal)',
  'SCHEDULED',
  'Initial bonding session for upper and lower arches.',
  1,
  NOW(),
  NOW()
),
(
  'c1e12345-0002-4bc6-849c-f9e4034876b0',
  'b1e12345-0002-4bc6-849c-f9e4034876b0',
  CURRENT_DATE + TIME '14:30:00',
  'Arch Wire Placement / Change',
  'SCHEDULED',
  'Standard appointment to switch wire to rectangular SS 19x25.',
  3,
  NOW(),
  NOW()
),
(
  'c1e12345-0003-4bc6-849c-f9e4034876b0',
  'b1e12345-0003-4bc6-849c-f9e4034876b0',
  (CURRENT_DATE + INTERVAL '1 day') + TIME '11:30:00',
  'Activation / Adjustment Visit',
  'SCHEDULED',
  'Routine rubber band tension checks and general progress inspection.',
  5,
  NOW(),
  NOW()
);
