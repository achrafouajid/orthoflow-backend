-- V1__billing_schema.sql

-- Invoices table
CREATE TABLE invoices (
  id UUID PRIMARY KEY,
  practice_id UUID NOT NULL,
  patient_id UUID NOT NULL,
  treatment_plan_id UUID,
  invoice_number VARCHAR(32) UNIQUE NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
  issue_date DATE,
  due_date DATE,
  currency VARCHAR(3) NOT NULL DEFAULT 'EUR',
  subtotal NUMERIC(12,2),
  tax_amount NUMERIC(12,2),
  discount_amount NUMERIC(12,2),
  total NUMERIC(12,2),
  region_code VARCHAR(2) NOT NULL,
  insurance_scheme VARCHAR(64),
  notes TEXT,
  created_by UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_invoices_patient ON invoices(patient_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_practice ON invoices(practice_id);

-- Invoice line items
CREATE TABLE invoice_lines (
  id UUID PRIMARY KEY,
  invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
  act_code VARCHAR(32),
  label VARCHAR(255) NOT NULL,
  quantity NUMERIC(6,2) NOT NULL DEFAULT 1,
  unit_price NUMERIC(12,2) NOT NULL,
  discount_pct NUMERIC(5,2) NOT NULL DEFAULT 0,
  line_total NUMERIC(12,2) NOT NULL,
  sort_order INT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_invoice_lines_invoice ON invoice_lines(invoice_id);

-- Payments
CREATE TABLE payments (
  id UUID PRIMARY KEY,
  invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE RESTRICT,
  amount NUMERIC(12,2) NOT NULL,
  method VARCHAR(32) NOT NULL,
  payment_date DATE NOT NULL,
  reference VARCHAR(128),
  notes TEXT,
  recorded_by UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT chk_payment_amount CHECK (amount > 0)
);

CREATE INDEX idx_payments_invoice ON payments(invoice_id);

-- Audit log (for compliance)
CREATE TABLE billing_audit_log (
  id UUID PRIMARY KEY,
  invoice_id UUID REFERENCES invoices(id) ON DELETE SET NULL,
  action VARCHAR(32) NOT NULL,
  actor_id UUID NOT NULL,
  details JSONB,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_invoice ON billing_audit_log(invoice_id);
CREATE INDEX idx_audit_actor ON billing_audit_log(actor_id);

-- Sequence for invoice numbering (atomic generation)
CREATE SEQUENCE invoices_seq START WITH 1 INCREMENT BY 1;
