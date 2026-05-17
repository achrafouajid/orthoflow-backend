-- V3__stock_schema.sql

-- Sequences for PO, DN, SO and Treatment Invoice Numbers
CREATE SEQUENCE po_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE dn_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE so_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE treatment_inv_seq START WITH 1 INCREMENT BY 1;

-- Suppliers Table
CREATE TABLE suppliers (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  contact_name VARCHAR(255),
  email VARCHAR(255),
  phone VARCHAR(50),
  address TEXT,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Stock Items Table
CREATE TABLE stock_items (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  sku VARCHAR(100) UNIQUE NOT NULL,
  category VARCHAR(50) NOT NULL,
  unit VARCHAR(20) NOT NULL,
  unit_size NUMERIC(10,2) NOT NULL,
  unit_label VARCHAR(100) NOT NULL,
  purchase_price NUMERIC(12,2) NOT NULL,
  price_per_use NUMERIC(12,4) NOT NULL,
  current_stock NUMERIC(12,2) NOT NULL DEFAULT 0,
  minimum_stock NUMERIC(12,2) NOT NULL DEFAULT 0,
  svg_icon TEXT,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  supplier_id UUID REFERENCES suppliers(id) ON DELETE SET NULL,
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Treatments Table
CREATE TABLE treatments (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  code VARCHAR(50) UNIQUE NOT NULL,
  description TEXT,
  base_price NUMERIC(12,2) NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  category VARCHAR(50),
  duration_minutes INT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Treatment Consumables (Many-to-Many with attributes)
CREATE TABLE treatment_consumables (
  treatment_id UUID NOT NULL REFERENCES treatments(id) ON DELETE CASCADE,
  stock_item_id UUID NOT NULL REFERENCES stock_items(id) ON DELETE CASCADE,
  quantity_used NUMERIC(12,2) NOT NULL,
  is_optional BOOLEAN NOT NULL DEFAULT FALSE,
  notes TEXT,
  PRIMARY KEY (treatment_id, stock_item_id)
);

-- Purchase Orders Table
CREATE TABLE purchase_orders (
  id UUID PRIMARY KEY,
  po_number VARCHAR(32) UNIQUE NOT NULL,
  supplier_id UUID NOT NULL REFERENCES suppliers(id),
  status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
  order_date DATE NOT NULL,
  expected_delivery_date DATE,
  total_amount NUMERIC(12,2) NOT NULL,
  notes TEXT,
  created_by UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Purchase Order Lines Table
CREATE TABLE purchase_order_lines (
  id UUID PRIMARY KEY,
  purchase_order_id UUID NOT NULL REFERENCES purchase_orders(id) ON DELETE CASCADE,
  stock_item_id UUID NOT NULL REFERENCES stock_items(id),
  quantity_ordered NUMERIC(12,2) NOT NULL,
  unit_price NUMERIC(12,2) NOT NULL,
  total_price NUMERIC(12,2) NOT NULL,
  quantity_received NUMERIC(12,2) NOT NULL DEFAULT 0
);

-- Delivery Notes Table
CREATE TABLE delivery_notes (
  id UUID PRIMARY KEY,
  dn_number VARCHAR(32) UNIQUE NOT NULL,
  purchase_order_id UUID NOT NULL REFERENCES purchase_orders(id),
  supplier_id UUID NOT NULL REFERENCES suppliers(id),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  received_date DATE,
  received_by UUID,
  notes TEXT,
  stock_movements_generated BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Delivery Note Lines Table
CREATE TABLE delivery_note_lines (
  id UUID PRIMARY KEY,
  delivery_note_id UUID NOT NULL REFERENCES delivery_notes(id) ON DELETE CASCADE,
  po_line_id UUID NOT NULL REFERENCES purchase_order_lines(id),
  stock_item_id UUID NOT NULL REFERENCES stock_items(id),
  quantity_expected NUMERIC(12,2) NOT NULL,
  quantity_received NUMERIC(12,2) NOT NULL DEFAULT 0,
  batch_number VARCHAR(100),
  expiry_date DATE
);

-- Sales Orders Table
CREATE TABLE sales_orders (
  id UUID PRIMARY KEY,
  so_number VARCHAR(32) UNIQUE NOT NULL,
  patient_id UUID NOT NULL REFERENCES patients(id),
  status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
  total_amount NUMERIC(12,2) NOT NULL,
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Sales Order Lines Table
CREATE TABLE sales_order_lines (
  id UUID PRIMARY KEY,
  sales_order_id UUID NOT NULL REFERENCES sales_orders(id) ON DELETE CASCADE,
  stock_item_id UUID NOT NULL REFERENCES stock_items(id),
  quantity NUMERIC(12,2) NOT NULL,
  unit_price NUMERIC(12,2) NOT NULL,
  discount NUMERIC(5,2)
);

-- Treatment Invoices Table
CREATE TABLE treatment_invoices (
  id UUID PRIMARY KEY,
  invoice_number VARCHAR(32) UNIQUE NOT NULL,
  patient_id UUID NOT NULL REFERENCES patients(id),
  treatment_id UUID NOT NULL REFERENCES treatments(id),
  session_date DATE NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
  treatment_price NUMERIC(12,2) NOT NULL,
  consumables_cost NUMERIC(12,2) NOT NULL DEFAULT 0,
  subtotal NUMERIC(12,2) NOT NULL,
  discount_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
  total NUMERIC(12,2) NOT NULL,
  stock_movements_generated BOOLEAN NOT NULL DEFAULT FALSE,
  notes TEXT,
  created_by UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  finalized_at TIMESTAMPTZ
);

-- Treatment Invoice Consumables Table
CREATE TABLE treatment_invoice_consumables (
  id UUID PRIMARY KEY,
  treatment_invoice_id UUID NOT NULL REFERENCES treatment_invoices(id) ON DELETE CASCADE,
  stock_item_id UUID NOT NULL REFERENCES stock_items(id),
  default_quantity NUMERIC(12,2) NOT NULL,
  actual_quantity NUMERIC(12,2) NOT NULL,
  price_per_unit NUMERIC(12,4) NOT NULL,
  total_cost NUMERIC(12,2) NOT NULL,
  is_modified BOOLEAN NOT NULL DEFAULT FALSE
);

-- Invoice Discounts Table
CREATE TABLE invoice_discounts (
  id UUID PRIMARY KEY,
  treatment_invoice_id UUID NOT NULL REFERENCES treatment_invoices(id) ON DELETE CASCADE,
  type VARCHAR(20) NOT NULL,
  target VARCHAR(20) NOT NULL,
  target_id UUID,
  value NUMERIC(12,2) NOT NULL,
  reason VARCHAR(255)
);

-- Stock Movements Table
CREATE TABLE stock_movements (
  id UUID PRIMARY KEY,
  stock_item_id UUID NOT NULL REFERENCES stock_items(id),
  movement_type VARCHAR(20) NOT NULL,
  quantity NUMERIC(12,2) NOT NULL,
  quantity_before NUMERIC(12,2) NOT NULL,
  quantity_after NUMERIC(12,2) NOT NULL,
  source_type VARCHAR(32) NOT NULL,
  source_id UUID NOT NULL,
  source_reference VARCHAR(100) NOT NULL,
  notes TEXT,
  created_by UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_stock_movements_item ON stock_movements(stock_item_id);
CREATE INDEX idx_stock_items_sku ON stock_items(sku);
CREATE INDEX idx_treatments_code ON treatments(code);
CREATE INDEX idx_purchase_orders_supplier ON purchase_orders(supplier_id);
CREATE INDEX idx_delivery_notes_po ON delivery_notes(purchase_order_id);
CREATE INDEX idx_treatment_invoices_patient ON treatment_invoices(patient_id);
