-- Migration to split full_name into first_name and last_name

-- Add new columns
ALTER TABLE drivers ADD COLUMN first_name VARCHAR(100);
ALTER TABLE drivers ADD COLUMN last_name VARCHAR(100);

-- Migrate data: split full_name into first_name and last_name
-- Assumes full_name contains at least one space separating first and last name
UPDATE drivers 
SET first_name = SPLIT_PART(full_name, ' ', 1),
    last_name = SUBSTRING(full_name FROM POSITION(' ' IN full_name) + 1)
WHERE full_name IS NOT NULL AND full_name LIKE '% %';

-- Handle cases where full_name doesn't contain a space (only first name provided)
UPDATE drivers 
SET first_name = full_name,
    last_name = ''
WHERE full_name IS NOT NULL AND full_name NOT LIKE '% %';

-- Make columns NOT NULL after data migration
ALTER TABLE drivers ALTER COLUMN first_name SET NOT NULL;
ALTER TABLE drivers ALTER COLUMN last_name SET NOT NULL;

-- Drop the old column
ALTER TABLE drivers DROP COLUMN full_name;
