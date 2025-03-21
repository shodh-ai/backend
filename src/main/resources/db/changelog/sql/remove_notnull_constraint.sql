-- DATE: 21-March-2025 13:00:00
-- Student table changes
ALTER TABLE student ALTER COLUMN college_email DROP NOT NULL;
ALTER TABLE student ALTER COLUMN gender_id DROP NOT NULL;
ALTER TABLE student ALTER COLUMN academic_degree_id DROP NOT NULL;
-- Faculty table changes
ALTER TABLE faculty ALTER COLUMN college_email DROP NOT NULL;
ALTER TABLE faculty ALTER COLUMN gender_id DROP NOT NULL;