-- V6__fix_stock_category_enums.sql

-- Update all existing categories to exact uppercase matching the StockCategory enum values.
UPDATE stock_items 
SET category = 'HYGIENE' 
WHERE LOWER(category) = 'hygiene';

UPDATE stock_items 
SET category = 'INSTRUMENTS' 
WHERE LOWER(category) = 'instruments';

UPDATE stock_items 
SET category = 'ORTHODONTIC_PARTS' 
WHERE LOWER(category) IN ('brackets', 'wires', 'bands', 'orthodontic_parts', 'orthodontic');

UPDATE stock_items 
SET category = 'ANESTHESIA' 
WHERE LOWER(category) = 'anesthesia';

UPDATE stock_items 
SET category = 'MEDICATION' 
WHERE LOWER(category) = 'medication';

-- Anything else falls back to CONSUMABLES
UPDATE stock_items 
SET category = 'CONSUMABLES' 
WHERE category NOT IN ('HYGIENE', 'INSTRUMENTS', 'ORTHODONTIC_PARTS', 'ANESTHESIA', 'MEDICATION', 'CONSUMABLES');
