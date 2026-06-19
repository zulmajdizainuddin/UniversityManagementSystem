-- Users table: common info for all users
CREATE TABLE users (
  user_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL, -- hashed passwords
  role VARCHAR(20) NOT NULL CHECK (role IN ('Admin', 'Lecturer', 'Student')),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Students table: student-specific info
CREATE TABLE students (
  user_id INT PRIMARY KEY,
  student_number VARCHAR(50) UNIQUE,
  major VARCHAR(100),
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Staff table: lecturer/admin-specific info
CREATE TABLE staff (
  user_id INT PRIMARY KEY,
  staff_number VARCHAR(50) UNIQUE,
  department VARCHAR(100),
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Courses table
CREATE TABLE courses (
  course_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  course_name VARCHAR(100) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Subjects table linked to courses, with subject_code
CREATE TABLE subjects (
  subject_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  subject_code VARCHAR(20) NOT NULL UNIQUE,
  subject_name VARCHAR(100) NOT NULL,
  course_id INT NOT NULL,
  FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

-- Classes table linked to subjects (not courses)
CREATE TABLE classes (
  class_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  class_name VARCHAR(100) NOT NULL,
  subject_id INT NOT NULL,
  FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE
);

-- Link lecturers to classes they teach
CREATE TABLE lecturer_classes (
  lecturer_id INT NOT NULL,
  class_id INT NOT NULL,
  PRIMARY KEY (lecturer_id, class_id),
  FOREIGN KEY (lecturer_id) REFERENCES staff(user_id) ON DELETE CASCADE,
  FOREIGN KEY (class_id) REFERENCES classes(class_id) ON DELETE CASCADE
);

-- Link lecturers to subjects they teach
CREATE TABLE lecturer_subjects (
  lecturer_id INT NOT NULL,
  subject_id INT NOT NULL,
  PRIMARY KEY (lecturer_id, subject_id),
  FOREIGN KEY (lecturer_id) REFERENCES staff(user_id) ON DELETE CASCADE,
  FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE
);

-- Attendance status lookup
CREATE TABLE attendance_status (
  status VARCHAR(10) PRIMARY KEY
);

-- Attendance table with lecturer tracking and subject_id included
CREATE TABLE attendance (
  attendance_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  student_id INT NOT NULL,
  class_id INT NOT NULL,
  subject_id INT NOT NULL,
  date DATE NOT NULL,
  status VARCHAR(10) NOT NULL,
  lecturer_id INT NOT NULL,
  FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
  FOREIGN KEY (class_id) REFERENCES classes(class_id) ON DELETE CASCADE,
  FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE,
  FOREIGN KEY (status) REFERENCES attendance_status(status),
  FOREIGN KEY (lecturer_id) REFERENCES staff(user_id) ON DELETE CASCADE,
  UNIQUE (student_id, class_id, subject_id, date)
);

-- Grades table with lecturer tracking
CREATE TABLE grades (
  grade_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  student_id INT NOT NULL,
  subject_id INT NOT NULL,
  score DECIMAL(5,2) NOT NULL CHECK (score >= 0 AND score <= 100),
  lecturer_id INT NOT NULL,
  FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
  FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE,
  FOREIGN KEY (lecturer_id) REFERENCES staff(user_id) ON DELETE CASCADE,
  UNIQUE (student_id, subject_id)
);

CREATE TABLE student_classes (
  student_id INT NOT NULL,
  class_id INT NOT NULL,
  PRIMARY KEY (student_id, class_id),
  FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
  FOREIGN KEY (class_id) REFERENCES classes(class_id) ON DELETE CASCADE
);

CREATE TABLE student_subjects (
  student_id INT NOT NULL,
  subject_id INT NOT NULL,
  PRIMARY KEY (student_id, subject_id),
  FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
  FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE
);
-- ============================================================
-- COURSE EVALUATION TABLES (dynamic evaluation system)
-- ============================================================

-- Evaluation question bank (Rating and Text question types)
CREATE TABLE evaluation_question (
  question_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
  question_text VARCHAR(500) NOT NULL,
  question_type VARCHAR(10) NOT NULL CHECK (question_type IN ('RATING', 'TEXT')),
  is_required SMALLINT NOT NULL DEFAULT 1,
  is_active SMALLINT NOT NULL DEFAULT 1,
  display_order INTEGER NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_display_order ON evaluation_question(display_order);

-- One record per student-subject evaluation submission
CREATE TABLE course_evaluation_header (
  evaluation_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
  student_id INTEGER NOT NULL,
  subject_id INTEGER NOT NULL,
  lecturer_id INTEGER NOT NULL,
  submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (student_id) REFERENCES users(user_id),
  FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
  FOREIGN KEY (lecturer_id) REFERENCES users(user_id)
);

CREATE UNIQUE INDEX idx_unique_evaluation_header ON course_evaluation_header(student_id, subject_id);

-- Individual answers per question per submission
CREATE TABLE course_evaluation_answer (
  answer_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
  evaluation_id INTEGER NOT NULL,
  question_id INTEGER NOT NULL,
  rating_value INTEGER CHECK (rating_value >= 1 AND rating_value <= 5),
  text_value CLOB,
  FOREIGN KEY (evaluation_id) REFERENCES course_evaluation_header(evaluation_id) ON DELETE CASCADE,
  FOREIGN KEY (question_id) REFERENCES evaluation_question(question_id)
);

CREATE INDEX idx_evaluation_id ON course_evaluation_answer(evaluation_id);
