-- SQL DDL Script for Course Evaluation Table
-- Database: Apache Derby/JavaDB
-- Run this script to create the COURSE_EVALUATION table

CREATE TABLE course_evaluation (
    evaluation_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    student_id INTEGER NOT NULL,
    subject_id INTEGER NOT NULL,
    lecturer_id INTEGER NOT NULL,
    semester VARCHAR(50) NOT NULL,
    q1 SMALLINT NOT NULL CHECK (q1 >= 1 AND q1 <= 5),
    q2 SMALLINT NOT NULL CHECK (q2 >= 1 AND q2 <= 5),
    q3 SMALLINT NOT NULL CHECK (q3 >= 1 AND q3 <= 5),
    q4 SMALLINT NOT NULL CHECK (q4 >= 1 AND q4 <= 5),
    q5 SMALLINT NOT NULL CHECK (q5 >= 1 AND q5 <= 5),
    q6 SMALLINT NOT NULL CHECK (q6 >= 1 AND q6 <= 5),
    q7 SMALLINT NOT NULL CHECK (q7 >= 1 AND q7 <= 5),
    q8 SMALLINT NOT NULL CHECK (q8 >= 1 AND q8 <= 5),
    q9 SMALLINT NOT NULL CHECK (q9 >= 1 AND q9 <= 5),
    q10 SMALLINT NOT NULL CHECK (q10 >= 1 AND q10 <= 5),
    q11 SMALLINT NOT NULL CHECK (q11 >= 1 AND q11 <= 5),
    q12 SMALLINT NOT NULL CHECK (q12 >= 1 AND q12 <= 5),
    q13 SMALLINT NOT NULL CHECK (q13 >= 1 AND q13 <= 5),
    q14 SMALLINT NOT NULL CHECK (q14 >= 1 AND q14 <= 5),
    likes_comment CLOB,
    improvement_comment CLOB,
    other_comment CLOB,
    submitted_at TIMESTAMP NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(user_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    FOREIGN KEY (lecturer_id) REFERENCES users(user_id)
);

-- Create unique constraint to prevent duplicate submissions
-- One student can only submit one evaluation per subject (semester not used for duplicate check)
CREATE UNIQUE INDEX idx_unique_evaluation ON course_evaluation(student_id, subject_id);

-- Create index for lecturer queries
CREATE INDEX idx_lecturer_subject_semester ON course_evaluation(lecturer_id, subject_id, semester);

