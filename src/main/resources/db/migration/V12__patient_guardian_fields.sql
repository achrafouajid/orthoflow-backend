-- Most orthodontic patients are minors; there was nowhere to record the
-- responsible adult (audit VIII.4 / IV.3).
ALTER TABLE patients ADD COLUMN guardian_name VARCHAR(200);
ALTER TABLE patients ADD COLUMN guardian_phone VARCHAR(30);
