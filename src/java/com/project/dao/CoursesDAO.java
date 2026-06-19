/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.dao;

import com.project.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ZULMAJDI
 */
public class CoursesDAO extends BaseDAO {

    public List<Course> getAllCourses() throws Exception {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_id";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("course_id"));
                c.setCourseName(rs.getString("course_name"));
                c.setCreatedAt(rs.getTimestamp("created_at"));
                c.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(c);
            }
        }
        return list;
    }

    public Course getCourseById(int courseId) throws Exception {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Course c = new Course();
                    c.setCourseId(rs.getInt("course_id"));
                    c.setCourseName(rs.getString("course_name"));
                    c.setCreatedAt(rs.getTimestamp("created_at"));
                    c.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return c;
                }
            }
        }
        return null;
    }

    public void addCourse(Course course) throws Exception {
        String sql = "INSERT INTO courses (course_name) VALUES (?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getCourseName());
            ps.executeUpdate();
        }
    }

    public void updateCourse(Course course) throws Exception {
        String sql = "UPDATE courses SET course_name = ?, updated_at = CURRENT_TIMESTAMP WHERE course_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getCourseName());
            ps.setInt(2, course.getCourseId());
            ps.executeUpdate();
        }
    }

    public void deleteCourse(int courseId) throws Exception {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
    }

    public String getCourseNameByStudentId(int studentId) throws Exception {
        String sql = "SELECT c.course_name FROM courses c "
                + "JOIN students s ON s.major = c.course_name "
                + "WHERE s.user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("course_name");
                }
            }
        }
        return "N/A";
    }
}
