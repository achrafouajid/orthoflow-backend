-- V14__vendor_invoices_and_count_sessions.sql
-- Adds Vendor Invoices (BR03 — GRNI clearing / vendor liability) and
-- Physical Count Sessions (BR08 — inventory reconciliation).

CREATE SEQUENCE IF NOT EXISTS vendor_invoice_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS count_session_seq START WITH 1 INCREMENT BY 1;

-- Vendor Invoices Table
CREATE TABLE vendor_invoices (
  id UUID PRIMARY KEY,
  vendor_invoice_number VARCHAR(32) UNIQUE NOT NULL,
  delivery_note_id UUID NOT NULL REFERENCES delivery_notes(id),
  supplier_id UUID NOT NULL REFERENCES suppliers(id),
  status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'VALIDATED', 'CANCELLED')),
  invoice_date DATE NOT NULL,
  invoice_amount NUMERIC(12,2) NOT NULL,
  payment_terms VARCHAR(100),
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  validated_at TIMESTAMPTZ,
  created_by UUID REFERENCES users(id),
  validated_by UUID REFERENCES users(id)
);

-- Vendor Invoice Lines Table
CREATE TABLE vendor_invoice_lines (
  id UUID PRIMARY KEY,
  vendor_invoice_id UUID NOT NULL REFERENCES vendor_invoices(id) ON DELETE CASCADE,
  stock_item_id UUID NOT NULL REFERENCES stock_items(id),
  quantity_invoiced NUMERIC(12,2) NOT NULL,
  unit_price NUMERIC(12,2) NOT NULL,
  tax_rate NUMERIC(5,2),
  line_total NUMERIC(12,2) NOT NULL
);

CREATE INDEX idx_vendor_invoices_delivery_note ON vendor_invoices(delivery_note_id);
CREATE INDEX idx_vendor_invoice_lines_invoice ON vendor_invoice_lines(vendor_invoice_id);

-- Physical Count Sessions Table
CREATE TABLE count_sessions (
  id UUID PRIMARY KEY,
  session_number VARCHAR(32) UNIQUE NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'IN_PROGRESS', 'VALIDATED', 'CANCELLED')),
  snapshot_date TIMESTAMPTZ,
  count_date DATE,
  validated_date TIMESTAMPTZ,
  notes TEXT,
  total_quantity_variance NUMERIC(12,2) NOT NULL DEFAULT 0,
  total_cost_variance NUMERIC(12,2) NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by UUID REFERENCES users(id),
  validated_by UUID REFERENCES users(id)
);

-- Physical Count Session Lines Table
CREATE TABLE count_session_lines (
  id UUID PRIMARY KEY,
  count_session_id UUID NOT NULL REFERENCES count_sessions(id) ON DELETE CASCADE,
  stock_item_id UUID NOT NULL REFERENCES stock_items(id),
  theoretical_quantity NUMERIC(12,2) NOT NULL,
  physical_quantity NUMERIC(12,2),
  quantity_variance NUMERIC(12,2),
  cost_variance NUMERIC(12,2),
  notes TEXT,
  UNIQUE (count_session_id, stock_item_id)
);

CREATE INDEX idx_count_session_lines_session ON count_session_lines(count_session_id);
