-- V4__seed_stock_data.sql

-- 1. Insert Default Supplier
INSERT INTO suppliers (id, name, contact_name, email, phone, address, is_active)
VALUES (
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'OrthoDental Supply Co.',
  'Jean Dupont',
  'contact@orthodental.ma',
  '+212 522-123456',
  '123 Route d''El Jadida, Casablanca, Morocco',
  true
);

-- 2. Insert Stock Items
-- We generate fixed UUIDs for key items to reference them in treatment consumables

-- Item 1: Gloves — Nitrile M
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0001-4bc6-849c-f9e4034876b0',
  'Gloves — Nitrile M (box 100)',
  'GLV-NIT-M',
  'hygiene',
  'pair',
  100.00,
  'box of 100 pairs',
  80.00,
  0.8000,
  500.00, -- 5 boxes
  100.00,
  'icon-gloves',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Standard nitrile gloves, medium size'
);

-- Item 2: Surgical Mask
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0002-4bc6-849c-f9e4034876b0',
  'Surgical Mask (box 50)',
  'MSK-SRG',
  'hygiene',
  'unit',
  50.00,
  'box of 50',
  50.00,
  1.0000,
  250.00, -- 5 boxes
  50.00,
  'icon-mask',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Triple layer earloop masks'
);

-- Item 3: Bib / Patient Napkin
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0003-4bc6-849c-f9e4034876b0',
  'Bib / Patient Napkin (box 500)',
  'BIB-PTN',
  'hygiene',
  'unit',
  500.00,
  'box of 500',
  150.00,
  0.3000,
  1000.00, -- 2 boxes
  200.00,
  'icon-bib',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Disposable paper bibs'
);

-- Item 4: Cotton Rolls
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0004-4bc6-849c-f9e4034876b0',
  'Cotton Rolls (bag 2000)',
  'CTN-ROL',
  'consumables',
  'unit',
  2000.00,
  'bag of 2000',
  100.00,
  0.0500,
  4000.00, -- 2 bags
  1000.00,
  'icon-cotton',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Absorbent cotton rolls size 2'
);

-- Item 5: Cheek Retractor
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0005-4bc6-849c-f9e4034876b0',
  'Cheek Retractor',
  'INS-RET',
  'instruments',
  'unit',
  1.00,
  'per unit',
  40.00,
  40.0000,
  30.00,
  5.00,
  'icon-retractor',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Autoclavable plastic retractor'
);

-- Item 6: Alginate Impression
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0006-4bc6-849c-f9e4034876b0',
  'Alginate Impression (500g bag)',
  'CSM-ALG',
  'consumables',
  'g',
  500.00,
  '500g bag',
  120.00,
  0.2400,
  2000.00, -- 4 bags
  500.00,
  'icon-impression',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Fast set orthodontic alginate'
);

-- Item 7: Separator Elastics
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0007-4bc6-849c-f9e4034876b0',
  'Separator Elastics (bag 1000)',
  'ORT-SEP',
  'orthodontic_parts',
  'unit',
  1000.00,
  'bag of 1000',
  150.00,
  0.1500,
  2000.00,
  200.00,
  'icon-separator',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Radiopaque blue dental separators'
);

-- Item 8: Dental Floss
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0008-4bc6-849c-f9e4034876b0',
  'Dental Floss (100m roll)',
  'CSM-FLS',
  'consumables',
  'm',
  100.00,
  'per roll',
  35.00,
  0.3500,
  500.00,
  50.00,
  'icon-floss',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Waxed dental floss'
);

-- Item 9: Molar Band — Upper
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0009-4bc6-849c-f9e4034876b0',
  'Molar Band — Upper (set 4)',
  'ORT-MBD',
  'orthodontic_parts',
  'unit',
  4.00,
  'set of 4',
  200.00,
  50.0000,
  40.00,
  8.00,
  'icon-molar-band',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Pre-formed molar bands'
);

-- Item 10: Dental Adhesive Bonding
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0010-4bc6-849c-f9e4034876b0',
  'Dental Adhesive Bonding (7ml)',
  'ORT-ADH',
  'orthodontic_parts',
  'ml',
  7.00,
  '7ml bottle',
  450.00,
  64.2857,
  21.00,
  7.00,
  'icon-adhesive',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Light cure orthodontic primer'
);

