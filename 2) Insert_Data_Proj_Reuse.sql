-- 1. Users (Admin, Lecturers, Students)
INSERT INTO users (name, email, password, role) VALUES
('Ahmad Haris Admin', 'admin@gmail.com', '$2a$12$OfqAIM7OJoStgK6hqjlDROX2wZKwCIgRfDf7YNlQsa/TMa0ehveD6', 'Admin'),
('Nurin Harisa Admin', 'admin2@gmail.com', '$2a$12$DITSVret.X/lP8EnSpCJs.N1o4h.BnR3T7tWwrpUe/WrADmsCiIeS', 'Admin'),
('Dr Syarafana Lecturer', 'lecturer@gmail.com', '$2a$12$VE6OgYjnRGbQI0JguxxW/.yo4bSkdkPF48dLhJfBELJ702I0UF6Ye', 'Lecturer'),
('Dr Faiz Lecturer', 'lecturer2@gmail.com', '$2a$12$zSeQ9UTKqLjzYhzcp.RYfOh5jtYtE8Ayx1FHM3VirZjKIJdYdZ7Ei', 'Lecturer'),
('Nur Sazahah Student', 'student@gmail.com', '$2a$12$KL2sTftGi46VVdhaTEyC8uZsyHaCBbiVJ73iLIz/F.soiFUR7cFem', 'Student'),
('Nurin Nabeeha Student', 'student2@gmail.com', '$2a$12$3DiTSgJtUe/uW4bUPa8jwuksklPjx8RauDIgPE.ZGm22Xg2d8FhJG', 'Student'),
('Nurul Ain Adlina Student', 'student3@gmail.com', '$2a$12$v9XCwXAtJH7Tte6DYkXeOuCEaUXOsPSESeZUpiOXI5zQYqIjUEoLi', 'Student'),
('Zulmajdi Student', 'student4@gmail.com', '$2a$12$j3vN6orqPXM.gXMz7U3zDOq8sblwiT1yKusAkQpVjSEBvaymSgdX.', 'Student');

-- 2. Staff details (Lecturers and Admin)
INSERT INTO staff (user_id, staff_number, department) VALUES
(1, 'STAFF001', 'Administration'),
(2, 'STAFF002', 'Administration'),
(3, 'STAFF003', 'Software Engineering'),
(4, 'STAFF004', 'Networking System');

-- 3. Students details
INSERT INTO students (user_id, student_number, major) VALUES
(5, '52213223101', 'Bachelor of Networking System'),
(6, '52213223102', 'Bachelor of Software Engineering'),
(7, '52213223103', 'Bachelor of Software Engineering'),
(8, '52213223104', 'Bachelor of Networking System');

-- 4. Courses
INSERT INTO courses (course_name) VALUES
('Bachelor of Networking System'),
('Bachelor of Software Engineering');

-- 5. Subjects linked to courses
INSERT INTO subjects (subject_code, subject_name, course_id) VALUES
('NS101', 'Introduction to Programming', 1),
('NS102', 'Information Systems Fundamentals', 2),
('SE101', 'Data Structures', 2),
('SE102', 'Database Systems', 2);

-- 6. Classes linked to subjects
INSERT INTO classes (class_name, subject_id) VALUES
('NS101 - Class A', 1),
('NS101 - Class B', 1),
('NS102 - Class A', 2),
('SE101 - Class A', 3),
('SE102 - Class A', 4);

-- 7. Lecturer assigned to classes
INSERT INTO lecturer_classes (lecturer_id, class_id) VALUES
(4, 1),  -- Dr Faiz teaches NS101 Class A
(4, 2),  -- Dr Faiz teaches NS101 Class B
(4, 3),  -- Dr Faiz teaches NS102 Class A
(3, 4),  -- Dr Syarafana teaches SE101 Class A
(3, 5);  -- Dr Syarafana teaches SE102 Class A

-- 8. Lecturer assigned to subjects
INSERT INTO lecturer_subjects (lecturer_id, subject_id) VALUES
(4, 1),  -- Dr Faiz teaches Intro to Programming
(4, 2),  -- Dr Faiz teaches Information Systems Fundamentals
(3, 3),  -- Dr Syarafana teaches Data Structures
(3, 4);  -- Dr Syarafana teaches Database Systems

-- 9. Student enrolled in classes
INSERT INTO student_classes (student_id, class_id) VALUES
(5, 1),  -- Sazahah in NS101 Class A
(8, 2),  -- Zulmajdi in NS101 Class B
(8, 3),  -- Zulmajdi in NS102 Class A
(6, 4),  -- Nabeeha in SE101 Class A
(7, 5);  -- Ain in SE102 Class A

-- 10. Student enrolled in subjects
INSERT INTO student_subjects (student_id, subject_id) VALUES
(5, 1),  -- Sazahah enrolled in NS101
(8, 1),  -- Zulmajdi enrolled in NS101
(8, 2),  -- Zulmajdi enrolled in NS102
(6, 3),  -- Nabeeha enrolled in SE101
(7, 4);  -- Ain enrolled in SE102

-- 11. Attendance status lookup
INSERT INTO attendance_status (STATUS) VALUES
('Present'), ('Absent');

-- 12. Attendance records
INSERT INTO attendance (student_id, class_id, subject_id, date, status, lecturer_id) VALUES
(5, 1, 1, DATE('2025-06-01'), 'Present', 4),
(8, 2, 1, DATE('2025-06-01'), 'Absent', 4),
(8, 3, 2, DATE('2025-06-01'), 'Present', 4),
(6, 4, 3, DATE('2025-06-01'), 'Present', 3),
(7, 5, 4, DATE('2025-06-01'), 'Absent', 3);

-- 13. Grades records
INSERT INTO grades (student_id, subject_id, score, lecturer_id) VALUES
(5, 1, 85.50, 4),  -- Sazahah scored 85.5 in Intro to Programming
(8, 1, 90.00, 4),  -- Zulmajdi scored 90.0 in Intro to Programming
(8, 2, 78.50, 4),  -- Zulmajdi scored 78.5 in Information Systems Fundamentals
(6, 3, 88.00, 3),  -- Nabeeha scored 88.0 in Data Structures
(7, 4, 80.00, 3);  -- Ain scored 80.0 in Database Systems
