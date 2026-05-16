-- V2__patient_schema.sql

CREATE TABLE patients (
  id UUID PRIMARY KEY,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  date_of_birth DATE,
  gender VARCHAR(1),
  email VARCHAR(255) UNIQUE,
  phone VARCHAR(50),
  address TEXT,
  cin VARCHAR(50),
  insurance_provider VARCHAR(100),
  insurance_number VARCHAR(100),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_patients_name ON patients(last_name, first_name);
CREATE INDEX idx_patients_email ON patients(email);