-- Item 11: Gauze Pads
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0011-4bc6-849c-f9e4034876b0',
  'Gauze Pads (pack 100)',
  'HYG-GAZ',
  'hygiene',
  'unit',
  100.00,
  'pack of 100',
  40.00,
  0.4000,
  500.00,
  100.00,
  'icon-gauze',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Sterile 2x2 gauze sponges'
);

-- Item 12: Saliva Ejector
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0012-4bc6-849c-f9e4034876b0',
  'Saliva Ejector (box 100)',
  'CSM-SLV',
  'consumables',
  'unit',
  100.00,
  'box of 100',
  80.00,
  0.8000,
  300.00,
  50.00,
  'icon-ejector',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Flexible disposable suction tips'
);

-- Item 13: Articulating Paper
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0013-4bc6-849c-f9e4034876b0',
  'Articulating Paper (book 12)',
  'CSM-ART',
  'consumables',
  'unit',
  12.00,
  'booklet',
  60.00,
  5.0000,
  24.00,
  6.00,
  'icon-paper',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Bausch blue articulating paper'
);

-- Item 14: Anesthesia — Lidocaine 2%
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0014-4bc6-849c-f9e4034876b0',
  'Anesthesia — Lidocaine 2%',
  'ANE-LID-1000',
  'anesthesia',
  'ml',
  1000.00,
  '1000ml bottle',
  120.00,
  0.1200,
  3000.00, -- 3 bottles
  500.00,
  'icon-anesthesia-vial',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Lidocaine HCl 2% injectable'
);

-- Item 15: Metal Bracket — MBT 022
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0015-4bc6-849c-f9e4034876b0',
  'Metal Bracket — MBT 022',
  'ORT-BRK-MTL',
  'orthodontic_parts',
  'unit',
  1.00,
  'per bracket',
  15.00,
  15.0000,
  500.00,
  100.00,
  'icon-bracket',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Stainless steel brackets'
);

-- Item 16: Ceramic Bracket — MBT 022
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0016-4bc6-849c-f9e4034876b0',
  'Ceramic Bracket — MBT 022',
  'ORT-BRK-CRM',
  'orthodontic_parts',
  'unit',
  1.00,
  'per bracket',
  45.00,
  45.0000,
  200.00,
  50.00,
  'icon-bracket-ceramic',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Esthetic ceramic brackets'
);

-- Item 17: Etching Gel 37%
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0017-4bc6-849c-f9e4034876b0',
  'Etching Gel 37% (3ml)',
  'CSM-ETC-GEL',
  'consumables',
  'ml',
  3.00,
  'per syringe',
  60.00,
  20.0000,
  15.00, -- 5 syringes
  3.00,
  'icon-gel',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Phosphoric acid etchant'
);

-- Item 18: Composite Resin
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0018-4bc6-849c-f9e4034876b0',
  'Composite Resin (4g syringe)',
  'CSM-CMP-RSN',
  'consumables',
  'g',
  4.00,
  'per syringe',
  250.00,
  62.5000,
  20.00, -- 5 syringes
  4.00,
  'icon-composite',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Orthodontic adhesive paste'
);

-- Item 19: Elastic Ligature — Assorted
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0019-4bc6-849c-f9e4034876b0',
  'Elastic Ligature — Assorted (bag 1000)',
  'ORT-LGT-AST',
  'orthodontic_parts',
  'unit',
  1000.00,
  'bag of 1000',
  80.00,
  0.0800,
  5000.00,
  1000.00,
  'icon-ligature',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Assorted colored ligature ties'
);

-- Item 20: Arch Wire — NiTi Round 014
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0020-4bc6-849c-f9e4034876b0',
  'Arch Wire — NiTi Round 014',
  'ORT-WIR-NIT-014',
  'orthodontic_parts',
  'unit',
  1.00,
  'per wire',
  25.00,
  25.0000,
  100.00,
  20.00,
  'icon-archwire',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Superelastic nickel titanium archwire'
);

-- Item 21: Arch Wire — SS Rectangular 019x025
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0021-4bc6-849c-f9e4034876b0',
  'Arch Wire — SS Rectangular 019x025',
  'ORT-WIR-SS-1925',
  'orthodontic_parts',
  'unit',
  1.00,
  'per wire',
  30.00,
  30.0000,
  100.00,
  20.00,
  'icon-archwire',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Stainless steel rectangular archwire'
);

