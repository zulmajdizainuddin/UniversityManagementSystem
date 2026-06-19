/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.dao;

import com.project.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nabil
 */
public class StudentDAO extends BaseDAO {

    // Fetch all students with student_number
    public List<Student> getAllStudents() throws Exception {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT u.user_id, u.name, u.email, s.student_number, s.major "
                + "FROM users u JOIN students s ON u.user_id = s.user_id "
                + "WHERE u.role = 'Student' ORDER BY u.name";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student student = new Student();
                student.setUserId(rs.getInt("user_id"));
                student.setName(rs.getString("name"));
                student.setEmail(rs.getString("email"));
                student.setStudentNumber(rs.getString("student_number"));
                student.setMajor(rs.getString("major"));
                students.add(student);
            }
        }
        return students;
    }

    // Assign student to subject, checking for duplicates
    public void assignStudentToSubject(int studentId, int subjectId) throws Exception {
        String checkSql = "SELECT COUNT(*) FROM student_subjects WHERE student_id = ? AND subject_id = ?";
        String insertSql = "INSERT INTO student_subjects (student_id, subject_id) VALUES (?, ?)";

        try (Connection conn = getConnection(); PreparedStatement checkStmt = conn.prepareStatement(checkSql); PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            checkStmt.setInt(1, studentId);
            checkStmt.setInt(2, subjectId);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    insertStmt.setInt(1, studentId);
                    insertStmt.setInt(2, subjectId);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    // Assign student to class, checking for duplicates
    public void assignStudentToClass(int studentId, int classId) throws Exception {
        String checkSql = "SELECT COUNT(*) FROM student_classes WHERE student_id = ? AND class_id = ?";
        String insertSql = "INSERT INTO student_classes (student_id, class_id) VALUES (?, ?)";

        try (Connection conn = getConnection(); PreparedStatement checkStmt = conn.prepareStatement(checkSql); PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            checkStmt.setInt(1, studentId);
            checkStmt.setInt(2, classId);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    insertStmt.setInt(1, studentId);
                    insertStmt.setInt(2, classId);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    public List<Student> getStudentsByClass(int classId) throws Exception {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT u.user_id, u.name, u.email, s.student_number, s.major "
                + "FROM users u "
                + "JOIN students s ON u.user_id = s.user_id "
                + "JOIN student_classes sc ON sc.student_id = u.user_id "
                + "WHERE sc.class_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, classId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setUserId(rs.getInt("user_id"));
                    student.setName(rs.getString("name"));
                    student.setEmail(rs.getString("email"));
                    student.setStudentNumber(rs.getString("student_number"));
                    student.setMajor(rs.getString("major"));
                    students.add(student);
                }
            }
        }
        return students;
    }
}
