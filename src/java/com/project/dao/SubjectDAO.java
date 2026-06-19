/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.dao;

import com.project.model.Subject;
import java.sql.*;
import java.util.ArrayList;

import java.util.List;

/**
 *
 * @author ZULMAJDI
 */
public class SubjectDAO extends BaseDAO {

    public List<Subject> getAllSubjects() throws Exception {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT subject_id, subject_code, subject_name, course_id FROM subjects ORDER BY subject_name";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Subject subject = new Subject();
                subject.setSubjectId(rs.getInt("subject_id"));
                subject.setSubjectCode(rs.getString("subject_code"));  // Important!
                subject.setSubjectName(rs.getString("subject_name"));
                subject.setCourseId(rs.getInt("course_id"));
                subjects.add(subject);
            }
        }
        return subjects;
    }

    public Subject getSubjectById(int id) throws Exception {
        String sql = "SELECT * FROM subjects WHERE subject_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Subject s = new Subject();
                    s.setSubjectId(rs.getInt("subject_id"));
                    s.setSubjectCode(rs.getString("subject_code"));
                    s.setSubjectName(rs.getString("subject_name"));
                    s.setCourseId(rs.getInt("course_id"));
                    return s;
                }
            }
        }
        return null;
    }

    public void addSubject(Subject subject) throws Exception {
        String sql = "INSERT INTO subjects (subject_code, subject_name, course_id) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subject.getSubjectCode());
            ps.setString(2, subject.getSubjectName());
            ps.setInt(3, subject.getCourseId());
            ps.executeUpdate();
        }
    }

    public void updateSubject(Subject subject) throws Exception {
        String sql = "UPDATE subjects SET subject_code=?, subject_name=?, course_id=? WHERE subject_id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subject.getSubjectCode());
            ps.setString(2, subject.getSubjectName());
            ps.setInt(3, subject.getCourseId());
            ps.setInt(4, subject.getSubjectId());
            ps.executeUpdate();
        }
    }

    public void deleteSubject(int id) throws Exception {
        String sql = "DELETE FROM subjects WHERE subject_id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Subject> getSubjectsByLecturer(int lecturerId) throws Exception {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT DISTINCT s.subject_id, s.subject_name "
                + "FROM subjects s "
                + "JOIN classes c ON s.subject_id = c.subject_id "
                + "JOIN lecturer_classes lc ON c.class_id = lc.class_id "
                + "WHERE lc.lecturer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Subject subject = new Subject();
                    subject.setSubjectId(rs.getInt("subject_id"));
                    subject.setSubjectName(rs.getString("subject_name"));
                    subjects.add(subject);
                }
            }
        }
        return subjects;
    }

    public int countSubjectsByLecturer(int lecturerId) throws Exception {
        String sql = "SELECT COUNT(*) FROM lecturer_subjects WHERE lecturer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // SubjectDAO.java
    public List<Subject> getEnrolledSubjectsByStudent(int studentId) throws Exception {
        String sql = "SELECT s.subject_id, s.subject_name, s.course_id FROM subjects s "
                + "JOIN student_subjects ss ON s.subject_id = ss.subject_id "
                + "WHERE ss.student_id = ?";
        List<Subject> subjects = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Subject s = new Subject();
                    s.setSubjectId(rs.getInt("subject_id"));
                    s.setSubjectName(rs.getString("subject_name"));
                    s.setCourseId(rs.getInt("course_id"));
                    subjects.add(s);
                }
            }
        }
        return subjects;
    }
}