-- Item 22: Needle — Dental Aspirating
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0022-4bc6-849c-f9e4034876b0',
  'Needle — Dental Aspirating',
  'CSM-NDL-ASP',
  'consumables',
  'unit',
  1.00,
  'per unit',
  2.50,
  2.5000,
  100.00,
  20.00,
  'icon-needle',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Disposable aspirating needles 27G'
);

-- Item 23: Syringe — 5ml Disposable
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0023-4bc6-849c-f9e4034876b0',
  'Syringe — 5ml Disposable',
  'CSM-SYR-5ML',
  'consumables',
  'unit',
  1.00,
  'per unit',
  1.50,
  1.5000,
  150.00,
  30.00,
  'icon-syringe',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Luer lock disposable syringe'
);

-- Item 24: Dental Tooth Gum / Gingival
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0024-4bc6-849c-f9e4034876b0',
  'Dental Tooth Gum / Gingival (50ml)',
  'CSM-GEL-GGV',
  'consumables',
  'ml',
  50.00,
  '50ml tube',
  180.00,
  3.6000,
  250.00, -- 5 tubes
  50.00,
  'icon-dental-gel',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Topical anesthetic gel'
);

-- Item 25: Fluoride Gel
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0025-4bc6-849c-f9e4034876b0',
  'Fluoride Gel (60ml tube)',
  'CSM-FLU-GEL',
  'consumables',
  'ml',
  60.00,
  '60ml tube',
  120.00,
  2.0000,
  300.00, -- 5 tubes
  60.00,
  'icon-fluoride',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Prophylaxis fluoride gel'
);

-- Item 26: Bite Registration Silicone
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0026-4bc6-849c-f9e4034876b0',
  'Bite Registration Silicone (50ml)',
  'CSM-BIT-SIL',
  'consumables',
  'ml',
  50.00,
  '50ml cartridge',
  250.00,
  5.0000,
  150.00, -- 3 cartridges
  50.00,
  'icon-impression',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'A-Silicone bite registration material'
);

-- Item 27: Retainer Wire — SS 0.8mm
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0027-4bc6-849c-f9e4034876b0',
  'Retainer Wire — SS 0.8mm (30cm)',
  'ORT-WIR-RET-0.8',
  'orthodontic_parts',
  'cm',
  30.00,
  'per segment',
  60.00,
  2.0000,
  150.00, -- 5 segments
  30.00,
  'icon-wire',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Stainless steel straight wire for retainers'
);

-- Item 28: Suction Tip
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0028-4bc6-849c-f9e4034876b0',
  'Suction Tip (box 50)',
  'CSM-SUC-TIP',
  'consumables',
  'unit',
  50.00,
  'box of 50',
  100.00,
  2.0000,
  150.00, -- 3 boxes
  50.00,
  'icon-suction',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'High volume evacuation suction tips'
);

-- Item 29: Power Chain — Closed
INSERT INTO stock_items (id, name, sku, category, unit, unit_size, unit_label, purchase_price, price_per_use, current_stock, minimum_stock, svg_icon, is_active, supplier_id, notes)
VALUES (
  'a1e12345-0029-4bc6-849c-f9e4034876b0',
  'Power Chain — Closed (1m)',
  'ORT-PWR-CHN',
  'orthodontic_parts',
  'cm',
  100.00,
  '1m roll',
  120.00,
  1.2000,
  300.00, -- 3 rolls
  50.00,
  'icon-powerchain',
  true,
  'd80a1c1d-1234-4bc6-849c-f9e4034876b0',
  'Continuous elastic chain'
);

-- 3. Insert Treatments
-- ORTHO-001: Initial Consultation
INSERT INTO treatments (id, name, code, description, base_price, is_active, category, duration_minutes)
VALUES (
  'c1e12345-0001-4bc6-849c-f9e4034876b0',
  'Initial Consultation & Exam',
  'ORTHO-001',
  'First diagnostic examination and orthodontic consultation.',
  300.00,
  true,
  'Consultation',
  30
);

-- ORTHO-002: Orthodontic Records
INSERT INTO treatments (id, name, code, description, base_price, is_active, category, duration_minutes)
VALUES (
  'c1e12345-0002-4bc6-849c-f9e4034876b0',
  'Orthodontic Records (X-rays + Photos)',
  'ORTHO-002',
  'Full diagnostic records including alginate models, clinical photos and panoramic analysis.',
  500.00,
  true,
  'Diagnostic',
  45
);

