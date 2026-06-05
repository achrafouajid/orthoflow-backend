-- V8__add_patient_treatment_schema.sql

CREATE TABLE patient_treatments (
  id UUID PRIMARY KEY,
  patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
  treatment_id UUID NOT NULL REFERENCES treatments(id),
  teeth VARCHAR(255) NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'PLANNED',
  progress INTEGER NOT NULL DEFAULT 0,
  notes TEXT,
  doctor_name VARCHAR(255),
  start_date DATE,
  end_date DATE,
  stock_movements_generated BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE patient_treatment_consumables (
  id UUID PRIMARY KEY,
  patient_treatment_id UUID NOT NULL REFERENCES patient_treatments(id) ON DELETE CASCADE,
  stock_item_id UUID NOT NULL REFERENCES stock_items(id),
  quantity_used NUMERIC(12,2) NOT NULL,
  price_per_unit NUMERIC(12,4) NOT NULL,
  notes TEXT
);

CREATE INDEX idx_patient_treatments_patient ON patient_treatments(patient_id);
CREATE INDEX idx_patient_treatments_treatment ON patient_treatments(treatment_id);
CREATE INDEX idx_patient_treatment_consumables_parent ON patient_treatment_consumables(patient_treatment_id);
