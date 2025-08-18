ALTER TABLE student ADD CONSTRAINT chk_student_age CHECK (age >= 16);

ALTER TABLE student ALTER COLUMN name SET NOT NULL;

ALTER TABLE student ALTER COLUMN age SET DEFAULT 20;

ALTER TABLE faculty ADD CONSTRAINT uq_faculty_name_color UNIQUE (name, color);