-- ORTHO-003: Separator Placement
INSERT INTO treatments (id, name, code, description, base_price, is_active, category, duration_minutes)
VALUES (
  'c1e12345-0003-4bc6-849c-f9e4034876b0',
  'Separator Placement',
  'ORTHO-003',
  'Placement of orthodontic separators to prepare for band fitting.',
  200.00,
  true,
  'Preparation',
  20
);

-- ORTHO-004: Molar Band Fitting
INSERT INTO treatments (id, name, code, description, base_price, is_active, category, duration_minutes)
VALUES (
  'c1e12345-0004-4bc6-849c-f9e4034876b0',
  'Molar Band Fitting & Cementation',
  'ORTHO-004',
  'Sizing, fitting and glass ionomer cementation of orthodontic molar bands.',
  800.00,
  true,
  'Bonding',
  45
);

-- ORTHO-005: Full Bracket Bonding Metal
INSERT INTO treatments (id, name, code, description, base_price, is_active, category, duration_minutes)
VALUES (
  'c1e12345-0005-4bc6-849c-f9e4034876b0',
  'Full Bracket Bonding (Metal)',
  'ORTHO-005',
  'Direct bonding of full metal bracket appliance (MBT 022) upper and lower arches.',
  4500.00,
  true,
  'Bonding',
  90
);

-- ORTHO-006: Full Bracket Bonding Ceramic
INSERT INTO treatments (id, name, code, description, base_price, is_active, category, duration_minutes)
VALUES (
  'c1e12345-0006-4bc6-849c-f9e4034876b0',
  'Full Bracket Bonding (Ceramic)',
  'ORTHO-006',
  'Direct bonding of premium esthetic ceramic bracket appliance upper and lower arches.',
  7000.00,
  true,
  'Bonding',
  90
);

-- ORTHO-007: Arch Wire Placement
INSERT INTO treatments (id, name, code, description, base_price, is_active, category, duration_minutes)
VALUES (
  'c1e12345-0007-4bc6-849c-f9e4034876b0',
  'Arch Wire Placement / Change',
  'ORTHO-007',
  'Insertion or change of active orthodontic archwire.',
  400.00,
  true,
  'Maintenance',
  30
);

-- ORTHO-008: Activation / Adjustment
INSERT INTO treatments (id, name, code, description, base_price, is_active, category, duration_minutes)
VALUES (
  'c1e12345-0008-4bc6-849c-f9e4034876b0',
  'Activation / Adjustment Visit',
  'ORTHO-008',
  'Regular activation visit with ligature replacement and elastic tracking.',
  250.00,
  true,
  'Maintenance',
  20
);

-- ORTHO-009: Local Anesthesia
INSERT INTO treatments (id, name, code, description, base_price, is_active, category, duration_minutes)
VALUES (
  'c1e12345-0009-4bc6-849c-f9e4034876b0',
  'Local Anesthesia Administration',
  'ORTHO-009',
  'Infiltration or block anesthesia injection.',
  150.00,
  true,
  'Procedure',
  10
);

-- ORTHO-010: Bracket Removal
INSERT INTO treatments (id, name, code, description, base_price, is_active, category, duration_minutes)
VALUES (
  'c1e12345-0010-4bc6-849c-f9e4034876b0',
  'Bracket Removal (Debonding)',
  'ORTHO-010',
  'Debonding of appliance, composite cleanup, tooth polishing and topical fluoride.',
  1200.00,
  true,
  'Debonding',
  60
);

-- ORTHO-011: Retainer Fitting
INSERT INTO treatments (id, name, code, description, base_price, is_active, category, duration_minutes)
VALUES (
  'c1e12345-0011-4bc6-849c-f9e4034876b0',
  'Retainer Fitting (Removable)',
  'ORTHO-011',
  'Impressions and fitting of removable orthodontic retainer (Hawley or vacuum-formed).',
  1500.00,
  true,
  'Retention',
  30
);

-- ORTHO-012: Fixed Retainer Placement
INSERT INTO treatments (id, name, code, description, base_price, is_active, category, duration_minutes)
VALUES (
  'c1e12345-0012-4bc6-849c-f9e4034876b0',
  'Fixed Retainer Placement (Bonded)',
  'ORTHO-012',
  'Bonding of lingual fixed retainer wire upper or lower.',
  1800.00,
  true,
  'Retention',
  45
);

