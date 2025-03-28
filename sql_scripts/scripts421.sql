ALTER TABLE Student
ADD CONSTRAINT age_check CHECK (age >= 16);

ALTER TABLE Student
ADD CONSTRAINT unique_name UNIQUE (name);

ALTER TABLE Student
MODIFY name VARCHAR(255) NOT NULL;

ALTER TABLE Faculty
ADD CONSTRAINT unique_name_color UNIQUE (name, color);

ALTER TABLE Student
MODIFY age INT DEFAULT 20;