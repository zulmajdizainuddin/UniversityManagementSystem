package com.project.dao;

import com.project.model.Grade;
import java.sql.*;
import java.util.*;

public class GradeDAO extends BaseDAO {

    public void addOrUpdateGrade(Grade grade) throws Exception {
        String checkSql = "SELECT grade_id FROM grades WHERE student_id = ? AND subject_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            checkPs.setInt(1, grade.getStudentId());
            checkPs.setInt(2, grade.getSubjectId());

            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    int gradeId = rs.getInt("grade_id");
                    String updateSql = "UPDATE grades SET score = ?, lecturer_id = ? WHERE grade_id = ?";
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setDouble(1, grade.getScore());
                        updatePs.setInt(2, grade.getLecturerId());
                        updatePs.setInt(3, gradeId);
                        updatePs.executeUpdate();
                    }
                } else {
                    String insertSql = "INSERT INTO grades (student_id, subject_id, score, lecturer_id) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setInt(1, grade.getStudentId());
                        insertPs.setInt(2, grade.getSubjectId());
                        insertPs.setDouble(3, grade.getScore());
                        insertPs.setInt(4, grade.getLecturerId());
                        insertPs.executeUpdate();
                    }
                }
            }
        }
    }

    public List<Grade> getGradesBySubject(int subjectId) throws Exception {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT g.grade_id, g.student_id, g.subject_id, g.score, g.lecturer_id, s.subject_name "
                + "FROM grades g JOIN subjects s ON g.subject_id = s.subject_id WHERE g.subject_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Grade g = new Grade();
                    g.setGradeId(rs.getInt("grade_id"));
                    g.setStudentId(rs.getInt("student_id"));
                    g.setSubjectId(rs.getInt("subject_id"));
                    g.setScore(rs.getDouble("score"));
                    g.setLecturerId(rs.getInt("lecturer_id"));
                    g.setSubjectName(rs.getString("subject_name"));
                    list.add(g);
                }
            }
        }
        return list;
    }

    public List<Grade> getGradesByStudent(int studentId) throws Exception {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT g.grade_id, g.student_id, g.subject_id, g.score, g.lecturer_id, s.subject_code, s.subject_name "
                + "FROM grades g JOIN subjects s ON g.subject_id = s.subject_id "
                + "WHERE g.student_id = ? "
                + "ORDER BY s.subject_name";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Grade g = new Grade();
                    g.setGradeId(rs.getInt("grade_id"));
                    g.setStudentId(rs.getInt("student_id"));
                    g.setSubjectId(rs.getInt("subject_id"));
                    g.setScore(rs.getDouble("score"));
                    g.setLecturerId(rs.getInt("lecturer_id"));
                    g.setSubjectCode(rs.getString("subject_code"));
                    g.setSubjectName(rs.getString("subject_name"));
                    list.add(g);
                }
            }
        }
        return list;
    }
}