-- ORTHO-013: Dental Cleaning
INSERT INTO treatments (id, name, code, description, base_price, is_active, category, duration_minutes)
VALUES (
  'c1e12345-0013-4bc6-849c-f9e4034876b0',
  'Dental Cleaning / Prophylaxis',
  'ORTHO-013',
  'Scaling, plaque removal, and prophylaxis cleaning for orthodontic patients.',
  350.00,
  true,
  'Hygiene',
  40
);


-- 4. Insert Treatment Consumables (Default configuration)

-- ORTHO-001 (Initial Consultation): Nitrile Glove, Mask, Napkin, Cotton Roll, Cheek Retractor
INSERT INTO treatment_consumables (treatment_id, stock_item_id, quantity_used, is_optional) VALUES
('c1e12345-0001-4bc6-849c-f9e4034876b0', 'a1e12345-0001-4bc6-849c-f9e4034876b0', 1, false), -- Glove Nitrile
('c1e12345-0001-4bc6-849c-f9e4034876b0', 'a1e12345-0002-4bc6-849c-f9e4034876b0', 1, false), -- Mask
('c1e12345-0001-4bc6-849c-f9e4034876b0', 'a1e12345-0003-4bc6-849c-f9e4034876b0', 1, false), -- Napkin
('c1e12345-0001-4bc6-849c-f9e4034876b0', 'a1e12345-0004-4bc6-849c-f9e4034876b0', 4, false), -- Cotton Roll
('c1e12345-0001-4bc6-849c-f9e4034876b0', 'a1e12345-0005-4bc6-849c-f9e4034876b0', 1, false); -- Retractor

-- ORTHO-002 (Orthodontic Records)
INSERT INTO treatment_consumables (treatment_id, stock_item_id, quantity_used, is_optional) VALUES
('c1e12345-0002-4bc6-849c-f9e4034876b0', 'a1e12345-0001-4bc6-849c-f9e4034876b0', 1, false), -- Glove Nitrile
('c1e12345-0002-4bc6-849c-f9e4034876b0', 'a1e12345-0002-4bc6-849c-f9e4034876b0', 1, false), -- Mask
('c1e12345-0002-4bc6-849c-f9e4034876b0', 'a1e12345-0006-4bc6-849c-f9e4034876b0', 30, false), -- Alginate (30g)
('c1e12345-0002-4bc6-849c-f9e4034876b0', 'a1e12345-0003-4bc6-849c-f9e4034876b0', 2, false), -- Napkin (2)
('c1e12345-0002-4bc6-849c-f9e4034876b0', 'a1e12345-0005-4bc6-849c-f9e4034876b0', 1, false), -- Retractor
('c1e12345-0002-4bc6-849c-f9e4034876b0', 'a1e12345-0004-4bc6-849c-f9e4034876b0', 6, false); -- Cotton Roll (6)

-- ORTHO-003 (Separator Placement)
INSERT INTO treatment_consumables (treatment_id, stock_item_id, quantity_used, is_optional) VALUES
('c1e12345-0003-4bc6-849c-f9e4034876b0', 'a1e12345-0001-4bc6-849c-f9e4034876b0', 1, false), -- Glove Nitrile
('c1e12345-0003-4bc6-849c-f9e4034876b0', 'a1e12345-0002-4bc6-849c-f9e4034876b0', 1, false), -- Mask
('c1e12345-0003-4bc6-849c-f9e4034876b0', 'a1e12345-0007-4bc6-849c-f9e4034876b0', 8, false), -- Separator (8)
('c1e12345-0003-4bc6-849c-f9e4034876b0', 'a1e12345-0004-4bc6-849c-f9e4034876b0', 4, false), -- Cotton Roll (4)
('c1e12345-0003-4bc6-849c-f9e4034876b0', 'a1e12345-0008-4bc6-849c-f9e4034876b0', 30, false), -- Dental Floss (30cm)
('c1e12345-0003-4bc6-849c-f9e4034876b0', 'a1e12345-0003-4bc6-849c-f9e4034876b0', 1, false); -- Napkin

