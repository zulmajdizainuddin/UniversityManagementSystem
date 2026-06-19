/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.dao;

import com.project.model.Class;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nabil
 */
public class ClassesDAO extends BaseDAO {

    public List<Class> getAllClasses() throws Exception {
        List<Class> list = new ArrayList<>();
        String sql = "SELECT * FROM classes ORDER BY class_id";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Class c = new Class();
                c.setClassId(rs.getInt("class_id"));
                c.setClassName(rs.getString("class_name"));
                c.setSubjectId(rs.getInt("subject_id"));
                list.add(c);
            }
        }
        return list;
    }

    public Class getClassById(int classId) throws Exception {
        String sql = "SELECT * FROM classes WHERE class_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Class c = new Class();
                    c.setClassId(rs.getInt("class_id"));
                    c.setClassName(rs.getString("class_name"));
                    c.setSubjectId(rs.getInt("subject_id"));
                    return c;
                }
            }
        }
        return null;
    }

    public void addClass(Class c) throws Exception {
        String sql = "INSERT INTO classes (class_name, subject_id) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getClassName());
            ps.setInt(2, c.getSubjectId());
            ps.executeUpdate();
        }
    }

    public void updateClass(Class c) throws Exception {
        String sql = "UPDATE classes SET class_name=?, subject_id=? WHERE class_id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getClassName());
            ps.setInt(2, c.getSubjectId());
            ps.setInt(3, c.getClassId());
            ps.executeUpdate();
        }
    }

    public void deleteClass(int classId) throws Exception {
        String sql = "DELETE FROM classes WHERE class_id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.executeUpdate();
        }
    }

    public int getSubjectIdByClassId(int classId) throws Exception {
        String sql = "SELECT subject_id FROM classes WHERE class_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("subject_id");
                }
            }
        }
        // Return 0 or throw exception if not found
        throw new Exception("Subject not found for class ID: " + classId);
    }

    public List<Class> getClassesByLecturer(int lecturerId) throws Exception {
        List<Class> list = new ArrayList<>();
        String sql = "SELECT c.class_id, c.class_name, c.subject_id "
                + "FROM classes c "
                + "JOIN lecturer_classes lc ON c.class_id = lc.class_id "
                + "WHERE lc.lecturer_id = ? "
                + "ORDER BY c.class_id";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Class c = new Class();
                    c.setClassId(rs.getInt("class_id"));
                    c.setClassName(rs.getString("class_name"));
                    c.setSubjectId(rs.getInt("subject_id"));
                    list.add(c);
                }
            }
        }
        return list;
    }

    public List<Class> getClassesByLecturerAndSubject(int lecturerId, int subjectId) throws Exception {
        List<Class> list = new ArrayList<>();
        String sql = "SELECT c.class_id, c.class_name, c.subject_id "
                + "FROM classes c "
                + "JOIN lecturer_classes lc ON c.class_id = lc.class_id "
                + "WHERE lc.lecturer_id = ? AND c.subject_id = ? "
                + "ORDER BY c.class_name";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Class c = new Class();
                    c.setClassId(rs.getInt("class_id"));
                    c.setClassName(rs.getString("class_name"));
                    c.setSubjectId(rs.getInt("subject_id"));
                    list.add(c);
                }
            }
        }
        return list;
    }

    public List<Class> getEnrolledClassesByStudentAndSubject(int studentId, int subjectId) throws Exception {
        List<com.project.model.Class> classes = new ArrayList<>();
        String sql = "SELECT c.class_id, c.class_name, c.subject_id FROM classes c "
                + "JOIN student_classes sc ON c.class_id = sc.class_id "
                + "WHERE sc.student_id = ? AND c.subject_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    com.project.model.Class cls = new com.project.model.Class();
                    cls.setClassId(rs.getInt("class_id"));
                    cls.setClassName(rs.getString("class_name"));
                    cls.setSubjectId(rs.getInt("subject_id"));
                    classes.add(cls);
                }
            }
        }
        return classes;
    }

    public List<Class> getClassesBySubject(int subjectId) throws Exception {
        List<Class> classes = new ArrayList<>();
        String sql = "SELECT class_id, class_name, subject_id FROM classes WHERE subject_id = ? ORDER BY class_name";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Class c = new Class();
                    c.setClassId(rs.getInt("class_id"));
                    c.setClassName(rs.getString("class_name"));
                    c.setSubjectId(rs.getInt("subject_id"));
                    classes.add(c);
                }
            }
        }
        return classes;
    }
}