-- ORTHO-004 (Molar Band Fitting)
INSERT INTO treatment_consumables (treatment_id, stock_item_id, quantity_used, is_optional) VALUES
('c1e12345-0004-4bc6-849c-f9e4034876b0', 'a1e12345-0001-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0004-4bc6-849c-f9e4034876b0', 'a1e12345-0002-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0004-4bc6-849c-f9e4034876b0', 'a1e12345-0009-4bc6-849c-f9e4034876b0', 1, false), -- Molar band
('c1e12345-0004-4bc6-849c-f9e4034876b0', 'a1e12345-0010-4bc6-849c-f9e4034876b0', 0.5, false), -- Adhesive (0.5ml)
('c1e12345-0004-4bc6-849c-f9e4034876b0', 'a1e12345-0011-4bc6-849c-f9e4034876b0', 4, false), -- Gauze (4)
('c1e12345-0004-4bc6-849c-f9e4034876b0', 'a1e12345-0004-4bc6-849c-f9e4034876b0', 6, false), -- Cotton Roll (6)
('c1e12345-0004-4bc6-849c-f9e4034876b0', 'a1e12345-0012-4bc6-849c-f9e4034876b0', 1, false), -- Saliva Ejector
('c1e12345-0004-4bc6-849c-f9e4034876b0', 'a1e12345-0003-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0004-4bc6-849c-f9e4034876b0', 'a1e12345-0013-4bc6-849c-f9e4034876b0', 2, false); -- Articulating Paper (2)

-- ORTHO-005 (Full Bracket Bonding Metal)
INSERT INTO treatment_consumables (treatment_id, stock_item_id, quantity_used, is_optional) VALUES
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0001-4bc6-849c-f9e4034876b0', 2, false), -- Nitrile Glove (2 pairs)
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0002-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0015-4bc6-849c-f9e4034876b0', 20, false), -- Metal Bracket (20)
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0010-4bc6-849c-f9e4034876b0', 2, false), -- Adhesive Primer (2ml)
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0017-4bc6-849c-f9e4034876b0', 1.5, false), -- Etch Gel (1.5ml)
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0018-4bc6-849c-f9e4034876b0', 2, false), -- Composite Paste (2g)
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0019-4bc6-849c-f9e4034876b0', 20, false), -- Elastic Ligature (20)
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0005-4bc6-849c-f9e4034876b0', 1, false), -- Retractor
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0012-4bc6-849c-f9e4034876b0', 2, false), -- Saliva Ejector (2)
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0028-4bc6-849c-f9e4034876b0', 1, false), -- Suction Tip
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0011-4bc6-849c-f9e4034876b0', 6, false), -- Gauze (6)
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0004-4bc6-849c-f9e4034876b0', 10, false), -- Cotton Roll (10)
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0003-4bc6-849c-f9e4034876b0', 2, false),
('c1e12345-0005-4bc6-849c-f9e4034876b0', 'a1e12345-0013-4bc6-849c-f9e4034876b0', 4, false); -- Articulating Paper (4)

-- ORTHO-006 (Full Bracket Bonding Ceramic)
INSERT INTO treatment_consumables (treatment_id, stock_item_id, quantity_used, is_optional) VALUES
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0001-4bc6-849c-f9e4034876b0', 2, false),
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0002-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0016-4bc6-849c-f9e4034876b0', 20, false), -- Ceramic Bracket (20)
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0010-4bc6-849c-f9e4034876b0', 2, false),
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0017-4bc6-849c-f9e4034876b0', 1.5, false),
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0018-4bc6-849c-f9e4034876b0', 2, false),
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0019-4bc6-849c-f9e4034876b0', 20, false),
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0005-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0012-4bc6-849c-f9e4034876b0', 2, false),
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0028-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0011-4bc6-849c-f9e4034876b0', 6, false),
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0004-4bc6-849c-f9e4034876b0', 10, false),
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0003-4bc6-849c-f9e4034876b0', 2, false),
('c1e12345-0006-4bc6-849c-f9e4034876b0', 'a1e12345-0013-4bc6-849c-f9e4034876b0', 4, false);

-- ORTHO-007 (Arch Wire Placement / Change)
INSERT INTO treatment_consumables (treatment_id, stock_item_id, quantity_used, is_optional) VALUES
('c1e12345-0007-4bc6-849c-f9e4034876b0', 'a1e12345-0001-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0007-4bc6-849c-f9e4034876b0', 'a1e12345-0002-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0007-4bc6-849c-f9e4034876b0', 'a1e12345-0020-4bc6-849c-f9e4034876b0', 1, false), -- Arch Wire (1)
('c1e12345-0007-4bc6-849c-f9e4034876b0', 'a1e12345-0019-4bc6-849c-f9e4034876b0', 20, false), -- Ligatures (20)
('c1e12345-0007-4bc6-849c-f9e4034876b0', 'a1e12345-0004-4bc6-849c-f9e4034876b0', 4, false),
('c1e12345-0007-4bc6-849c-f9e4034876b0', 'a1e12345-0003-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0007-4bc6-849c-f9e4034876b0', 'a1e12345-0011-4bc6-849c-f9e4034876b0', 2, false);

-- ORTHO-008 (Activation / Adjustment Visit)
INSERT INTO treatment_consumables (treatment_id, stock_item_id, quantity_used, is_optional) VALUES
('c1e12345-0008-4bc6-849c-f9e4034876b0', 'a1e12345-0001-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0008-4bc6-849c-f9e4034876b0', 'a1e12345-0002-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0008-4bc6-849c-f9e4034876b0', 'a1e12345-0019-4bc6-849c-f9e4034876b0', 20, false),
('c1e12345-0008-4bc6-849c-f9e4034876b0', 'a1e12345-0029-4bc6-849c-f9e4034876b0', 10, true), -- Power Chain (10cm) - optional
('c1e12345-0008-4bc6-849c-f9e4034876b0', 'a1e12345-0004-4bc6-849c-f9e4034876b0', 4, false),
('c1e12345-0008-4bc6-849c-f9e4034876b0', 'a1e12345-0003-4bc6-849c-f9e4034876b0', 1, false);

-- ORTHO-009 (Local Anesthesia Administration)
INSERT INTO treatment_consumables (treatment_id, stock_item_id, quantity_used, is_optional) VALUES
('c1e12345-0009-4bc6-849c-f9e4034876b0', 'a1e12345-0001-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0009-4bc6-849c-f9e4034876b0', 'a1e12345-0002-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0009-4bc6-849c-f9e4034876b0', 'a1e12345-0023-4bc6-849c-f9e4034876b0', 1, false), -- Syringe 5ml
('c1e12345-0009-4bc6-849c-f9e4034876b0', 'a1e12345-0022-4bc6-849c-f9e4034876b0', 1, false), -- Needle
('c1e12345-0009-4bc6-849c-f9e4034876b0', 'a1e12345-0014-4bc6-849c-f9e4034876b0', 5, false), -- Lidocaine 2% (5ml)
('c1e12345-0009-4bc6-849c-f9e4034876b0', 'a1e12345-0024-4bc6-849c-f9e4034876b0', 1, false), -- Topical Gel (1ml)
('c1e12345-0009-4bc6-849c-f9e4034876b0', 'a1e12345-0004-4bc6-849c-f9e4034876b0', 4, false),
('c1e12345-0009-4bc6-849c-f9e4034876b0', 'a1e12345-0011-4bc6-849c-f9e4034876b0', 2, false),
('c1e12345-0009-4bc6-849c-f9e4034876b0', 'a1e12345-0003-4bc6-849c-f9e4034876b0', 1, false);

-- ORTHO-010 (Bracket Removal - Debonding)
INSERT INTO treatment_consumables (treatment_id, stock_item_id, quantity_used, is_optional) VALUES
('c1e12345-0010-4bc6-849c-f9e4034876b0', 'a1e12345-0001-4bc6-849c-f9e4034876b0', 2, false),
('c1e12345-0010-4bc6-849c-f9e4034876b0', 'a1e12345-0002-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0010-4bc6-849c-f9e4034876b0', 'a1e12345-0011-4bc6-849c-f9e4034876b0', 8, false),
('c1e12345-0010-4bc6-849c-f9e4034876b0', 'a1e12345-0004-4bc6-849c-f9e4034876b0', 10, false),
('c1e12345-0010-4bc6-849c-f9e4034876b0', 'a1e12345-0028-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0010-4bc6-849c-f9e4034876b0', 'a1e12345-0012-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0010-4bc6-849c-f9e4034876b0', 'a1e12345-0025-4bc6-849c-f9e4034876b0', 5, false), -- Fluoride (5ml)
('c1e12345-0010-4bc6-849c-f9e4034876b0', 'a1e12345-0003-4bc6-849c-f9e4034876b0', 2, false);

-- ORTHO-011 (Retainer Fitting - Removable)
INSERT INTO treatment_consumables (treatment_id, stock_item_id, quantity_used, is_optional) VALUES
('c1e12345-0011-4bc6-849c-f9e4034876b0', 'a1e12345-0001-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0011-4bc6-849c-f9e4034876b0', 'a1e12345-0002-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0011-4bc6-849c-f9e4034876b0', 'a1e12345-0006-4bc6-849c-f9e4034876b0', 40, false), -- Alginate (40g)
('c1e12345-0011-4bc6-849c-f9e4034876b0', 'a1e12345-0026-4bc6-849c-f9e4034876b0', 5, false), -- Bite Registration Silicone (5ml)
('c1e12345-0011-4bc6-849c-f9e4034876b0', 'a1e12345-0005-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0011-4bc6-849c-f9e4034876b0', 'a1e12345-0004-4bc6-849c-f9e4034876b0', 6, false),
('c1e12345-0011-4bc6-849c-f9e4034876b0', 'a1e12345-0003-4bc6-849c-f9e4034876b0', 1, false);

-- ORTHO-012 (Fixed Retainer Placement)
INSERT INTO treatment_consumables (treatment_id, stock_item_id, quantity_used, is_optional) VALUES
('c1e12345-0012-4bc6-849c-f9e4034876b0', 'a1e12345-0001-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0012-4bc6-849c-f9e4034876b0', 'a1e12345-0002-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0012-4bc6-849c-f9e4034876b0', 'a1e12345-0027-4bc6-849c-f9e4034876b0', 6, false), -- Retainer Wire (6cm)
('c1e12345-0012-4bc6-849c-f9e4034876b0', 'a1e12345-0018-4bc6-849c-f9e4034876b0', 1, false), -- Composite Paste (1g)
('c1e12345-0012-4bc6-849c-f9e4034876b0', 'a1e12345-0010-4bc6-849c-f9e4034876b0', 0.5, false), -- Adhesive (0.5ml)
('c1e12345-0012-4bc6-849c-f9e4034876b0', 'a1e12345-0017-4bc6-849c-f9e4034876b0', 0.5, false), -- Etch Gel (0.5ml)
('c1e12345-0012-4bc6-849c-f9e4034876b0', 'a1e12345-0008-4bc6-849c-f9e4034876b0', 20, false), -- Dental Floss (20cm)
('c1e12345-0012-4bc6-849c-f9e4034876b0', 'a1e12345-0012-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0012-4bc6-849c-f9e4034876b0', 'a1e12345-0004-4bc6-849c-f9e4034876b0', 6, false),
('c1e12345-0012-4bc6-849c-f9e4034876b0', 'a1e12345-0011-4bc6-849c-f9e4034876b0', 4, false),
('c1e12345-0012-4bc6-849c-f9e4034876b0', 'a1e12345-0003-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0012-4bc6-849c-f9e4034876b0', 'a1e12345-0013-4bc6-849c-f9e4034876b0', 2, false);

-- ORTHO-013 (Dental Cleaning)
INSERT INTO treatment_consumables (treatment_id, stock_item_id, quantity_used, is_optional) VALUES
('c1e12345-0013-4bc6-849c-f9e4034876b0', 'a1e12345-0001-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0013-4bc6-849c-f9e4034876b0', 'a1e12345-0002-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0013-4bc6-849c-f9e4034876b0', 'a1e12345-0025-4bc6-849c-f9e4034876b0', 3, false), -- Fluoride (3ml)
('c1e12345-0013-4bc6-849c-f9e4034876b0', 'a1e12345-0012-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0013-4bc6-849c-f9e4034876b0', 'a1e12345-0028-4bc6-849c-f9e4034876b0', 1, false),
('c1e12345-0013-4bc6-849c-f9e4034876b0', 'a1e12345-0011-4bc6-849c-f9e4034876b0', 4, false),
('c1e12345-0013-4bc6-849c-f9e4034876b0', 'a1e12345-0004-4bc6-849c-f9e4034876b0', 6, false),
('c1e12345-0013-4bc6-849c-f9e4034876b0', 'a1e12345-0003-4bc6-849c-f9e4034876b0', 1, false);
